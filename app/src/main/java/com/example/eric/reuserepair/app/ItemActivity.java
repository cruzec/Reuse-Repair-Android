package com.example.eric.reuserepair.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ItemFragment extends Fragment {
        ArrayAdapter<String> mItemAdapter;
        public ItemFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            String selectedCat = getActivity().getIntent().getExtras().getString("category");//test until we can figure out how to get selected category
            String allItemsString = null;
            String allCategoriesString = null;
            String allItemCategoriesString = null;
            JSONArray itemsArray = null;
            JSONArray categoryArray = null;
            JSONArray itemsCategoriesArray = null;

            GetItem allItems = new GetItem();
            GetCategory allCategories = new GetCategory();
            GetItemCategory allItemCategories = new GetItemCategory();


            String CID = null;
            ArrayList<String> selectedItemNumbers = new ArrayList<String>();
            ArrayList<String> data = new ArrayList<String>();
            try {
                allItemsString = allItems.execute().get();
                JSONObject itemsJSONObj = new JSONObject(allItemsString);
                itemsJSONObj = new JSONObject(itemsJSONObj.get("item").toString());
                itemsArray = itemsJSONObj.getJSONArray("records");


                allCategoriesString = allCategories.execute().get();
                JSONObject categoriesJSONObj = new JSONObject(allCategoriesString);
                categoriesJSONObj = new JSONObject(categoriesJSONObj.get("category").toString());
                categoryArray = categoriesJSONObj.getJSONArray("records");

                allItemCategoriesString = allItemCategories.execute().get();
                JSONObject ItemsCategoriesJSONObj = new JSONObject(allItemCategoriesString);
                ItemsCategoriesJSONObj = new JSONObject(ItemsCategoriesJSONObj.get("item-category").toString());
                itemsCategoriesArray = ItemsCategoriesJSONObj.getJSONArray("records");

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < categoryArray.length(); i++){
                try {
                    JSONArray lookingForSelected = categoryArray.getJSONArray(i);
                    String key = lookingForSelected.getString(1);
                    if(key.equals(selectedCat)){
                        CID = lookingForSelected.getString(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for(int i = 0; i < itemsCategoriesArray.length(); i++){
                try {
                    JSONArray lookingForCID = itemsCategoriesArray.getJSONArray(i);
                    String key = lookingForCID.getString(1);
                    if(key.equals(CID)){
                        selectedItemNumbers.add(lookingForCID.getString(0));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for(int i = 0; i < itemsArray.length(); i++){
                try {
                    JSONArray lookingForIID = itemsArray.getJSONArray(i);
                    String key = lookingForIID.getString(0);
                    if(selectedItemNumbers.contains(key)){
                        data.add(lookingForIID.getString(1));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Blank string which will be appended after GET request
            /*String[] data = {
                    "Harcoded item 1",
                    "Harcoded item 2",
                    "Harcoded item 3",
                    "Harcoded item 4",
                    "Harcoded item 5"
            };*/
            //List<String> item = new ArrayList<String>(Arrays.asList(data));

            // Create an ArrayAdapter for the blank category list to populate ListView
            mItemAdapter =
                    new ArrayAdapter<String>(
                            getActivity(),
                            R.layout.list_item_item,
                            R.id.list_item_item_textview,
                            data
                    );
            View rootView = inflater.inflate(R.layout.fragment_item, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_item);
            listView.setAdapter(mItemAdapter);
            final String finalAllItemsString = allItemsString;
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String item = mItemAdapter.getItem(position);
                    Intent intent = new Intent(getActivity(), BusinessActivity.class);
                    intent.putExtra("allItems", finalAllItemsString);
                    intent.putExtra("selectedItem", item);
                    startActivity(intent);
                }
            });
            return rootView;
        }
    }
}
