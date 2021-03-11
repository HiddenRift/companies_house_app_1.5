package com.example.r4_fry.androidnodeapp;

import android.arch.lifecycle.LiveData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.example.r4_fry.androidnodeapp.CompaniesDB.Company;

import java.util.ArrayList;

public class CompaniesListActivity extends AppCompatActivity {

    EditText mA;
    private RecyclerView mRecyclerView;
    private CompaniesRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LiveData<ArrayList<Company>> mliveData;


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
        setContentView(R.layout.activity_companies_list);

        // get reference to edittext box in UI & recyclerview
        mA = (EditText) findViewById(R.id.queryText);
        mRecyclerView = (RecyclerView) findViewById(R.id.CompaniesResults);

        // set recyclerview to fix size
        mRecyclerView.hasFixedSize();

        // set layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify adaptor and connect to recyclerview
        mAdapter = new CompaniesRecyclerViewAdapter(null);
        mRecyclerView.setAdapter(mAdapter);

        // run query to fetch data through the repository and specify observer to run setDataset
        mliveData = CompaniesRepository.GetInstance(getApplication()).fetchCompanyData();
        mliveData.observeForever(companies -> mAdapter.setDataset(companies));

    }


    /**Searches the api using query
     *
     * Searches the API using the string tin the editText box and passes the string to execute the
     * search in the repository class. Also updates the local livedata object to retrieve the
     * data when it is ready.
     * @param view
     */
    public void doSomething(View view) {
        CompaniesRepository.GetInstance(getApplication()).runFetchCompanies(mA.getText().toString());
        mliveData = CompaniesRepository.GetInstance(getApplication()).fetchCompanyData();
    }
}