package com.example.r4_fry.androidnodeapp.SentientDetector;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

/**
 * ViewModel to hold data for the Node View handles interfacing with the data in the API and the
 * Room Database
 */
public class SentientDetectorViewModel extends AndroidViewModel {

    private final SentientDetectorRepository mSentientRepository;

    /**Constructor for ViewModel to handle the node diagrams data
     * @param application reference for context to create reference to the repository class
     */
    public SentientDetectorViewModel(Application application){
        super(application);
        mSentientRepository = SentientDetectorRepository.GetInstance(application);
    }

    public LiveData<String> getSentientStatus(){
        updateSentientState();
        return mSentientRepository.fetchSentientStatus();
    }

    public void updateSentientState(){
        mSentientRepository.runFetchWorldState();
    }

}
