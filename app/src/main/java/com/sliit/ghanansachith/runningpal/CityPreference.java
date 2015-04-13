package com.sliit.ghanansachith.runningpal;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    // Colombo as the default city
    public String getCity(){
        //coord

        return prefs.getString("city", "Colombo, Sri Lanka");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }

}