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
// Title: CategoryActivity.java
//
// Description: Activity to display a list of categories from
// our database that the user can choose.
// ---------------------------------------//
// Acknowledgements:
// http://developer.android.com/training/basics/network-ops/connecting.html

package com.example.eric.reuserepair.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    public boolean isInternetAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!isInternetAvailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A fragment to display the categories a user can select.
     */
    public static class CategoryFragment extends Fragment {

        ArrayAdapter<String> mCategoryAdapter;

        public CategoryFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            FetchCategoryTask categoryTask = new FetchCategoryTask();
            categoryTask.execute();

            // Blank string which will be appended after GET request
            String[] data = {};
            List<String> category = new ArrayList<String>(Arrays.asList(data));

            // Create an ArrayAdapter for the blank category list to populate ListView
            mCategoryAdapter =
                    new ArrayAdapter<String>(
                            getActivity(),
                            R.layout.list_item_category,
                            R.id.list_item_category_textview,
                            category
                    );
            View rootView = inflater.inflate(R.layout.fragment_category, container, false);

            // Get a reference to the ListView and attach this adapter to it
            ListView listView = (ListView) rootView.findViewById(R.id.listview_category);
            listView.setAdapter(mCategoryAdapter);

            // On-click listener to open ItemActivity
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String category = mCategoryAdapter.getItem(position);
                    Intent intent = new Intent(getActivity(), ItemActivity.class)
                            .putExtra("category", category);
                    startActivity(intent);
                }
            });

            return rootView;
        }

        public class FetchCategoryTask extends AsyncTask<String, Void, String[]> {

            private final String LOG_TAG = FetchCategoryTask.class.getSimpleName();

            private String[] getCategoryDataFromJson(String categoryJsonStr)
                    throws JSONException {

                JSONObject categoryJson = new JSONObject(categoryJsonStr);
                JSONObject recordObject = categoryJson.getJSONObject("category");
                JSONArray categoryArray = recordObject.getJSONArray("records");

                String[] resultStrs = new String[categoryArray.length()];
                for (int i = 0; i < categoryArray.length(); i++) {
                    JSONArray categoryPair = categoryArray.getJSONArray(i);
                    resultStrs[i] = categoryPair.getString(1);
                }

                for (String s : resultStrs) {
                    Log.v(LOG_TAG, "Category entry: " + s);
                }
                return resultStrs;
            }

            @Override
            protected String[] doInBackground(String... params) {

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String categoryJsonStr = null;

                try {
                    // Construct URL for category GET request
                    String baseUrl = "http://web.engr.oregonstate.edu/~jenkinch/api.php/category";
                    URL url = new URL(baseUrl);

                    // Open HTTP connection to API
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }
                    categoryJsonStr = buffer.toString();

                    Log.v(LOG_TAG, "Category: " + categoryJsonStr);
                } catch (IOException e) {
                    Log.e("PlaceholderFragment", "Error", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e("PlaceholderFragment", "Error closing stream", e);
                        }
                    }
                }

                try {
                    return getCategoryDataFromJson(categoryJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String[] result) {
                if (result != null) {
                    mCategoryAdapter.clear();
                    Arrays.sort(result);
                    for (String categoryStr : result) {
                        mCategoryAdapter.add(categoryStr);
                    }
                }
            }
        }
    }
}

