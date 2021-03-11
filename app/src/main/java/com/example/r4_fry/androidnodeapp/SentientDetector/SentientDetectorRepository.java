package com.example.r4_fry.androidnodeapp.SentientDetector;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.r4_fry.androidnodeapp.CompaniesAPIAccess;
import com.example.r4_fry.androidnodeapp.CompaniesDB.CompaniesDAO;
import com.example.r4_fry.androidnodeapp.CompaniesDB.CompaniesRoomDatabase;
import com.example.r4_fry.androidnodeapp.CompaniesDB.Company;
import com.example.r4_fry.androidnodeapp.CompaniesDB.Officer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Class for maintaining access to data throughout the application.
 * This class is created as a singleton to allow for it to be used to house the adaptor for the Room
 * Database DAO class.
 */
public class SentientDetectorRepository {
    private static volatile SentientDetectorRepository mInstance;

    private MutableLiveData<String> mSentientData = new MutableLiveData<>();

    /**Vestigal initialisation code from the node app repository
     * @param application application reference for initialising database
     */
    private SentientDetectorRepository(Application application) {
        CompaniesRoomDatabase db = CompaniesRoomDatabase.getDatabase(application);
    }

    /**
     * @param application application reference for constructor
     * @return instance to singleton CompaniesRepository
     */
    public static SentientDetectorRepository GetInstance(Application application){
        if(mInstance == null){
            synchronized (SentientDetectorRepository.class){
                if(mInstance == null){
                    mInstance = new SentientDetectorRepository(application);
                }
            }
        }
        return mInstance;
    }

    /**Retrieves the  value of an element in a json object safely
     * @param object contains a JSON object
     * @param key contains a key for value in the json object
     * @return string containing the contents of the object held with the key or null
     * if no value existed with that key
     * @throws JSONException if object is uninitialised or attempting to retrieve array by value
     */
    private String ifExistsGet(JSONObject object, String key) throws JSONException{
        if(object.has(key))
            return object.getString(key);
        else
            return null;
    }

    /**Retrieves the worldstate and parses the json to retrieve the location of the sentient vessel.
     * This is posted to the livedata object so the front end can update
     */
    public void runFetchWorldState() {
        new Thread(()->{
            final String jsonStr = WorldstateAPIAccess.getWorldStateInfoRaw();
            //ArrayList<Officer> officerList = new ArrayList<>();
            String result = "";
            int nodeCode = 0;
            try {
                final JSONObject root = new JSONObject(jsonStr);
                result = root.getString("Tmp");
                final JSONObject newRoot = new JSONObject(root.getString("Tmp"));
                nodeCode = newRoot.getInt("sfn");
                result = translateAreaCode(nodeCode);
            } catch (JSONException e) {
                e.printStackTrace();
                result = "Unable to detect sentient presence";
            }
            mSentientData.postValue(result);
        }).start();
    }

    /**Translates the code taken from the api to a location node on the map that can be recognised.
     * @return string containing the destination or a message that the app requires updating
     */
    private String translateAreaCode(int area){
        String result;
        switch (area) {
            case 505: result = "Ruse War Field";
                break;
            case 510: result = "Gian Point";
                break;
            case 550: result = "Nsu Grid";
                break;
            case 551: result = "Ganalen's Grave";
                break;
            case 552: result = "Rya";
                break;
            case 553: result = "Flexa";
                break;
            case 554: result = "H-2 Cloud";
                break;
            case 555: result = "R-9 Cloud";
                break;
            default:
                String temp = String.valueOf(area);
                result = "A new area code was found, please update the app " + temp;
        }
        return result;
    }

    /**Retrieves a reference to the LiveData object used to store the location result from the api
     * @return returns LiveData
     */
    public LiveData<String> fetchSentientStatus(){
        return mSentientData;
    }
}
