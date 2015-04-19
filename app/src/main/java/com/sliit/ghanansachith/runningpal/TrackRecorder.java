package com.sliit.ghanansachith.runningpal;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

public class TrackRecorder extends FragmentActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    /** Variables to be used over are declared here */

    //dialog prograss bar
    public ProgressBar pgb;
    public int progress = 0;
    android.os.Handler h = new android.os.Handler();

    public Vector<AlertDialog> dialogs = new Vector<AlertDialog>();

    //cmara on
    public static int TAKE_PRICTURE = 1;
    public Uri imageUri;


    //check avalability of sd card
    public boolean mExternalStorageAvailable = false;
    public boolean mExternalStorageWriteable = false;

    //UI Elements
    Button btnRecord, btnStop;
    TextView txtDistance, txtSpeed, distanceTextView;
    Chronometer chronometer;

    //Map Related Stuff
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ArrayList<LatLng> listLatLng = new ArrayList<LatLng>();
    int arrayLoc=0;

    //GPS Related Stuff
    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private String locationProvider;


    // 0 to not draw polyline. 1 to draw
    int setDrawOption = 0;
    PolylineOptions polylineOptions = new PolylineOptions().color(Color.RED).width(5);


    //Calculation variables
    long timeStarted = 0;
    long timeWhenStopped = 0;
    double speed = 0.0;
    double distance = 0.0;

    String mapSnapshotLocation;
    String fullImagePath;
    String fileName;

    public int headSetSwich = 1;
    public ListView lv;



    public ImageButton btnPlay;
    public ImageButton btnForward;
    public ImageButton btnBackward;
    public ImageButton btnNext;
    public ImageButton btnPrevious;
    // private ImageButton btnPlaylist;
    public ImageButton btnRepeat;
    public ImageButton btnShuffle;
    public SeekBar songProgressBar;
    public TextView songTitleLabel;
    public TextView songCurrentDurationLabel;
    public TextView songTotalDurationLabel;
    // Media Player
    public MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    public Handler mHandler = new Handler();;
    public SongsManager songManager;
    public Utilities utils;
    public int seekForwardTime = 5000; // 5000 milliseconds
    public int seekBackwardTime = 5000; // 5000 milliseconds
    public int currentSongIndex = 0;
    public boolean isShuffle = false;
    public boolean isRepeat = false;
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    public boolean isPausedInCall = false;
    public PhoneStateListener phoneStateListener;
    public TelephonyManager telephonyManager;
    public AlertDialog.Builder builderMusicPlayrViewFristTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_recorder);

        distanceTextView = (TextView) findViewById(R.id.distanceLabelTextView);

        chronometer = (Chronometer) findViewById(R.id.chronometer);
        //chronometer.setFormat("H:MM:SS");

        //create references to elements in layout
        btnRecord = (Button) findViewById(R.id.btnStartRecord);
        btnRecord.setBackgroundColor(Color.GREEN);

        btnStop = (Button) findViewById(R.id.btnStopRecord);
        btnStop.setBackgroundColor(Color.RED);

        txtDistance = (TextView) findViewById(R.id.distanceTimerTextView);
        txtSpeed = (TextView) findViewById(R.id.speedTextView);

        btnRecord.setOnClickListener(startRecordingListener);
        btnStop.setOnClickListener(stopRecordingListener);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if(setDrawOption==1) {


                    //draw path on map
                    LatLng tempPosition = new LatLng(location.getLatitude(), location.getLongitude());

                    //get the distance
                    polylineOptions.add(tempPosition);
                    listLatLng.add(arrayLoc++, tempPosition);
                    mMap.addPolyline(polylineOptions);
                    float[] results = new float[1];
                    Location.distanceBetween(listLatLng.get(arrayLoc - 2).latitude, listLatLng.get(arrayLoc - 2).longitude, listLatLng.get(arrayLoc - 1).latitude, listLatLng.get(arrayLoc - 1).longitude, results);
                    distance += results[0];


                    if(distance<1000) {
                        distanceTextView.setText("Distance Travelled in m");
                        txtDistance.setText(String.format("%.2f", distance));
                    } else {
                        distanceTextView.setText("Distance Travelled in km)");
                        txtDistance.setText(String.format("%.2f", distance/1000));
                    }

                    //display speed
                    double distInKm = distance/1000;
                    double timeElapsed = ((SystemClock.elapsedRealtime()-chronometer.getBase())/3600000.0);
                    Toast.makeText(getApplicationContext(),Double.toString(timeElapsed),Toast.LENGTH_SHORT).show();
                    speed=distInKm/timeElapsed;
                    txtSpeed.setText(String.format("%.2f", speed));
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        setUpMapIfNeeded();



//////////////////////////////////////////////////
        ImageButton btnRecPic = (ImageButton)findViewById(R.id.btnRecord_pic);
        btnRecPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog aldilog1s = Camara_selection_menu_view();
                aldilog1s.setCanceledOnTouchOutside(true);
                dialogs.add(aldilog1s);
                aldilog1s.show();
            }
        });

        ImageButton btnRecMain = (ImageButton)findViewById(R.id.btn_Record_MainView);
        btnRecMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog aldilog = main_menu_view();
                aldilog.setCanceledOnTouchOutside(true);
                dialogs.add(aldilog);
                aldilog.show();
            }
        });

        ImageButton btnRecMusic = (ImageButton)findViewById(R.id.btn_Record_Music);
        btnRecMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
            LayoutInflater inflater5efs = this.getLayoutInflater();
            View layout2 = inflater5efs.inflate(R.layout.dialog_music_player,(ViewGroup) findViewById(R.id.musicPlayerIDLayout));
            btnPlay = (ImageButton) layout2.findViewById(R.id.btnPlay);
            if(mp.isPlaying()){
                if(mp!=null){

                    // Changing button image to play button
                    btnPlay.setImageResource(R.drawable.btn_pause);
                }
            }else{
                // Resume song
                if(mp!=null){

                    // Changing button image to pause button
                    btnPlay.setImageResource(R.drawable.btn_play);
                }
            }*/

                //check weather external storage is avalable to read
                String state = Environment.getExternalStorageState();

                if (Environment.MEDIA_MOUNTED.equals(state)) {
// We can read and write the media
                    mExternalStorageAvailable = mExternalStorageWriteable = true;
                } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
