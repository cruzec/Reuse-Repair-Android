package com.example.eric.reuserepair.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.analytics.ecommerce.Product;

import java.util.ArrayList;

/**
 * Created by Eric on 3/14/2016.
 */

/*
Code borrowed from: https://github.com/codepath/android-custom-array-adapter-demo/blob/master/app/src/main/java/com/codepath/example/customadapterdemo/CustomUsersAdapter.java
*/
public class PlaceAdapter extends ArrayAdapter<Place> {

    public PlaceAdapter(Context context, ArrayList<Place> places) {
        super(context, 0, places);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Place place = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_business, parent, false);
        }

        TextView placeName = (TextView) convertView.findViewById(R.id.placeName);
        TextView placeDistance = (TextView) convertView.findViewById(R.id.placeDistance);

        placeName.setText(place.getName());

        // Round to nearest decimal
        double d = (double) Math.round(place.getDistance() * 100 / 10) / 10;
        if (d == 0.0 || d > 100000) {
            placeDistance.setText("n/a");
        } else {
            placeDistance.setText(d + " miles");
        }


        return convertView;
    }
}
