package com.example.eric.reuserepair.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

/**
 * Created by Billy Kerns on 2/16/2016.
 */

/*
Code borrowed from: http://stackoverflow.com/questions/32567839/returned-location-is-always-null-in-googleapiclient
*/
public class BusinessActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    // private final String LOG_TAG = BusinessActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    static double myLat;
    static double myLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusCheck();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(this.getIntent().getExtras().getString("selectedItem"));
    }

    /*
    Code borrowed from http://stackoverflow.com/questions/25175522/how-to-enable-location-access-programatically-in-android
    */
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    // Prompts message if Locations is disabled
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Location is turned off. Would you like to enable it?")
                .setTitle("Use location?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    */

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

    /*
    @Override
    protected void onResume() {
        super.onResume();

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }
    */

    @Override
    protected void onPause(){
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        // Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();

        LocationRequest mLocationRequest;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //mLocationRequest.setSmallestDisplacement(0.1F);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"Connection Suspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"Connection Failed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        myLat = mLastLocation.getLatitude();
        myLng = mLastLocation.getLongitude();

        String lat = String.valueOf(myLat);
        String lng = String.valueOf(myLng);
        // Log.v(LOG_TAG, "LatLong: " + lat + " " + lng);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class BusinessFragment extends Fragment {

        PlaceAdapter mItemAdapter;
        private final String LOG_TAG = BusinessFragment.class.getSimpleName();

        public BusinessFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            ArrayList<String> BIDHolder = new ArrayList<String>();
            ArrayList<String> data = new ArrayList<String>();
            ArrayList<Place> places = new ArrayList<Place>();
            //ArrayList<Double> distance = new ArrayList<Double>();
            //final ArrayList<Pair<String, String>> latLong = new ArrayList<Pair<String, String>>();\
            final ArrayList<String> latLong = new ArrayList<String>();
            String allBusinessString = null;
            String allBusinessItemString;
            JSONArray businessArray = null;
            JSONArray itemBusinessArray = null;
            GetBusiness allBusiness = new GetBusiness();
            GetItemBusiness allItemBusiness = new GetItemBusiness();

            try {
                allBusinessString = allBusiness.execute().get();
                JSONObject businessJSONObj = new JSONObject(allBusinessString);
                businessJSONObj = new JSONObject(businessJSONObj.get("business").toString());
                businessArray = businessJSONObj.getJSONArray("records");

                allBusinessItemString = allItemBusiness.execute().get();
                JSONObject itemBusinessJSONObj = new JSONObject(allBusinessItemString);
                itemBusinessJSONObj = new JSONObject(itemBusinessJSONObj.get("item-business").toString());
                itemBusinessArray = itemBusinessJSONObj.getJSONArray("records");

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONArray itemsArray = null;
            String selectedItem = getActivity().getIntent().getExtras().getString("selectedItem");
            String selectedItemIID = null;
            JSONObject itemsJSONObj = null;

            try {
                itemsJSONObj = new JSONObject(getActivity().getIntent().getExtras().getString("allItems"));
                itemsJSONObj = new JSONObject(itemsJSONObj.get("item").toString());
                itemsArray = itemsJSONObj.getJSONArray("records");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < itemsArray.length(); i++){
                try {
                    JSONArray lookingForIID = itemsArray.getJSONArray(i);
                    String key = lookingForIID.getString(1);

                    if(key.equals(selectedItem)){
                        selectedItemIID = lookingForIID.getString(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for(int i = 0; i < itemBusinessArray.length(); i++){
                try {
                    JSONArray lookingForBID = itemBusinessArray.getJSONArray(i);
                    String key = lookingForBID.getString(0);
                    if(key.equals(selectedItemIID)){
                        //selectedItemIID = lookingForBID.getString(0);
                        BIDHolder.add(lookingForBID.getString(1));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for(int i = 0; i < businessArray.length(); i++){
                try {
                    JSONArray lookingForBName = businessArray.getJSONArray(i);
                    String key = lookingForBName.getString(0);
                    if(BIDHolder.contains(key)){
                        data.add(lookingForBName.getString(1));

                        if (myLat != 0.0 && myLng != 0.0) {
                            try {
                                Location locationA = new Location("point A");
                                locationA.setLatitude(myLat);
                                locationA.setLongitude(myLng);

                                String latS = lookingForBName.getString(7);
                                String lngS = lookingForBName.getString(8);
                                Location locationB = new Location("point B");
                                double d;

                                if (!latS.equals("null") && !lngS.equals("null")) {
                                    locationB.setLatitude(Double.parseDouble(lookingForBName.getString(7)));
                                    locationB.setLongitude(Double.parseDouble(lookingForBName.getString(8)));
                                    d = locationA.distanceTo(locationB) * 0.000621371192237334;
                                    // Log.v(LOG_TAG, "num1: " + d);
                                }
                                else {
                                    d = Double.POSITIVE_INFINITY;
                                    // Log.v(LOG_TAG, "num2: " + Double.POSITIVE_INFINITY);
                                }

                                Place newPlace = new Place(lookingForBName.getString(1), d);
                                places.add(newPlace);

                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Place newPlace = new Place(lookingForBName.getString(1), 0.0);
                            places.add(newPlace);
                        }

                        //latLong.add(new Pair<String, String>(lookingForBName.getString(7), lookingForBName.getString(8)));
                        latLong.add(lookingForBName.getString(7));
                        latLong.add(lookingForBName.getString(8));
                        latLong.add(lookingForBName.getString(1));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Collections.sort(places, new Comparator<Place>() {
               @Override
               public int compare(Place p1, Place p2) {
                   return Double.compare(p1.getDistance(), p2.getDistance());
               }
            });

            /*
            for (Place place_ : places) {
                Log.v(LOG_TAG, "Dis: " + place_.getName());
                Log.v(LOG_TAG, "Dis: " + place_.getDistance());
            }
            */

            mItemAdapter = new PlaceAdapter(getActivity(), places);
            View rootView = inflater.inflate(R.layout.fragment_business, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.listview_business);
            listView.setAdapter(mItemAdapter);

            final String finalAllBusinessString = allBusinessString;
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String item = mItemAdapter.getItem(position).getName();
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("allBusiness", finalAllBusinessString);
                    intent.putExtra("business", item);
                    startActivity(intent);
                }
            });

            // Floating action button that maps business markers on click
            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MapsActivity.class).putStringArrayListExtra("latLong", (ArrayList<String>) latLong);
                    intent.putExtra("businesses", finalAllBusinessString);
                    startActivity(intent);

                    /*Intent intent = new Intent(getActivity(), MapsActivity.class);
                    startActivity(intent);*/
                }
            });

            return rootView;
        }
    }
}