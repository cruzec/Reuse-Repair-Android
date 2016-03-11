package com.example.eric.reuserepair.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Intent intent = getIntent();

        ArrayList<String> latLong = intent.getStringArrayListExtra("latLong");
        final String businesses = intent.getExtras().getString("businesses");

        // Check for location permission before finding user's last known coordinates
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
        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        double myLat = location.getLatitude();
        double myLng = location.getLongitude();
        LatLng myLatLng = new LatLng(myLat, myLng);
        mMap.addMarker(new MarkerOptions().position(myLatLng).title("My Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        double latitude = 0;
        double longitude = 0;

        for(int i = 0; i < latLong.size(); i++){
            if(!latLong.get(i).toString().equals("null")&& i % 3 == 0){
                latitude = Double.parseDouble(latLong.get(i).toString());
                longitude = Double.parseDouble(latLong.get(i+1).toString());
                LatLng latLng = new LatLng(latitude, longitude);
                String businessName = latLong.get(i+2);
                mMap.addMarker(new MarkerOptions().position(latLng).title(businessName));
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(44.564566, -123.262044), 12));

        // Make marker window clickable to show DetailActivity
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker m) {
                String markerName = m.getTitle();
                Intent intent = new Intent(MapsActivity.this, DetailActivity.class);
                intent.putExtra("allBusiness", businesses);
                intent.putExtra("business", markerName);
                startActivity(intent);
            }
        });

/*        if (location != null) {
            LatLng latLng = new LatLng(latitude, longitude);
            LatLng latLng2 = new LatLng(44.5779766, -123.261567);
            mMap.addMarker(new MarkerOptions().position(latLng).title("My Current Location"));
            mMap.addMarker(new MarkerOptions().position(latLng2).title("My Current Location2"));
            // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }
        else {
            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }*/
    }
}