// We can only read the media
                    mExternalStorageAvailable = true;
                    mExternalStorageWriteable = false;
                } else {
// Something else is wrong. It may be one of many other states, but all we need
//  to know is we can neither read nor write
                    mExternalStorageAvailable = mExternalStorageWriteable = false;
                }
//check weather external storage is avalable to read

                if(mExternalStorageAvailable==true && mExternalStorageAvailable==true){



                    AlertDialog aldilog1 = main_music_player_view();
                    aldilog1.setCanceledOnTouchOutside(true);
                    dialogs.add(aldilog1);
                    aldilog1.show();

                }else {
                    Toast.makeText(getBaseContext(),"Cannot access it sd card access permission",Toast.LENGTH_LONG).show();
                }

            }
        });

        // Mediaplayer
        mp = new MediaPlayer();

        songManager = new SongsManager();
        utils = new Utilities();

        ///music stop when call comes

        LayoutInflater inflater5 = this.getLayoutInflater();
        View layoute = inflater5.inflate(R.layout.dialog_music_player,(ViewGroup) findViewById(R.id.musicPlayerIDLayout));

        btnPlay = (ImageButton) layoute.findViewById(R.id.btnPlay);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {


            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
//INCOMING call
//do all necessary action to pause the audio
                    if(mp!=null){//check mp
                        //   setPlayerButton(true, false, true);

                        if(mp.isPlaying()){

                            mp.pause();
                            btnPlay.setImageResource(R.drawable.btn_pause);
                        }
                    }

                } else if(state == TelephonyManager.CALL_STATE_IDLE) {
//Not IN CALL
//do anything if the phone-state is idle
                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
//A call is dialing, active or on hold
//do all necessary action to pause the audio
//do something here
                    if(mp!=null){//check mp
                        // setPlayerButton(true, false, true);

                        if(mp.isPlaying()){

                            mp.pause();
                            btnPlay.setImageResource(R.drawable.btn_pause);
                        }
                    }
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };//end PhoneStateListener

        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

