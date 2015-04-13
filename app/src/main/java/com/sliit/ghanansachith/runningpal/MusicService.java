package com.sliit.ghanansachith.runningpal;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Created by Sachith on 3/10/2015.
 */
public class MusicService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener,
                                                     MediaPlayer.OnErrorListener,
                                                     MediaPlayer.OnSeekCompleteListener,
                                                     MediaPlayer.OnInfoListener,
                                                     MediaPlayer.OnBufferingUpdateListener{

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {



    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
