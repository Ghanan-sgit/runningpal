package com.sliit.ghanansachith.runningpal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by pranavaghanan on 3/29/15.
 */
public class TrackDBHandler extends SQLiteOpenHelper {

    SQLiteDatabase db;

    //Database Name & Version
    private static final String DATABASE_NAME = "TracksDB";
    private static final int DATABASE_VERSION = 1;

    //Table Names
    private static final String TABLE_TRACKS = "tracks";

    //Column Names
    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_DISTANCE = "distance";
    private static final String COL_TIME = "time";
    private static final String COL_SPEED = "speed";
    private static final String COL_IMAGE = "image";

    private static final String[] COLUMNS = {COL_ID, COL_DATE, COL_DISTANCE, COL_TIME, COL_SPEED, COL_IMAGE};


    private static final String CREATE_TRACKS_TABLE = "CREATE TABLE " + TABLE_TRACKS
            + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_DATE + " TEXT, "
            + COL_DISTANCE + " TEXT,"
            + COL_SPEED + " TEXT,"
            + COL_TIME + " TEXT,"
            + COL_IMAGE + " TEXT);";


    public TrackDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TRACKS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tracks");
        onCreate(db);
    }


    public boolean insertTrack(Track ptrack) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("date", ptrack.getDate());
        contentValues.put("distance", ptrack.getDistance());
        contentValues.put("time", ptrack.getTime());
        contentValues.put("speed", ptrack.getSpeed());
        contentValues.put("image", ptrack.getTrackImage());

        db.insert("tracks", null, contentValues);
        db.close();
        return true;
    }


    public Track getTrack(int trackId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Track track = null;

        Cursor cursor = db.query(true, TABLE_TRACKS, COLUMNS, "id = ?", new String[]{String.valueOf(trackId)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        track = new Track();

        track.setTrackId(Integer.parseInt(cursor.getString(0)));
        track.setDate(cursor.getString(1));
        track.setDistance(Double.parseDouble(cursor.getString(2)));
        track.setTime(cursor.getString(3));
        track.setSpeed(cursor.getString(4));
        track.setTrackImage(cursor.getString(5));

        return track;
    }


    public List<Track> getAllTracks() {
      //  SQLiteDatabase db;
        List<Track> tracks = new ArrayList<Track>();
      try {
          String selectQuery = "SELECT  * FROM " + TABLE_TRACKS;

          db = this.getReadableDatabase();
          Cursor c = db.rawQuery(selectQuery, null);

          // looping through all rows and adding to list
          if (c.moveToFirst()) {
              do {
                  Track track = new Track();
                  track.setTrackId(c.getInt((c.getColumnIndex(COL_ID))));
                  track.setTime((c.getString(c.getColumnIndex(COL_TIME))));
                  track.setDistance(Double.parseDouble(c.getString(c.getColumnIndex(COL_DISTANCE))));
                  track.setDate(c.getString(c.getColumnIndex(COL_DATE)));
                  track.setTrackImage(c.getString(c.getColumnIndex(COL_IMAGE)));
                  track.setSpeed(c.getString(c.getColumnIndex(COL_SPEED)));

                  tracks.add(track);
              } while (c.moveToNext());
          }
      }catch (Exception e){}finally {
          db.close();
      }


        return tracks;
    }


    public String calculateTotalDistance() {
        String totDis = null;

        try {

            String selectQuery = "SELECT  SUM(distance) FROM " + TABLE_TRACKS;

            db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);

            if (c != null)
                c.moveToFirst();

            DecimalFormat df = new DecimalFormat("#.00");

            double disInDouble = c.getDouble(0);

            if (disInDouble > 1000) {
                totDis = df.format(disInDouble / 1000) + " km";
            } else
                totDis = df.format(disInDouble) + " m";
        } catch (Exception e) {
        }finally {
            db.close();
        }

        return totDis;
    }

    public String calculateAverageSpeed() {
        String totSpeed = null;

        try {

            String selectQuery = "SELECT  AVG(speed) FROM " + TABLE_TRACKS;

            db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);

            if (c != null)
                c.moveToFirst();

            DecimalFormat df = new DecimalFormat("#.00");

            double speedInDouble = c.getDouble(0);

            totSpeed = df.format(speedInDouble) + " kmph";

        } catch (Exception e) {
        }finally {
            db.close();
        }

        return totSpeed;
    }

    public String calculateTotalTime() {
        String totTime = null;

        try {

            String selectQuery = "SELECT time FROM " + TABLE_TRACKS;

            db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                long sumOfTime = 0;

                do {
                    Date tempDate = sdf.parse(c.getString(0));
                    Log.d("current time", c.getString(0));
                    sumOfTime += tempDate.getTime();
                } while (c.moveToNext());

                totTime = sdf.format(new Date(sumOfTime));
            }


        } catch (Exception e) {
        }finally {
            db.close();
        }

        return totTime;
    }


    public static String storeImage(Bitmap imageData, String filename) {
        //get path to external storage (SD card)
        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/RunningPal/Snapshots/";
        File sdIconStorageDir = new File(iconsStoragePath);

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();

        String filePath = null;

        try {
            filePath = sdIconStorageDir.toString() + "/" + filename + ".png";
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose file format
            imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
        }

        return iconsStoragePath+filename;
    }


}

/* class BitMapConverter {

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getPhoto(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 100, image.length);
    }

} */