/////////////////////////////////pause music when haedphone Unpluged


        // unregisterReceiver(headsetReceiver);

        registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

    }
    private BroadcastReceiver headsetReceiver =  new BroadcastReceiver() {
        private boolean headSetConnected =  false;
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("state")){
                if(headSetConnected && intent.getIntExtra("state",0)==0){
                    headSetConnected = false;
                    headSetSwich = 0;

                }else if(!headSetConnected && intent.getIntExtra("state",0)==1){

                    headSetConnected = true;
                    headSetSwich = 1;
                }

                switch (headSetSwich){
                    case (0):
                        headSetDisconnected();
                        break;
                    case (1):
                        //  headSetConnected();
                        break;
                }
            }
        }
    };
    private void headSetDisconnected(){

//close all dialogs
        closeDialogs();
//show alert view Again
        AlertDialog aldilog1 = main_music_player_view();
        aldilog1.setCanceledOnTouchOutside(true);
        ///change pla button icon
        btnPlay.setImageResource(R.drawable.btn_pause);
        mp.pause();
        aldilog1.show();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////music
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(currentSongIndex);
        }

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }



    //dialog Progras bar
    private AlertDialog dialogPrograssBar()/////////////////////////////////////////////
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater/
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layoutx
        v =inflater.inflate(R.layout.sialog_prograss_bar, null);
        builder.setView(v);

        pgb = (ProgressBar)v.findViewById(R.id.progressBar);

        pgb.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);


        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0;i<10;i++){
                    progress += 20;
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            pgb.setProgress(progress);
                            if(progress == pgb.getMax()){
                                //pgb.setVisibility(View.VISIBLE);
                                gloalVariable glV = new gloalVariable();
                                ////////////////////////////////////////////////////////


                                songManager = new SongsManager();

                                songsList = new ArrayList<HashMap<String, String>>();
                                songsList = songManager.getPlayList();


                                glV.setsongsList(songsList);


                            }
                        }
                    });
                    try {
                        Thread.sleep(700);
                        closeDialogs();

                    }catch (InterruptedException e){

                    }

                }
            }
        }).start();



        // Add action buttons
        return builder.create();



    }


    /**
     * On Song Playing completed
     * if repeat is ON play same song again
     * if shuffle is ON play random song
     * */
    @Override
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else{
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (songsList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    private AlertDialog musicPlayerListView(){

        AlertDialog.Builder builder343 = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater5 = this.getLayoutInflater();
        View layout56 = inflater5.inflate(R.layout.playlist,(ViewGroup) findViewById(R.id.dialogPlaList));

        // selecting single ListView item
        lv = (ListView)layout56.findViewById(R.id.list); //getListView();

        // View layoutPlaListItem = inflater5.inflate(R.layout.playlist_item,(ViewGroup) findViewById(R.id.playListItemodel));
        ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();

        // SongsManager plm = new SongsManager();
        // get all songs from sdcard
        //songsList = plm.getPlayList();

        // looping through playlist
        for (int i = 0; i < songsList.size(); i++) {
            // creating new HashMap
            HashMap<String, String> song = songsList.get(i);

            // adding HashList to ArrayList
            songsListData.add(song);
        }

        // Adding menuItems to ListView
        ListAdapter adapter = new SimpleAdapter(getBaseContext(), songsListData,R.layout.playlist_item, new String[] { "songTitle" }, new int[] {R.id.songTitle });

        lv.setAdapter(adapter);


        // listening to single listitem click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting listitem index
                int songIndex = position;

                // Starting new intent

                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.btn_play);
                        playSong(songIndex);
                    }
                }else{
                    // Resume song
                    if(mp!=null){
                        //  mp.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.btn_pause);
                        playSong(songIndex);
                    }
                }

            }
        });


        builder343.setView(layout56)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        dialog.dismiss();
                        AlertDialog alDilg3 = main_music_player_view();
                        alDilg3.setCanceledOnTouchOutside(true);
                        alDilg3.show();

                    }
                });
        return builder343.create();
    }
    public AlertDialog main_music_player_view(){


        builderMusicPlayrViewFristTime = new AlertDialog.Builder(this);
        // Get the layout inflater

        LayoutInflater inflater5 = this.getLayoutInflater();
        View layout = inflater5.inflate(R.layout.dialog_music_player,(ViewGroup) findViewById(R.id.musicPlayerIDLayout));
        builderMusicPlayrViewFristTime.setView(layout)

                .setNegativeButton("Play List", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // dialog.dismiss();
                        AlertDialog alDilog2 = musicPlayerListView();
                        alDilog2.setCanceledOnTouchOutside(true);
                        alDilog2.show();

                    }
                });
