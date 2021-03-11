package com.example.r4_fry.androidnodeapp.NodeDiagram;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.r4_fry.androidnodeapp.CompanyData.CompanyDataActivity;
import  com.example.r4_fry.androidnodeapp.CompaniesDB.Officer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Custom View to display node diagram for a company with the company as the root and officers branching off
 */
public class NodeView extends View {
    int mWidth;
    int mHeight;
    int mCircleRadius;
    NodeViewModel mNodeViewModel;
    WeakReference<Node> mSelectedNode;

    // for pannning
    float mXTouchOld;
    float mYTouchOld;

    // for distinguishing click
    float mXInitialClick;
    float mYInitialClick;
    float mMoveTolerance = 1;

    // paints
    private Paint mCompanyPaint = new Paint();
    private Paint mOfficerPaint = new Paint();
    private Paint mOfficerTextPaint = new Paint();
    private Paint mLinePaint = new Paint();

    // scaling
    private ScaleGestureDetector mScaleDetector;

    // building initial tree
    private LiveData<ArrayList<Officer>> mOfficerLiveData;
    private double mDiagramRadius;
    public static final String COMPANY_ID_KEY = "com.example.r4_fry.androidnodeapp.NodeDiagram.CompanyID";
    public static final String OFFICER_ID_KEY = "com.example.r4_fry.androidnodeapp.NodeDiagram.OfficerID";


    /**
     * @param context context of parent would be AppCompatActivity. This view should only be created
     *                from an activity to prevent it from failing due to not retrieving proper
     *                context
     */
    public NodeView(Context context) {

        super(context);
        setDrawingCacheEnabled(true);

        // retrieve viewmodel from context
        mNodeViewModel = ViewModelProviders.of((AppCompatActivity)context).get(NodeViewModel.class);

        // set up paints
        mCompanyPaint.setColor(Color.BLUE);
        mCompanyPaint.setAntiAlias(true);

        mLinePaint.setColor(Color.GRAY);
        mLinePaint.setStrokeWidth(3);
        mLinePaint.setAntiAlias(true);

        mOfficerPaint.setColor(Color.RED);
        mOfficerPaint.setAntiAlias(true);

        mOfficerTextPaint.setAntiAlias(true);
        mOfficerTextPaint.setTextSize(30f);
        mOfficerTextPaint.setColor(Color.BLACK);

        // set up scale gesture detector as scale listener local class
        mScaleDetector = new ScaleGestureDetector(context, new ViewScaleListener());

        // set up LiveData to show ViewModel contents on receiving data
        mOfficerLiveData = mNodeViewModel.getmOfficerData();
        mOfficerLiveData.observeForever(this::setupNodes);
    }

    /**Retrieves a screenshot of the view displayed on the screen
     * @return bitmap containing the screenshot
     */
    public Bitmap getBitmap() {
        return getDrawingCache();
    }

    /**Function to set up the initial positions of officers and a company on the canvas.
     * The list of officers is provided and the company is assumed to exist so is
     * placed at the centre of the diagram initially. Additional officers are positioned around the
     * mid point in a circle. This positioning code is originally from
     * https://codelabs.developers.google.com/codelabs/advanced-android-training-custom-view-from-scratch/index.html?index=..%2F..advanced-android-training#4
     *
     * @param officers list of officers in the company
     */
    private void setupNodes(ArrayList<Officer> officers){
        if(mNodeViewModel.isInitialisation())
            return;

        // build root node
        Node root = new Node(mWidth/2,mHeight/2,null,null);
        // display officer list
        int limit = officers.size();
        if(limit>0) {
            //if officers are present place them
            float interval = 2 / limit;
            for (int i = 0; i < limit; i++) {
                double angle, startAngle;
                startAngle = Math.PI * (3 / 2d);
                angle = startAngle + (i * (Math.PI / limit));
                float nX = (float) (mDiagramRadius * Math.cos(angle * 2))
                        + (mWidth / 2);
                float nY = (float) (mDiagramRadius * Math.sin(angle * 2))
                        + (mHeight / 2);
                if ((angle > Math.toRadians(360))) {
                    nY += 20;
                }
                root.subNodeArrayList.add(new Node((int) nX, (int) nY, null, officers.get(i)));

            }
        }

        // set the root node in the viewmodel an dupdate initialisation to prevent running a second
        // time
        mNodeViewModel.setRootNode(root);
        mNodeViewModel.setInitialisation(true);

        mNodeViewModel.resetOfficerLiveData();

        // invalidate to redraw with new data
        invalidate();
    }

