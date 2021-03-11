package com.example.r4_fry.androidnodeapp.NodeDiagram;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.r4_fry.androidnodeapp.CompaniesRepository;
import com.example.r4_fry.androidnodeapp.CompaniesDB.Officer;

import java.util.ArrayList;

/**
 * ViewModel to hold data for the Node View handles interfacing with the data in the API and the
 * Room Database
 */
public class NodeViewModel extends AndroidViewModel {
    private String mRootCompanyId;
    private Node mRootNode;
    private float mViewScale;
    private CompaniesRepository mCompaniesRepository;
    private Application mApplication;
    private LiveData<ArrayList<Officer>> mOfficerData;
    private boolean mInitialisation = false;

    /**Constructor for ViewModel to handle the node diagrams data
     * @param application reference for context to create reference to the repository class
     */
    public NodeViewModel(Application application){
        super(application);
        mApplication = application;
        mViewScale = 1.0f;

        mCompaniesRepository = CompaniesRepository.GetInstance(application);
        mOfficerData =  mCompaniesRepository.fetchOfficerData();

        mRootNode = new Node(100,100,null,null);


    }

    /**Set the id of the company located at the root of the diagram
     * @param RootCompanyId id of company at root of diagram
     */
    public void setRootCompanyId(String RootCompanyId) {
        mRootCompanyId = RootCompanyId;
    }

    /**Search for officers using the Companies House API
     * @param companyId id of the company to search for officers for
     */
    public void fetchOfficerInfo(String companyId)
    {
        mCompaniesRepository.runFetchOfficers(companyId);
    }

    /**
     * Resets the officers live data object
     */
    public void resetOfficerLiveData(){
        mCompaniesRepository.resetOfficerData();
    }


    /**Sets the root node to be shown at the centre of the diagram
     * The root node also contains its children nodes
     * @param mRootNode Sets the root node of the diagram
     */
    public void setRootNode(Node mRootNode) {
        // call to invalidate should be made after setting new root
        this.mRootNode = mRootNode;
    }

    /**Gets the root node to be used to draw of search the diagram
     * @return gets the root node for the diagram
     */
    public Node getRootNode() {
        return mRootNode;
    }

    /** Sets the view scale of the diagram
     * @param viewScale view scale of the diagram
     */
    public void setViewScale(float viewScale){
        mViewScale = viewScale;
    }

    /**Retrieves the view scale of the diagram
     * @return view scale of the diagram
     */
    public float getViewScale(){
        return mViewScale;
    }

    /**Checks whether the node diagram has been initialised
     * @return whether the node diagram has been initialised
     */
    public boolean isInitialisation() {
        return mInitialisation;
    }

    /**Sets the state of the diagram initiaisation
     * @param initialisation sets the initialisation state of the diagram
     */
    public void setInitialisation(boolean initialisation) {
        this.mInitialisation = initialisation;
    }

    /**Returns a reference to the list of officers in a company that can be observed
     * @return reference to officer data list that will be retreived by companies repository
     */
    public LiveData<ArrayList<Officer>> getmOfficerData() {
        return mOfficerData;
    }

    /**Returns the ID of the root company
     * @return retrieves ID of the root company
     */
    public String getRootCompanyId() {
        return mRootCompanyId;
    }
}
