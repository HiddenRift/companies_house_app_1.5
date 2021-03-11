package com.example.r4_fry.androidnodeapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.r4_fry.androidnodeapp.CompaniesDB.Company;
import com.example.r4_fry.androidnodeapp.NodeDiagram.NodeDiagramActivity;
import com.example.r4_fry.androidnodeapp.SentientDetector.SentientDetectorActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText mA;
    private Button mCompaniesHouse;
    private Button mSentientDetector;


    /**Sets up the interface for the MainActivity
     * Initialises all variables and sets up the RecyclerView to display output fed into
     * the adaptor
     * @param savedInstanceState
     */
    // app created to target android 5.0
    // https://www.mytrendin.com/fetch-json-data-using-asynctask-restful-api-display-recyclerview-android/
    // look here for reference
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
    }

    public void onSentientButtonPressed(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, SentientDetectorActivity.class);
        context.startActivity(intent);
    }

    public void onCompaniesHouseButtonPressed(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, CompaniesListActivity.class);
        context.startActivity(intent);
    }

}
