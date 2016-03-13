package com.example.eric.reuserepair.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.TextView;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(this.getIntent().getExtras().getString("business"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment {

        private ImageView mImg;
        private TextView mAddress;
        private TextView mWebsite;
        private TextView mPhone;
        private TextView mHours;
        private TextView mRepair;

        private final String LOG_TAG = DetailFragment.class.getSimpleName();

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            int businessID = 0;
            ArrayList<String> data = new ArrayList<String>();
            String selectedBusiness = getActivity().getIntent().getExtras().getString("business");
            JSONArray businessRecords = null;
            JSONArray lookingForBID;
            double lat = 0;
            double lng = 0;

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            mAddress = (TextView) rootView.findViewById(R.id.address_view);
            mPhone = (TextView) rootView.findViewById(R.id.phone_view);
            mWebsite = (TextView) rootView.findViewById(R.id.site_view);
            mHours = (TextView) rootView.findViewById(R.id.hours_view);
            mRepair = (TextView) rootView.findViewById(R.id.repair_view);
            String address = null;
            String phoneNum = null;
            String website = null;
            String hours = null;

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

                        address = lookingForBID.getString(4);
                        if (address == null || address.equals("null")) {
                            address = "n/a";
                        }
                        mAddress.setText(address);

                        phoneNum = lookingForBID.getString(3);
                        if (phoneNum == null || phoneNum.equals("null")) {
                            phoneNum = "n/a";
                        }
                        mPhone.setText(phoneNum);

                        website = lookingForBID.getString(2);
                        if (website == null || website.equals("null")) {
                            website = "n/a";
                        }
                        mWebsite.setText(website);

                        hours = lookingForBID.getString(5);
                        if (hours == null || hours.equals("null")) {
                            hours = "n/a";
                        }
                        mHours.setText(hours);

                        if (lookingForBID.getInt(6) != 0) {
                            mRepair.setText("Yes");
                        }
                        else {
                            mRepair.setText("No");
                        }
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.v(LOG_TAG, "This string " + data);

            // Open dialer on click for business phone number
            final String fPhoneNum = phoneNum;
            if (phoneNum != null) {
                mPhone.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + fPhoneNum));
                        startActivity(callIntent);
                    }
                });
            }

            // Open browser on click for business website
            final String fWebsite = website;
            if (website != null) {
                mWebsite.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(fWebsite);
                        Intent intent3 = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent3);
                    }
                });
            }

            // Get Google Static Map image of location marker
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

            // Make Google Static Map image clickable
            final double fLat = lat;
            final double fLng = lng;
            final String fAddress = address;

            if (lat != 0.0 && lng != 0.0) {
                mImg.setOnClickListener(new View.OnClickListener() {
                    // Open Google Maps on click
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + fLat + ">,<" + fLng + ">?q=<" + fLat + ">,<" + fLng + ">(" + fAddress + ")"));
                        startActivity(intent);
                    }
                });
            }

            return rootView;
        }
    }
}