/////////////////////////////////////////////////////////////////Music oncreate//////////////////////////////////
// All player buttons
        btnPlay = (ImageButton) layout.findViewById(R.id.btnPlay);
        btnForward = (ImageButton) layout.findViewById(R.id.btnForward);
        btnBackward = (ImageButton)layout. findViewById(R.id.btnBackward);
        btnNext = (ImageButton) layout.findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) layout.findViewById(R.id.btnPrevious);
        // btnPlaylist = (ImageButton) layout.findViewById(R.id.btnPlaylist);
        btnRepeat = (ImageButton) layout.findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) layout.findViewById(R.id.btnShuffle);
        songProgressBar = (SeekBar) layout.findViewById(R.id.songProgressBar);
        songTitleLabel = (TextView)layout.findViewById(R.id.songTitle);
        songCurrentDurationLabel = (TextView) layout.findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) layout.findViewById(R.id.songTotalDurationLabel);


// Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this); // Important


        // By default play first song
        // playSong(0);
        /**
         * Play button click event
         * plays a song and changes button to pause image
         * pauses a song and changes button to play image
         * */
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                }else{
                    // Resume song
                    if(mp!=null){
                        mp.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }

            }
        });



        /**
         * Forward button click event
         * Forwards song specified seconds
         * */
        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(currentPosition + seekForwardTime <= mp.getDuration()){
                    // forward song
                    mp.seekTo(currentPosition + seekForwardTime);
                }else{
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }
            }
        });

        /**
         * Backward button click event
         * Backward song to specified seconds
         * */
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                    mp.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                    mp.seekTo(0);
                }

            }
        });

        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
                if(currentSongIndex < (songsList.size() - 1)){
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                }else{
                    // play first song
                    playSong(0);
                    currentSongIndex = 0;
                }

            }
        });

        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(currentSongIndex > 0){
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                }else{
                    // play last song
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }
                if(mp.getCurrentPosition() > 15000){
                    playSong(currentSongIndex);
                }

            }
        });

        /**
         * Button Click event for Repeat button
         * Enables repeat flag to true
         * */
        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });

        /**
         * Button Click event for Shuffle button
         * Enables shuffle flag to true
         * */
        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });

        return builderMusicPlayrViewFristTime.create();
