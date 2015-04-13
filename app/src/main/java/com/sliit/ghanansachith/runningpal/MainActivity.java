package com.sliit.ghanansachith.runningpal;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Vector;


public class MainActivity extends ActionBarActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    TabHost tabHost;

    private Fragment contentFragment;
    TrackListFragment pdtListFragment;
    //dialog prograss bar
    public ProgressBar pgb;
    public int progress = 0;
    android.os.Handler h = new android.os.Handler();

    //check avalability of sd card
    public boolean mExternalStorageAvailable = false;
    public boolean mExternalStorageWriteable = false;

    //cmara on
    public static int TAKE_PRICTURE = 1;
    public Uri imageUri;

    // Songs list
    //  public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    public int headSetSwich = 1;
    public ListView lv;

    public Vector<AlertDialog> dialogs = new Vector<AlertDialog>();

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
    public  MediaPlayer mp;
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

    View v41;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tabHost=(TabHost)findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1=tabHost.newTabSpec("TAB 1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Home");


        TabHost.TabSpec spec2=tabHost.newTabSpec("TAB 2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("History");

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);

        getSupportActionBar().setIcon(R.drawable.logo_menu_icon);
        getSupportActionBar().setTitle("RunningPAL");
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE);

        setMobileDataEnabled(getBaseContext(),true);

        // Mediaplayer
        mp = new MediaPlayer();

        songManager = new SongsManager();
        utils = new Utilities();

        ImageButton btnsample = (ImageButton)findViewById(R.id.imageButton);
        btnsample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),TrackRecorder.class));
            }
        });
        ImageButton InviteFriends = (ImageButton)findViewById(R.id.btnInviteFriends);
        InviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),InviteFriends.class));
            }
        });



        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (savedInstanceState == null) {


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WeatherFragment())
                    .commit();

        }

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
//set history distance
        TextView dist = (TextView) findViewById(R.id.textDist);/////////////////////////////////////////////////
       TrackDBHandler tdb = new TrackDBHandler(getApplicationContext());
        dist.setText(tdb.calculateTotalDistance());

      //  TrackDBHandler dbtr = new TrackDBHandler(getApplicationContext());
      //  List<Track> tr1 = dbtr.getAllTracks();
      /*  if(tr1!=null) {

            FragmentManager fragmentManager = getSupportFragmentManager();

		/*
		 * This is called when orientation is changed.
		 */
           /* if (savedInstanceState != null) {

                if (savedInstanceState.containsKey("content")) {
                    String content = savedInstanceState.getString("content");
                    //   if (content.equals(FavoriteListFragment.ARG_ITEM_ID)) {
                    // if (fragmentManager.findFragmentByTag(FavoriteListFragment.ARG_ITEM_ID) != null) {
                    //  setFragmentTitle(R.string.favorites);
                    //  contentFragment = fragmentManager
                    //        .findFragmentByTag(FavoriteListFragment.ARG_ITEM_ID);
                    // }
                    //  }
                }
                if (fragmentManager.findFragmentByTag(TrackListFragment.ARG_ITEM_ID) != null) {
                    pdtListFragment = (TrackListFragment) fragmentManager
                            .findFragmentByTag(TrackListFragment.ARG_ITEM_ID);
                    contentFragment = pdtListFragment;
                }
            } else {
                pdtListFragment = new TrackListFragment();
                //setFragmentTitle(R.string.app_name);
                switchContent(pdtListFragment, ProductListFragment.ARG_ITEM_ID);

                //favListFragment = new FavoriteListFragment();
                /// switchContentFav(favListFragment, FavoriteListFragment.ARG_ITEM_ID);
            }

        }*/
    }

    ////////////////////////////////////////////////////
    public void switchContent(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.popBackStackImmediate());

        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction();
            transaction.replace(R.id.content_frame, fragment, tag);
            //Only FavoriteListFragment is added to the back stack.
            if (!(fragment instanceof ProductListFragment)) {
                transaction.addToBackStack(tag);
            }
            transaction.commit();
            contentFragment = fragment;
        }
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
        ///change pla button icon
