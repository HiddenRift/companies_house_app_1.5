package com.example.r4_fry.androidnodeapp.SentientDetector;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.example.r4_fry.androidnodeapp.CompaniesDB.Officer;
import com.example.r4_fry.androidnodeapp.NodeDiagram.NodeViewModel;
import com.example.r4_fry.androidnodeapp.R;

import java.util.ArrayList;

public class SentientDetectorActivity extends AppCompatActivity {

    private SentientDetectorViewModel mDetectorViewModel;
    private LiveData<String> SentientStatusLiveData;
    private TextView mSentientStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentient_detector);
        this.setTitle("Warframe sentient checker");
        mSentientStatus = findViewById(R.id.sentient_status);
        mDetectorViewModel = ViewModelProviders.of(this).get(SentientDetectorViewModel.class);
        SentientStatusLiveData = mDetectorViewModel.getSentientStatus();
        SentientStatusLiveData.observeForever(this::update);
    }

    public void refresh(View view){
        mDetectorViewModel.updateSentientState();
    }

    public void update(String status){
        mSentientStatus.setText(status);
    }
}