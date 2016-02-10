package com.example.eric.reuserepair.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayAdapter<String> mCategoryAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Hard-coded dummy data for item category list
        String[] data = {
                "APPLIANCES, LARGE",
                "APPLIANCE, SMALL",
                "ART SUPPLIES",
                "BEDDING / BATH",
                "BIKES / SPORTING CAMPING",
                "BOOKS",
                "BROWN PAPER / LARGE SHOPPING BAGS",
                "BLDG MATERIALS / HOME IMPROVEMENT",
                "CDs, DVDs, LPs, VIDEO GAMES, etc",
                "CELL PHONES",
                "CHILDREN'S GOODS / CLOTHING",
                "CLOTHING / ACCESSORIES",
                "COMPUTER PAPER",
                "COMPUTERS / MONITORS",
                "EGG CARTONS",
                "EYEGLASSES",
                "FABRIC (material, batting, supplies)",
                "FIREWOOD",
                "FOOD (unopened, pre-expired)",
                "FOOD (surplus garden produce)",
                "FOOD CONTAINERS (clean glass/plastic w/lids)",
                "FURNITURE",
                "GARDEN / LANDSCAPING",
                "GARDEN POTS",
                "HOME ELECTRONICS",
                "HOUSEHOLD GOODS",
                "MEDICAL EQUIPMENT / SUPPLIES",
                "OFFICE EQUIPMENT",
                "OFFICE SUPPLIES",
                "PACKING MATERIALS",
                "PET SUPPIES / FOOD",
                "PRINTER CARTRDGE REFILLING",
                "SCHOOL SUPPLIES",
                "TOILETRIES",
                "VEHICLE / PARTS",
                "MEDICATIONS"
        };
        List<String> category = new ArrayList<String>(Arrays.asList(data));

        // Create an ArrayAdapter for the dummy category list to populate ListView
        mCategoryAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_category,
                        R.id.list_item_category_textview,
                        category
                );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView and attach this adapter to it
        ListView listView = (ListView) rootView.findViewById(R.id.listview_category);
        listView.setAdapter(mCategoryAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String category = mCategoryAdapter.getItem(position);
                Toast.makeText(getActivity(), category, Toast.LENGTH_SHORT).show();
            }
        });

        FetchCategoryTask categoryTask = new FetchCategoryTask();
        categoryTask.execute();

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
                Log.v(LOG_TAG, "Forecast entry: " + s);
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
                for (String categoryStr : result) {
                    mCategoryAdapter.add(categoryStr);
                }
            }
        }
    }
}