    /**
     * When view is loaded and attached to window it is ready to run
     * Call fetch officer info if and only if initialisatoin is set to false;
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(!mNodeViewModel.isInitialisation()) {
            // set up viewmodel data
            mNodeViewModel.fetchOfficerInfo(mNodeViewModel.getRootCompanyId());
        }
    }

    /**When the views screen size is changed update class values for dimensions
     * node radius is set here as well and is calculated as 40% the smaller dimension of the screen
     * be that width or height
     * @param w screen width
     * @param h screen height
     * @param oldw old screen width
     * @param oldh old screen height
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mDiagramRadius = ((h>w) ? w : h) * 0.4;
        mCircleRadius = 40;
    }

    /**This function checks whether a set of x and y coordinates fall within a circle
     * The function below is attributed to the following Webpage
     * https://www.geeksforgeeks.org/find-if-a-point-lies-inside-or-on-circle/
     * @param n the node to compare
     * @param x horizontal position on canvas
     * @param y vertical position on canvas
     * @return true if node is in the circle
     */
    private boolean isInCircle(Node n, float x, float y) {
        return ((x - n.xCoord) * (x - n.xCoord) + (y - n.yCoord) * (y - n.yCoord)) <= (mCircleRadius * mCircleRadius);
    }

    /**Search for a node that has been touched by a set of x and y coordinates
     * If multiple nodes are made to overlap the first one it encounters will be returned
     * @param x horizontal position of touch on canvas
     * @param y vertical position of touch on canvas
     * @return reference to the node touched or null if no node was found
     */
    private WeakReference<Node> getNodeTouched(float x, float y) {
        // check root node first then move on to list
        if(isInCircle(mNodeViewModel.getRootNode(),x,y)) {
            Log.d("TOUCHEVENT:", "Action was in circle");
            return new WeakReference<Node>(mNodeViewModel.getRootNode());
        }else {
            //not in root iterate on list
            int limit = mNodeViewModel.getRootNode().subNodeArrayList.size();
            for (int i = 0; i<limit;i++){
                if(isInCircle(mNodeViewModel.getRootNode().subNodeArrayList.get(i),x,y)) {
                    // node in circle
                    return new WeakReference<Node>(mNodeViewModel.getRootNode().subNodeArrayList.get(i));
                }
            }
        }
        return null;
    }

    /**Applies a translation to all nodes displayed in the graph
     * @param xDiff total horizontal movement to translate
     * @param yDiff total vertical movement to translate
     */
    private void moveAll(float xDiff, float yDiff) {
        // update root
        mNodeViewModel.getRootNode().xCoord -= xDiff;
        mNodeViewModel.getRootNode().yCoord -= yDiff;

        int limit = mNodeViewModel.getRootNode().subNodeArrayList.size();
        for (int i = 0; i<limit;i++){
            mNodeViewModel.getRootNode().subNodeArrayList.get(i).xCoord -= xDiff;
            mNodeViewModel.getRootNode().subNodeArrayList.get(i).yCoord -= yDiff;
        }
    }

