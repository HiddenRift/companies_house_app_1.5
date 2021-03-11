package com.example.r4_fry.androidnodeapp.NodeDiagram;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.r4_fry.androidnodeapp.CompaniesRecyclerViewAdapter;
import com.example.r4_fry.androidnodeapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;

/**
 * Activity to display a node network diagram
 */
public class NodeDiagramActivity extends AppCompatActivity {
    private NodeViewModel mNodeViewModel;
    private NodeView mNodeView;
    public final static int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    /**Creates the view and initialises variables, retrieving reference to the viewModel for this
     * activity which is passed to the customView.
     * Sets th title of company to the title on screen
     * Finally sets the content to display the custom view
     * @param savedInstanceState unused except in super()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_diagram);

        mNodeViewModel = ViewModelProviders.of(this).get(NodeViewModel.class);
        Intent intent = getIntent();
        String temp = intent.getStringExtra(CompaniesRecyclerViewAdapter.COMPANY_ID_KEY);

        setTitle(intent.getStringExtra(CompaniesRecyclerViewAdapter.COMPANY_NAME_KEY));
        mNodeViewModel.setRootCompanyId(temp);

        mNodeView = new NodeView(this);
        setContentView(mNodeView);
    }

    /**Sets up the menu bar to add the share button
     * @param menu the options bar at top of screen
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.node_actions_menu, menu);
        return true;
    }

    /**Performs action specified by selection an item in the options menu
     * Implemented via a case statement in order to better provide extensibility for adding more
     * buttons in the future.
     * on Share
     * @param item selected item in the menu bar
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:
                share();
                break;
        }
        return true;
    }

    /**
     * Shares an image of the node graph to an app that is capable of handling image data
     * This function was made using code from https://developer.android.com/training/sharing/send
     * Permission requesting was implemented using code found from
     * https://developer.android.com/training/permissions/requesting
     *
     */
    private void share() {
        // retrieve Bitmap
        Bitmap img = mNodeView.getBitmap();

        // convert to jpeg byte stream
        ByteArrayOutputStream ioStream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, ioStream);

        //check permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // permission not granted so request
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }else {
            // permission has been granted so create file instance with path and attempt sharing
            File fp = new File(Environment.getExternalStorageDirectory()+File.separator+"NodeGraphShare.jpg");
            try {
                // create new file and write outstream
                fp.createNewFile();
                FileOutputStream fOut = new FileOutputStream(fp);
                fOut.write(ioStream.toByteArray());
                fOut.close();
            } catch (IOException e) {
                //sharing has failed so return
                return;
            }
            // retrieve file URI
            Uri imageUri = FileProvider.getUriForFile(this, getString(R.string.file_prov_auth), fp);

            // set up intent
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(intent, "Share image"));
        }
    }
}
