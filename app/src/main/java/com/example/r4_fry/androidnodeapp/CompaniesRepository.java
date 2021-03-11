package com.example.r4_fry.androidnodeapp;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

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
public class CompaniesRepository {
    private static volatile CompaniesRepository mInstance;
    private static CompaniesDAO mCompaniesDao;

    private MutableLiveData<ArrayList<Company>> mAPIData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Officer>> mOfficerAPIData = new MutableLiveData<>();

    /**Initialises the companies room database
     * @param application application reference for initialising database
     */
    private CompaniesRepository(Application application) {
        CompaniesRoomDatabase db = CompaniesRoomDatabase.getDatabase(application);
        mCompaniesDao = db.companiesDao();

    }

    /**
     * @param application application reference for constructor
     * @return instance to singleton CompaniesRepository
     */
    public static CompaniesRepository GetInstance(Application application){
        if(mInstance == null){
            synchronized (CompaniesRepository.class){
                if(mInstance == null){
                    mInstance = new CompaniesRepository(application);
                }
            }
        }
        return mInstance;
    }

    /**
     * resets the company MutableLiveData object
     */
    public void resetCompanyData(){
        mAPIData = new MutableLiveData<>();
    }

    /**
     * resets the Officer MutableLiveData object
     */
    public void resetOfficerData(){
        mOfficerAPIData = new MutableLiveData<>();
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

    /**
     * @param id string containing id reference to company
     * @return LiveData object that will contain data from the room database
     */
    public LiveData<List<Company>> getCompanyFromDBWhereID(String id)
    {
        return mCompaniesDao.getCompany(id);
    }

    /**
     * @param id string containing id reference to officer
     * @return LiveData object that will contain data from the room database
     */
    public LiveData<List<Officer>> getOfficerFromDBWhereID(String id){
        return mCompaniesDao.getOfficer(id);
    }

    /**Retrieves officers that work for a company
     * Runs a thread that retrieves officers that work for a company by a companies ID and passes
     * the data to the classes MutableLiveData object and creates an entry to the room Database for
     * each result returned. if no results are returned an empty list is inserted to the LiveData
     * object
     * @param companyId string containing id reference to company
     */
    public void runFetchOfficers(String companyId) {
        new Thread(()->{
            mCompaniesDao.deleteAllOfficers();
            final String jsonStr = CompaniesAPIAccess.getCompanyOfficers(companyId);
            ArrayList<Officer> officerList = new ArrayList<>();
            try {
                final JSONObject root = new JSONObject(jsonStr);
                final JSONArray root_items = root.getJSONArray("items");
                final int root_items_length = root_items.length();
                for (int i = 0; i <root_items_length;i++){
                    JSONObject item = root_items.getJSONObject(i);
                    String officerId = item.getJSONObject("links")
                            .getJSONObject("officer")
                            .getString("appointments")
                            .replace("/officers/","")
                            .replace("/appointments","");

                    officerList.add(new Officer(
                            officerId,
                            item.getString("name"),
                            ifExistsGet(item, "appointed_on"),
                            ifExistsGet(item, "officer_role"),
                            ifExistsGet(item, "nationality"),
                            ifExistsGet(item, "occupation")
                    ));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mCompaniesDao.insertOfficers(officerList);
            mOfficerAPIData.postValue(officerList);
        }).start();
    }

    /**Retrieves Companies matching query string
     * Runs a thread that retrieves company data by a search query and passes
     * the data to the classes MutableLiveData object and creates an entry to the room Database for
     * each result returned. if no results are returned an empty list is inserted to the LiveData
     * object
     * @param query string containing a query to search
     */
    public void runFetchCompanies(String query){
        new Thread(()->{

            mCompaniesDao.deleteAllCompanies();
            final String jsonStr = CompaniesAPIAccess.getCompanyInfo(query);

            ArrayList<Company> companyList = new ArrayList<>();

            try {
                final JSONObject root = new JSONObject(jsonStr);
                final JSONArray root_items = root.getJSONArray("items");
                for (int i = 0; i < root_items.length(); i++) {
                    JSONObject item = root_items.getJSONObject(i);
                    if(item.has("title") && item.has("company_number")){
                        companyList.add(new Company(item.getString("company_number"),
                                item.getString("title"),
                                ifExistsGet(item, "company_status"),
                                ifExistsGet(item, "description"),
                                ifExistsGet(item, "address_snippet")
                        ));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                companyList.add(new Company("NULL", "ERROR", null, null,null));
            }
            mCompaniesDao.insertCompanies(companyList);
            mAPIData.postValue(companyList);
        }).start();
    }

    /**Retrieves a reference to the LiveData object used to store the officer results from the api
     * @return returns LiveData
     */
    public LiveData<ArrayList<Officer>> fetchOfficerData(){
        return mOfficerAPIData;
    }


    /**Retrieves a reference to the LiveData object used to store the company results from the api
     * @return returns LiveData
     */
    public LiveData<ArrayList<Company>> fetchCompanyData(){
        //this.resetCompanyData();
        return mAPIData;
    }
}
