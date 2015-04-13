package com.sliit.ghanansachith.runningpal;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sachith on 4/10/2015.
 */
public class TrackListFragment extends Fragment implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final String ARG_ITEM_ID = "product_list";


    List<Track> tracks;

    Activity activity;
    ListView trackListView;

    TrackListAdapter trackListAdapter;

    SharedPreference sharedPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        sharedPreference = new SharedPreference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container,
                false);
        findViewsById(view);

        setTracks();

        trackListAdapter = new TrackListAdapter(activity, tracks);
        trackListView.setAdapter(trackListAdapter);
        trackListView.setOnItemClickListener(this);
        trackListView.setOnItemLongClickListener(this);
        return view;
    }

    private void setTracks() {

        TrackDBHandler trackDBHandler = new TrackDBHandler(getActivity());
        tracks = trackDBHandler.getAllTracks();
    }

    private void findViewsById(View view) {
        trackListView = (ListView) view.findViewById(R.id.list_product);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Product product = (Product) parent.getItemAtPosition(position);
        Toast.makeText(activity, product.toString(), Toast.LENGTH_LONG).show();


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                   int position, long arg3) {
       // ImageView button = (ImageView) view.findViewById(R.id.imgbtn_favorite);

      //  String tag = button.getTag().toString();
     //   if (tag.equalsIgnoreCase("grey")) {
     //       sharedPreference.addFavorite(activity, tracks.get(position));
            //   Toast.makeText(activity,
            //     activity.getResources().getString(R.string.add_favr),
            //     Toast.LENGTH_SHORT).show();

       //     button.setTag("red");
       //     button.setImageResource(R.drawable.heart_red);
     //   } else {
       //     sharedPreference.removeFavorite(activity, tracks.get(position));
       //     button.setTag("grey");
        //    button.setImageResource(R.drawable.heart_grey);
            // Toast.makeText(activity,
            //        activity.getResources().getString(R.string.remove_favr),
            //      Toast.LENGTH_SHORT).show();
      //  }

        return true;
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.app_name);
//        getActivity().getActionBar().setTitle(R.string.app_name);
        super.onResume();
    }


}
