package com.example.r4_fry.androidnodeapp.SentientDetector;

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
public class WorldstateAPIAccess {
    //Constants for facilitating api URI constructions
    private static final String BASE_URL = "https://content.warframe.com/dynamic/worldState.php";


    /**Retrieves warframe data and parses it into a string so that it may be read as json
     * @return string containing json from request about companies
     */
    public static String getWorldStateInfoRaw(){

        URL req = null;

        Uri constructedURI = Uri.parse(BASE_URL);
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
    public static String getWorldStateDataManual(String path){
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


    /**Performs a GET request to the API using the specified URL
     * Fetches the JSON the data from the Warframe API and passes the result
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
