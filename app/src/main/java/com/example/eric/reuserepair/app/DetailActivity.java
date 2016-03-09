package com.example.eric.reuserepair.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.String;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(this.getIntent().getExtras().getString("business"));

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment {

        ImageView mImg;
        ArrayAdapter<String> mDetailAdapter;
        private final String LOG_TAG = DetailFragment.class.getSimpleName();

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            /*
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            */

            int businessID = 0;
            ArrayList<String> data = new ArrayList<String>();
            String selectedBusiness = getActivity().getIntent().getExtras().getString("business");
            JSONArray businessRecords = null;
            JSONArray lookingForBID;
            double lat = 0;
            double lng = 0;

            Log.v(LOG_TAG, "Business string: " + selectedBusiness);

            try {
                JSONObject allBusinessesString = new JSONObject(getActivity().getIntent().getExtras().getString("allBusiness"));
                Log.v(LOG_TAG, "All business string: " + allBusinessesString);
                JSONObject businessString = allBusinessesString.getJSONObject("business");
                businessRecords = businessString.getJSONArray("records");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < businessRecords.length(); i++) {
                try {
                    lookingForBID = businessRecords.getJSONArray(i);
                    String businessName = lookingForBID.getString(1);
                    if (businessName.equals(selectedBusiness)) {
                        data.add(lookingForBID.getString(1));
                        data.add(lookingForBID.getString(2));
                        data.add(lookingForBID.getString(3));
                        data.add(lookingForBID.getString(4));
                        data.add(lookingForBID.getString(5));
                        data.add(lookingForBID.getString(6));
                        lat = lookingForBID.getDouble(7);
                        lng = lookingForBID.getDouble(8);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.v(LOG_TAG, "This string " + data);

            mDetailAdapter =
                    new ArrayAdapter<String>(
                            getActivity(),
                            R.layout.list_item_detail,
                            R.id.list_item_detail_textview,
                            data
                    );
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_detail);
            listView.setAdapter(mDetailAdapter);

            mImg = (ImageView) rootView.findViewById(R.id.static_map_view);
            String url = "http://maps.google.com/maps/api/staticmap?center=&markers=color:blue%7Clabel:S%7C" + lat + "," + lng + "&zoom=15&size=300x150&maptype=roadmap&sensor=false";
            GetMapsImage getMapsImage = new GetMapsImage();
            Bitmap bm = null;
            try {
                bm = getMapsImage.execute(url).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            mImg.setImageBitmap(bm);

            //BitmapFactory.Options bmOptions;
            //bmOptions = new BitmapFactory.Options();
            //Bitmap bm = loadBitmap(url, bmOptions);

            return rootView;
        }

        /*
        public static Bitmap loadBitmap(String URL, BitmapFactory.Options options) {
            Bitmap bitmap = null;
            InputStream in = null;
            try {
                in = OpenHttpConnection(URL);
                bitmap = BitmapFactory.decodeStream(in, null, options);
                in.close();
            } catch (IOException e1) {
            }
            return bitmap;
        }
        */

        /*
        private static InputStream OpenHttpConnection(String strURL)
                throws IOException {
            InputStream inputStream = null;
            URL url = new URL(strURL);
            URLConnection conn = url.openConnection();

            try {
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = httpConn.getInputStream();
                }
            } catch (Exception ex) {
            }
            return inputStream;
        }
        */
    }
}
