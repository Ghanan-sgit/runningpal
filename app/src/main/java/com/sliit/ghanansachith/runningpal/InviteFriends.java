package com.sliit.ghanansachith.runningpal;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;



public class InviteFriends extends ActionBarActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{
    //galary item
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private ImageAdapter imageAdapter;
    ArrayList<String> f = new ArrayList<String>();// list of file paths
    File[] listFile;

    Bitmap imageHolder=null;
    // View v;

    //check avalability of sd card
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;

    View v41;
    //dialog prograss bar

    ProgressBar pgb;
    int progress = 0;
    android.os.Handler h = new android.os.Handler();

    //cmara on
    private static int TAKE_PRICTURE = 1;
    private Uri imageUri;

    private Vector<AlertDialog> dialogs = new Vector<AlertDialog>();

    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    // private ImageButton btnPlaylist;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    // Media Player
    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private SongsManager songManager;
    private Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    private boolean isPausedInCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    AlertDialog.Builder builderMusicPlayrViewFristTime;

    private int headSetSwich = 1;
    ListView lv;

    LayoutInflater inflaterNewOne;

    EditText commentFrist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        inflaterNewOne = this.getLayoutInflater();

        getSupportActionBar().setIcon(R.drawable.logo_menu_icon);
        getSupportActionBar().setTitle("RunningPAL");
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE);

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




     /*   Button btnSlectMap = (Button)findViewById(R.id.btnSelectMap);

        btnSlectMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /// AlertDialog alDialog = ImageGalary_view_dialog();
               // alDialog.setCanceledOnTouchOutside(true);
               // alDialog.show();
            }
        });*/

        Button btnSlectShare = (Button)findViewById(R.id.btnSelectMApToShare);

        btnSlectShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alDialog = ImageGalary_view_dialog();
                alDialog.setCanceledOnTouchOutside(true);
                alDialog.show();

            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////grid view selected image
        commentFrist=  (EditText)findViewById(R.id.txtComment123);

        Button fbShareSecond = (Button)findViewById(R.id.btnFbShareInFrist);
        fbShareSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                share("facebook",null,commentFrist.getText().toString());
            }
        });

        Button gmailShareSecond = (Button)findViewById(R.id.btngmailShareInFrist);
        gmailShareSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                share("gmail",null,commentFrist.getText().toString().toString());

            }
        });

        Button twitterShareSecond = (Button)findViewById(R.id.btnTwitterShareInFrist);
        twitterShareSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                share("com.twitter.android",null,commentFrist.getText().toString());

            }
        });

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

    public void closeDialogs() {
        for (AlertDialog dialog : dialogs)
            if (dialog.isShowing()){ dialog.dismiss();};
    }


    //sharing on social media

    void share(String nameApp, String imagePath,String textContent) {

        try
        {

            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("image/jpeg");
            List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
            if (!resInfo.isEmpty()){
                Intent targetedShare=null;
                for (ResolveInfo info : resInfo) {
                    targetedShare = new Intent(android.content.Intent.ACTION_SEND);
                    targetedShare.setType("image/jpeg"); // put here your mime type
                    if (info.activityInfo.packageName.toLowerCase().contains(nameApp) || info.activityInfo.name.toLowerCase().contains(nameApp)) {
                        targetedShare.putExtra(Intent.EXTRA_SUBJECT, "RunningPAL Hangouts Invite");
                        targetedShare.putExtra(Intent.EXTRA_TEXT,textContent);
                        // targetedShare.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)) );
                        targetedShare.setPackage(info.activityInfo.packageName);
                        targetedShareIntents.add(targetedShare);
                    }

                }
                Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                startActivity(chooserIntent);
            }
        }
        catch(Exception e){
            Log.v("VM","Exception while sending image on" + nameApp + " "+  e.getMessage());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invite_friends, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_menu_home){

            AlertDialog aldilog = main_menu_view();
            aldilog.setCanceledOnTouchOutside(true);
            dialogs.add(aldilog);
            aldilog.show();
        }else if(id == R.id.action_music){

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
            }

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

    private AlertDialog main_music_player_view(){


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


    @Override
    protected void onResume() {
        super.onResume();
        getFromSdcard();
        gloalVariable glV = new gloalVariable();

        glV.setImagesFronSDFolderList(f);
        songsList = glV.getsongsList();

        // GridView imagegrid = (GridView) v.findViewById(R.id.PhoneImageGrid);
        // imageAdapter = new ImageAdapter();
        // imagegrid.setAdapter(imageAdapter);

    }


    /////////////////////////////////image galary

    private AlertDialog ImageGalary_view_dialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        v =inflater.inflate(R.layout.dialog_image_galary, null);
        builder.setView(v);

        // getFromSdcard();
        GridView imagegrid = (GridView) v.findViewById(R.id.PhoneImageGrid);
        imageAdapter = new ImageAdapter();
        imagegrid.setAdapter(imageAdapter);



        imagegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

//startActivity(new Intent(getBaseContext(),MainActivity.class));


                View v52 =inflaterNewOne.inflate(R.layout.activity_invite_friends,null);
                ImageView mImage = (ImageView)v52.findViewById(R.id.imageView);

                String imagePath = f.get(position);

                EditText comment = (EditText)findViewById(R.id.txtComment123);


                Intent i4 = new Intent(InviteFriends.this,InviteFriends2.class);
                i4.putExtra("imageResource",imagePath);
                i4.putExtra("comment",comment.getText());
                startActivity(i4);
                //  closeDialogs();

                //    imageHolder = BitmapFactory.decodeFile(imagePath);


                //   mImage.setImageBitmap(imageHolder);

                //  mImage.setImageBitmap();//.setImageUri(imageUri);
                //   mImage.setImageBitmap(decodeSampledBitmapFromFile(imagePath, 500, 250));
//startActivity(new Intent(InviteFriends.this,InviteFriends.class));
                // Uri imageUri = Uri.parse(imagePath);

                // mImage.setImageBitmap(BitmapFactory.decodeFile(listFile[position].getAbsolutePath()));

                // mImage.setImageResource(Constants.mThumbIds[index]);
            }
        });


//  getFromSdcard();




        // Add action buttons
        return builder.create();

    }

    public static Bitmap decodeSampledBitmapFromFile(String path,
                                                     int reqWidth, int reqHeight) { // BEST QUALITY MATCH

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }

        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }


        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }
    public void getFromSdcard()
    {
        File file= new File(android.os.Environment.getExternalStorageDirectory(),"RunningPal/Snapshots/");

        if (file.isDirectory())
        {
            listFile = file.listFiles();


            for (int i = 0; i < listFile.length; i++)
            {

                f.add(listFile[i].getAbsolutePath());

            }
        }
    }


    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return f.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.gelleryitem, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }


            Bitmap myBitmap1 = BitmapFactory.decodeFile(f.get(position));
            // holder.imageview.setImageResource(mThumbIds[position]);
            holder.imageview.setImageBitmap(myBitmap1);
            return convertView;
        }


    }
    class ViewHolder {
        ImageView imageview;
        int possiion;

    }
}
