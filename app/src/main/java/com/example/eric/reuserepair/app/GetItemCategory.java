// CS419 - Reuse & Repair Mobile App
// ---------------------------------------
// Charles Jenkins
// <jenkinch@oregonstate.edu>
//
// Billy Kerns
// <kernsbi@oregonstate.edu>
//
// Eric Cruz
// <cruze@oregonstate.edu>
//
// Title: GetItemCategory.java
//
// Description: Makes a network call to our
// web service to get all rows from the
// item-category table
// ---------------------------------------
// Acknowledgements:
// http://developer.android.com/training/basics/network-ops/connecting.html

package com.example.eric.reuserepair.app;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetItemCategory extends AsyncTask<String, Void, String> {
    //Based on http://developer.android.com/training/basics/network-ops/connecting.html
    protected String doInBackground(String... urls) {
        //Create URL we will be requesting data from
        String reuseAndRepairAPI = "http://web.engr.oregonstate.edu/~jenkinch/api.php/item-category";
        try {
            return downloadUrl(reuseAndRepairAPI);
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //So we don't hang forever on a bad network call
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Accept", "application/json");
            conn.connect();
            is = conn.getInputStream();

            String contentAsString = readIt(is);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    //Turn input into string
    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }
}
