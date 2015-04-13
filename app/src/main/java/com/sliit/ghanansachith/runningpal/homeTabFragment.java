package com.sliit.ghanansachith.runningpal;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by Sachith on 3/6/2015.
 */
public class homeTabFragment extends Fragment {

    ImageButton c;


    public homeTabFragment(){


    }




    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tab_home,container,false);
        return rootView;

    }



}
