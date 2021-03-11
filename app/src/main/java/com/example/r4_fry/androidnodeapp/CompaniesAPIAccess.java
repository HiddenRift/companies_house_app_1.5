package com.example.r4_fry.androidnodeapp;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Data source class to communicate with the Companies house API
 */
public class CompaniesAPIAccess {
    //Constants for facilitating api URI constructions
    private static final String BASE_URL = "https://api.companieshouse.gov.uk";
    private static final String QUERY_PARAM = "q";
    private static final String SEARCH_PATH = "search";
    private static final String COMPANIES_PATH = "companies";
    private static final String COMPANY_PATH = "company";
    private static final String OFFICERS_PATH = "officers";
    private static final String MAX_RESULTS_PARAM = "items_per_page";
    private static final String START_INDEX_PARAM = "start_index";

    //Key for accessing the API
    private static final String API_KEY = "";

    /**Searches for companies with query string
     * Builds the URI and executes the query to do so
     * @param q query string containing search params to look for company
     * @return string containing json from request about companies
     */
    public static String getCompanyInfo(String q){

        URL req = null;

        Uri constructedURI = Uri.parse(BASE_URL).buildUpon()
                .appendPath(SEARCH_PATH)
                .appendPath(COMPANIES_PATH)
                .appendQueryParameter(QUERY_PARAM,q)
                .appendQueryParameter(MAX_RESULTS_PARAM, "100")
                .appendQueryParameter(START_INDEX_PARAM,"0")
                .build();
        try {
            req = new URL(constructedURI.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return accessAPI(req);
    }

    /**Searches for whatever is at the path specified in the api
     * constructs the URI and returns the data from the API
     * @param path pre-encoded path for accessing the API
     * @return string containing json from request
     */
    public static String getCompanyDataManual(String path){
        URL req = null;

        Uri constructedURI = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(path)
                .build();
        try {
            req = new URL(constructedURI.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return accessAPI(req);
    }

    /**Retrieves the officer data from the API and
     * @param companyNo for the company to retrieve officers from
     * @return string containing json from request about officers
     */
    public static String getCompanyOfficers(String companyNo){
        URL req = null;

        Uri constructedURI = Uri.parse(BASE_URL).buildUpon()
                .appendPath(COMPANY_PATH)
                .appendPath(companyNo)
                .appendPath(OFFICERS_PATH)
                .build();
        try {
            req = new URL(constructedURI.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return accessAPI(req);
    }

    /**Performs a GET request to the API using the specified URL
     * Fetches the JSON the data from the Companies house API and passes the result
     * back as a string.
     * If there is an error in fetching the data returns null
     * @param req URL to communicate with
     * @return string containing json from request
     */
    private static String accessAPI(URL req){
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String jsonOutput = null;

        try {
            conn = (HttpURLConnection) req.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization",API_KEY);
            conn.connect();

            InputStream iStream = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(iStream));

            //build the string
            StringBuilder s = new StringBuilder();
            // read lines from the api appending to string builder until buffered reader is empty
            for (String line; (line = reader.readLine()) != null; s.append(line)){}

            if (s.length() == 0){
                return null;
            }
            jsonOutput = s.toString();


        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(conn!=null)
                conn.disconnect();
            if (reader!=null){
                try{
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(req.toString(),jsonOutput);

        return jsonOutput;
    }
}
