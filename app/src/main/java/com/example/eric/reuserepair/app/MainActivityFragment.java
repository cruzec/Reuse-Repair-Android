package com.example.eric.reuserepair.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

        return rootView;
    }
}