/////////////////////////////////////////////////////////////////Music oncreate//////////////////////////////////
    }
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            Log.d("Progress", "" + progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    public void  playSong(int songIndex){
        // Play song
        try {
            mp.reset();

            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();

            // Displaying Song title
            String songTitle = songsList.get(songIndex).get("songTitle");
            songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.btn_pause);

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    View v;
    private AlertDialog main_menu_view()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater/
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        v =inflater.inflate(R.layout.dialog_menu_home, null);
        builder.setView(v);
        Button btnMenuMainView1 =(Button) v.findViewById(R.id.btnMenuMainView);
        Button btnMenuMapView1 =(Button) v.findViewById(R.id.btnMenuMapView);

        Button btnButtonAboutUs1 =(Button) v.findViewById(R.id.btnButtonAboutUs);
        Button btnButtonInviteFriend =(Button) v.findViewById(R.id.btnButtonInviteFriend);


        btnMenuMainView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),MainActivity.class));
            }
        });
        btnMenuMapView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),TrackRecorder.class));
            }
        });

        btnButtonAboutUs1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),AboutUsActivity.class));
            }
        });
        btnButtonInviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),InviteFriends.class));
            }
        });
        // Add action buttons
        return builder.create();

    }
    private AlertDialog Camara_selection_menu_view()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater/
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
       View v =inflater.inflate(R.layout.dialog_choose_camara_option, null);
        builder.setView(v);
        Button btnfrontCam =(Button) v.findViewById(R.id.btnfrontCamara);
        Button btnackCam =(Button) v.findViewById(R.id.btnBackCamara);



        btnfrontCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialogs();
                takePhoto(v);
            }
        });
        btnackCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takePhoto(v);
            }
        });

        // Add action buttons
        return builder.create();

    }

    private void takePhoto(View v){

        Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
        File phto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"picture.jpg");

        imageUri = Uri.fromFile(phto);
        i.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(i,TAKE_PRICTURE);
    }
    public void closeDialogs() {
        for (AlertDialog dialog : dialogs)
            if (dialog.isShowing()){ dialog.dismiss();};
    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        AlertDialog aldilog =dialogPrograssBar();
        dialogs.add(aldilog);//.show();
        aldilog.setCanceledOnTouchOutside(false);
        aldilog.show();

        gloalVariable glV = new gloalVariable();
        //gloalVariable glV = new gloalVariable();
        songsList = glV.getsongsList();
        //songsList = songManager.getPlayList();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapTracker)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        startListening();

        // Get current location's latitude and longitude and move camera to that location
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(getLocation()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        stopListening();
    }

    View.OnClickListener startRecordingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (btnRecord.getText().equals("Start")) {

                distance=0.0;
                speed=0.0;
                setDrawOption=1;
                startListening();

                //change button to pause
                btnRecord.setText("Pause");
                btnRecord.setBackgroundColor(Color.rgb(255, 102, 0));

                //activate map to start recording
                LatLng currentLoc = getLocation();
                listLatLng.add(arrayLoc++,currentLoc);
                polylineOptions.add(currentLoc);
                mMap.addMarker(new MarkerOptions().position(currentLoc).title("Starting Point").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));



                //start timer
                timeStarted = SystemClock.elapsedRealtime();
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();

            } else if (btnRecord.getText().equals("Pause")) {
                setDrawOption=0;
                stopListening();
                btnRecord.setText("Resume");
                btnRecord.setBackgroundColor(Color.YELLOW);
                timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();

            } else if(btnRecord.getText().equals("Resume")) {
                setDrawOption=1;
                listLatLng.add(arrayLoc++, getLocation());
                startListening();
                btnRecord.setText("Pause");
                btnRecord.setBackgroundColor(Color.rgb(255, 102, 0));
                chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                chronometer.start();
            }
        }
    };

    View.OnClickListener stopRecordingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!btnRecord.getText().equals("Start")) {
                btnRecord.setText("Start");
                btnRecord.setBackgroundColor(Color.GREEN);
                chronometer.setBase(SystemClock.elapsedRealtime());

//                LatLng lastLoc = getLocation();
                LatLng lastLoc = listLatLng.get(arrayLoc-1);

                //zoom between start and ending points
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(listLatLng.get(0));
                builder.include(lastLoc);
                LatLngBounds bounds = builder.build();

                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
                mMap.addMarker(new MarkerOptions().position(lastLoc).title("Ending Point").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                //stop receiving updates
                setDrawOption = 0;
                stopListening();


                //Get Snapshot of Google Map

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'_'HH-mm-ss");
                fileName = "track_"+sdf.format(new Date());

                CaptureMapScreen();

                fullImagePath = Environment.getExternalStorageDirectory() + "/RunningPal/Snapshots/" + fileName;



                // Enter values to the database
                String averageSpeed = txtSpeed.getText().toString();

                long timeSpent = SystemClock.elapsedRealtime() - timeStarted;

                int hours = (int) (timeSpent / 3600000);
                int minutes = (int) (timeSpent - hours * 3600000) / 60000;
                int seconds = (int) (timeSpent - hours * 3600000 - minutes * 60000) / 1000;

                String timeElapsed = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String today = sdf2.format(new Date());

                DecimalFormat df = new DecimalFormat("#######.##");
                double travelledDistance = Double.parseDouble(df.format(distance));

                TrackDBHandler tdb = new TrackDBHandler(getApplicationContext());
                Track track = new Track(today,travelledDistance,timeElapsed,averageSpeed,fullImagePath);
                tdb.insertTrack(track);

                //stop timer
                chronometer.stop();

                Toast.makeText(getApplicationContext(),"Track recorded in database successfully!", Toast.LENGTH_SHORT).show();

//            }
            }
        }
    };



    public LatLng getLocation() {
        Location myLocation = null;
        LatLng currentLocation = null;
        //      if(locationManager != null ) {

        GPSTracker gps = new GPSTracker(getBaseContext());

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            //  myLocation = locationManager.getLastKnownLocation(locationProvider);
            //  double latitude = myLocation.getLatitude();
            // double longitude = myLocation.getLongitude();
            currentLocation = new LatLng(latitude, longitude);

        }else{
           /// currentLocation = new LatLng(0,0);

        }


        //   }

        return currentLocation;
    }

    public void startListening() {
        if (this.locationManager == null) {
            this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        final Criteria criteria = new Criteria();

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        locationProvider = this.locationManager.getBestProvider(criteria, true);


        this.locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 2000, 2, this.locationListener);
    }

    public void stopListening() {
        if (this.locationManager != null && this.locationListener != null)
        {
            this.locationManager.removeUpdates(this.locationListener);
        }

        this.locationManager = null;
    }



    public void CaptureMapScreen()
    {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

            @Override
            public void onSnapshotReady(Bitmap snapshot) {

                mapSnapshotLocation = TrackDBHandler.storeImage(snapshot, fileName);

            }
        };

        mMap.snapshot(callback);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record_track, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

       /*  if(id == R.id.action_menu_home){

           AlertDialog aldilog = main_menu_view();
            aldilog.setCanceledOnTouchOutside(true);
            dialogs.add(aldilog);
            aldilog.show();
        }else if(id == R.id.action_music){



            //check weather external storage is avalable to read
            String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state)) {
// We can read and write the media
                mExternalStorageAvailable = mExternalStorageWriteable = true;
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
// We can only read the media
                mExternalStorageAvailable = true;
                mExternalStorageWriteable = false;
            } else {
// Something else is wrong. It may be one of many other states, but all we need
//  to know is we can neither read nor write
                mExternalStorageAvailable = mExternalStorageWriteable = false;
            }
//check weather external storage is avalable to read

            if(mExternalStorageAvailable==true && mExternalStorageAvailable==true){



                AlertDialog aldilog1 = main_music_player_view();
                aldilog1.setCanceledOnTouchOutside(true);
                dialogs.add(aldilog1);
                aldilog1.show();

            }else {
                Toast.makeText(getBaseContext(),"Cannot access it sd card access permission",Toast.LENGTH_LONG).show();
            }


        }else if(id == R.id.action_menu_tack_pic){



            AlertDialog aldilog1s = Camara_selection_menu_view();
            aldilog1s.setCanceledOnTouchOutside(true);
            dialogs.add(aldilog1s);
            aldilog1s.show();
        }*/

        return super.onOptionsItemSelected(item);
    }
}





/*

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrackRecorder extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_recorder);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

*
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

*
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.


    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
*/
