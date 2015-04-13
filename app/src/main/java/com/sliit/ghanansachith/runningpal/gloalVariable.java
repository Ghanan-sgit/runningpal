package com.sliit.ghanansachith.runningpal;

import android.app.Application;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sachith on 4/6/2015.
 */
public class gloalVariable extends Application {

    private static ArrayList<String> f ;

    private static MediaPlayer mp ;

    private static ArrayList<HashMap<String, String>> songsList;

    public static ArrayList<HashMap<String, String>> getsongsList() {
        return songsList;
    }

    public void setsongsList(ArrayList<HashMap<String, String>> songsList) {
        this.songsList=songsList;
    }

    public static ArrayList<String>  getImagesFronSDFolderList() {
        return f;
    }

    public void setImagesFronSDFolderList(ArrayList<String>  f) {
        this.f=f;
    }

    public static MediaPlayer  getMediaPlayer() {
        return mp;
    }

    public void setMediaPlayer(MediaPlayer  f) {
        this.mp=mp;
    }


}