    /**Handles touch events in the View
     * Can handle touching and panning, during on canvas scale moving is disabled to prevent
     * unintentional touches registering
     * @param event Touch event details
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // pass values to scale detector here
        mScaleDetector.onTouchEvent(event);

        // get current scale to adjust x and y inputs
        float scale = mNodeViewModel.getViewScale();
        float xTouch = event.getX() / scale;
        float yTouch = event.getY() / scale;

        // handle touches
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("TOUCHEVENT:", "Action was DOWN");
                // check if node touched
                mSelectedNode = getNodeTouched(xTouch, yTouch);
                if(mSelectedNode != null){
                    // node has been touched, record its position
                    mXInitialClick = xTouch;
                    mYInitialClick = yTouch;
                }
                else{
                    //no node touched so do panning unless currently scaling
                    if(!mScaleDetector.isInProgress()) {
                        mXTouchOld = xTouch;
                        mYTouchOld = yTouch;
                    }
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if(!mScaleDetector.isInProgress()) {
                    // if node selected move to current x and y pos
                    if (mSelectedNode != null) {
                        // move to new pos
                        mSelectedNode.get().yCoord = (int) yTouch;
                        mSelectedNode.get().xCoord = (int) xTouch;
                    } else {
                        // otherwise mid panning panning so move all
                        float xDiff = mXTouchOld - xTouch;
                        float yDiff = mYTouchOld - yTouch;
                        moveAll(xDiff, yDiff);
                        mXTouchOld = xTouch;
                        mYTouchOld = yTouch;

                    }
                    // redraw the view
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                //if node was selected
                if(mSelectedNode != null){
                    // check distance moved
                    if(Math.abs(mXInitialClick - xTouch) < mMoveTolerance ||
                    Math.abs(mYInitialClick - yTouch) < mMoveTolerance) {
                        // if has not moved far from origin then treat as a click event and open
                        // data for that node
                        Context context = getContext();
                        Intent intent = new Intent(context, CompanyDataActivity.class);
                        // check if node is a company or officer
                        if (mSelectedNode.get().officerData == null){
                            intent.putExtra(COMPANY_ID_KEY, mNodeViewModel.getRootCompanyId());
                        }else{
                            intent.putExtra(OFFICER_ID_KEY, mSelectedNode.get().officerData.getOfficerId());
                        }
                        context.startActivity(intent);
                    }
                    mSelectedNode.clear();
                    mSelectedNode = null;
                }
                break;
        }
        return false;
    }

    /**Draw lines to the apps canvas
     * @param root the central node that other nodes are stored in as a tree
     * @param canvas The location being drawn to
     */
    private void drawLines(Node root, Canvas canvas){
        int limit = root.subNodeArrayList.size();
        for (int i = 0; i<limit;i++){
            canvas.drawLine(root.xCoord, root.yCoord, root.subNodeArrayList.get(i).xCoord,root.subNodeArrayList.get(i).yCoord, mLinePaint);
        }
    }

    /**Draw circles to the apps canvas as well as their accompanying text fields
     * @param root the central node that other nodes are stored in as a tree
     * @param canvas The location being drawn to
     */
    private void drawCircles(Node root, Canvas canvas){
        int limit = root.subNodeArrayList.size();
        for (int i = 0; i<limit;i++){
            canvas.drawCircle(root.subNodeArrayList.get(i).xCoord, root.subNodeArrayList.get(i).yCoord, mCircleRadius,mOfficerPaint);
            canvas.drawText(root.subNodeArrayList.get(i).officerData.getName(), root.subNodeArrayList.get(i).xCoord + (mCircleRadius - 10), root.subNodeArrayList.get(i).yCoord - (mCircleRadius-10), mOfficerTextPaint);
        }
    }

    /**Draws the node graph to the canvas
     * First this sets the scale of the canvas
     * Then sets the background of the canvas to white,
     * Then draws the lines
     * and finally the circles
     * @param canvas The location being drawn to
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scaleFactor = mNodeViewModel.getViewScale();
        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);
        // draw out the lines
        canvas.drawColor(Color.WHITE);
        drawLines(mNodeViewModel.getRootNode(), canvas);

        // draw root circle
        canvas.drawCircle(mNodeViewModel.getRootNode().xCoord, mNodeViewModel.getRootNode().yCoord, mCircleRadius, mCompanyPaint);

        // draw other circles(can be recursive, recurse on if subnode has nodes in list)
        drawCircles(mNodeViewModel.getRootNode(), canvas);

        canvas.restore();
    }

    /**This is called when scaling is detected in the on draw method and saves it to the
     * https://stuff.mit.edu/afs/sipb/project/android/docs/training/gestures/scale.html
     * scaling code attributed to here
     */
    private class ViewScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = mNodeViewModel.getViewScale();
            scaleFactor *= scaleGestureDetector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            mNodeViewModel.setViewScale(scaleFactor);
            invalidate();
            return true;
        }
    }
}