//close all dialogs
        closeDialogs();
//show alert view Again
        AlertDialog aldilog1 = main_music_player_view();
        aldilog1.setCanceledOnTouchOutside(true);
        mp.pause();
        btnPlay.setImageResource(R.drawable.btn_pause);
        aldilog1.show();
    }

    public void closeDialogs() {
        for (AlertDialog dialog : dialogs)
            if (dialog.isShowing()){ dialog.dismiss();};
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*public void switchContent(Fragment fragment, String tag) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    while (fragmentManager.popBackStackImmediate());

    if (fragment != null) {
        FragmentTransaction transaction = fragmentManager
                .beginTransaction();
        transaction.replace(R.id.content_frame, fragment, tag);
        //Only FavoriteListFragment is added to the back stack.
        if (!(fragment instanceof ProductListFragment)) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
        contentFragment = fragment;
    }
}*/
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id == R.id.action_menu_home){

            AlertDialog aldilog = main_menu_view();
            aldilog.setCanceledOnTouchOutside(true);
            dialogs.add(aldilog);
            aldilog.show();
        }else if(id == R.id.action_music){

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



                AlertDialog aldilog15 = main_music_player_view();
                aldilog15.setCanceledOnTouchOutside(true);
                dialogs.add(aldilog15);
                aldilog15.show();

            }else {
                Toast.makeText(getBaseContext(),"Cannot access it sd card access permission",Toast.LENGTH_LONG).show();
            }


        }else if(id == R.id.action_menu_tack_pic){



            AlertDialog aldilog1s = Camara_selection_menu_view();
            aldilog1s.setCanceledOnTouchOutside(true);
            dialogs.add(aldilog1s);
            aldilog1s.show();
        }

        return super.onOptionsItemSelected(item);
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

    private AlertDialog Camara_selection_menu_view()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater/
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        v =inflater.inflate(R.layout.dialog_choose_camara_option, null);
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


    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
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

    @Override
    public void onDestroy(){

        super.onDestroy();
        mp.release();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    protected void onResume() {
        super.onResume();
try {
    TextView dist = (TextView) findViewById(R.id.textDist);
    TrackDBHandler tdb = new TrackDBHandler(getApplicationContext());
    dist.setText(tdb.calculateTotalDistance());
}catch (Exception e){

}


        /////////////////////////////
      //  FragmentManager fragmentManager = getSupportFragmentManager();

		/*
		 * This is called when orientation is changed.
		 */

        TrackDBHandler dbtr = new TrackDBHandler(getApplicationContext());
       List<Track> tr1 = dbtr.getAllTracks();
        if(tr1!=null) {
            pdtListFragment = new TrackListFragment();
           // setFragmentTitle(R.string.app_name);
            switchContent(pdtListFragment, TrackListFragment.ARG_ITEM_ID);
        }
   // favListFragment = new FavoriteListFragment();
   /// switchContentFav(favListFragment, FavoriteListFragment.ARG_ITEM_ID);



        /////////////////////////

        //   setMobileDataEnabled(getBaseContext(),true);
        //   turnGPSOn();
        // TextView dataNoFoundText = (TextView)findViewById(R.id.textView18);
        //  dataNoFoundText.setText("");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new WeatherFragment())
                .commit();
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        //call the prograss bar when starts
        AlertDialog aldilog =dialogPrograssBar();
        dialogs.add(aldilog);//.show();
        aldilog.setCanceledOnTouchOutside(false);
        aldilog.show();

        gloalVariable glV = new gloalVariable();
        //gloalVariable glV = new gloalVariable();
        songsList = glV.getsongsList();
        //songsList = songManager.getPlayList();

    }
    /////////////////////////////////////////////////////////////////////////////////////////////music
    //



    //data enable method
    public void setMobileDataEnabled(Context context, boolean enabled) {


        try{

            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
