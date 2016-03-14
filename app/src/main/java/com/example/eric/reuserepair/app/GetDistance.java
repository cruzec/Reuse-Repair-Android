package com.example.eric.reuserepair.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.String;

/**
 * Created by Eric on 3/13/2016.
 */
public class GetDistance extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        // LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    private String calculateDistance(double lat, double lng) throws IOException {
        /*
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        if (location != null) {
            double myLat = location.getLatitude();
            double myLng = location.getLongitude();
            LatLng myLatLng = new LatLng(myLat, myLng);
        }
        */

        return null;
    }
}
