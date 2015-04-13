package com.sliit.ghanansachith.runningpal;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteFetch {
    //"http://api.openweathermap.org/data/2.5/weather?lat=%s&units=metric&lon=%s&units=metric"
//"http://api.openweathermap.org/data/2.5/weather?q=COLOMBO,Sri Lanka"

    //api.openweathermap.org/data/2.5/weather?lat=35&lon=139
    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&units=metric&lon=%s&units=metric";

    public static JSONObject getJSON(Context context,double latt,double longt) {

        try {

            URL url = new URL(String.format(OPEN_WEATHER_MAP_API,latt,longt));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key",
                    context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            //JSONObject data = new JSONObject((json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1)));
            JSONObject data = new JSONObject(json.toString());


           // JSONArray jsonArray = new JSONArray(json);
            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
e.printStackTrace();
            return null;

        }
    }

}