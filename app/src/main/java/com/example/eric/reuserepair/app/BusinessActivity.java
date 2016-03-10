package com.example.eric.reuserepair.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Billy Kerns on 2/16/2016.
 */
public class BusinessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(this.getIntent().getExtras().getString("selectedItem"));
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class BusinessFragment extends Fragment {
        ArrayAdapter<String> mItemAdapter;
        public BusinessFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ArrayList<String> BIDHolder = new ArrayList<String>();
            ArrayList<String> data = new ArrayList<String>();
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
                        //latLong.add(new Pair<String, String>(lookingForBName.getString(7), lookingForBName.getString(8)));
                        latLong.add(lookingForBName.getString(7));
                        latLong.add(lookingForBName.getString(8));
                        latLong.add(lookingForBName.getString(1));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            //List<String> item = new ArrayList<String>(Arrays.asList(data));

            // Create an ArrayAdapter for the blank category list to populate ListView
            mItemAdapter =
                    new ArrayAdapter<String>(
                            getActivity(),
                            R.layout.list_item_business,
                            R.id.list_item_business_textview,
                            data
                    );
            View rootView = inflater.inflate(R.layout.fragment_business, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.listview_business);
            listView.setAdapter(mItemAdapter);
            final String finalAllBusinessString = allBusinessString;
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String item = mItemAdapter.getItem(position);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("allBusiness", finalAllBusinessString);
                    intent.putExtra("business", item);
                    startActivity(intent);
                }
            });


            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MapsActivity.class).putStringArrayListExtra("latLong",  (ArrayList<String>) latLong);
                    startActivity(intent);

                    /*Intent intent = new Intent(getActivity(), MapsActivity.class);
                    startActivity(intent);*/
                }
            });

            return rootView;
        }
    }
}