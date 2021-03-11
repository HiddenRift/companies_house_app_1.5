package com.example.r4_fry.androidnodeapp.CompanyData;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.r4_fry.androidnodeapp.CompaniesDB.Company;
import com.example.r4_fry.androidnodeapp.CompaniesDB.Officer;
import com.example.r4_fry.androidnodeapp.CompaniesRecyclerViewAdapter;
import com.example.r4_fry.androidnodeapp.CompaniesRepository;
import com.example.r4_fry.androidnodeapp.NodeDiagram.NodeView;
import com.example.r4_fry.androidnodeapp.R;

import java.util.List;

/**
 * Activity to display the data for a given company or officer in a RecyclerView
 */
public class CompanyDataActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CompanyDataRecyclerViewAdaptor mAdaptor;
    private CompaniesRepository mRepository;
    private LiveData<List<Company>> mCompanyData;
    private LiveData<List<Officer>> mOfficerData;

    /**Sets up the Activity and begins pulling data from the database for the company or
     * officer specified.
     * @param savedInstanceState not used other than for super
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_data);
        mRepository = CompaniesRepository.GetInstance(getApplication());

        mRecyclerView = (RecyclerView) findViewById(R.id.company_data_activity_recycler_view);
        mRecyclerView.hasFixedSize();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdaptor = new CompanyDataRecyclerViewAdaptor();
        mRecyclerView.setAdapter(mAdaptor);


        Intent intent = getIntent();
        String companyID = intent.getStringExtra(NodeView.COMPANY_ID_KEY);
        String officerID = intent.getStringExtra(NodeView.OFFICER_ID_KEY);

        // if companyid specified then update with company info
        if (companyID != null) {
            mCompanyData = mRepository.getCompanyFromDBWhereID(companyID);
            mCompanyData.observeForever((List<Company> result) -> {
                if (result.size() == 0) {
                    Log.d("CompanyDataActivity", "Error result set empty");
                }else {
                    mAdaptor.setCompanies(result.get(0));
                }
            });
        }

        // if officer id is specified then update interface with officer info
        if (officerID != null){
            mOfficerData = mRepository.getOfficerFromDBWhereID(officerID);
            mOfficerData.observeForever((List<Officer> result) -> {
                if (result.size() == 0) {
                    Log.d("OfficerDataActivity", "Error result set empty");
                }else {
                    mAdaptor.setOfficers(result.get(0));
                }
            });
        }


    }
}
