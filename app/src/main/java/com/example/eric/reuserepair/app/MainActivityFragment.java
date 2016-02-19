package com.example.eric.reuserepair.app;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Button button = (Button) rootView.findViewById(R.id.main_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                startActivity(intent);
            }
        });

        Button button2 = (Button) rootView.findViewById(R.id.link_button1);
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri uri1 = Uri.parse("http://site.republicservices.com/site/corvallis-or/en/documents/corvallisrecycledepot.pdf");
                Intent intent2 = new Intent(Intent.ACTION_VIEW, uri1);
                startActivity(intent2);
            }
        });

        Button button3 = (Button) rootView.findViewById(R.id.link_button2);
        button3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri uri2 = Uri.parse("http://site.republicservices.com/site/corvallis-or/en/documents/detailedrecyclingguide.pdf");
                Intent intent3 = new Intent(Intent.ACTION_VIEW, uri2);
                startActivity(intent3);
            }
        });

        return rootView;
    }
}
