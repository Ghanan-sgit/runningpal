package com.sliit.ghanansachith.runningpal;

/**
 * Created by pranavaghanan on 4/2/15.
 */
public class Track {
    
    private int trackId;
    private String date;
    private double distance;
    private String time;
    private String speed;
    private String trackImage;

    public Track() { }

    public Track(String date, double distance, String time, String speed, String trackImage) {
        this.date = date;
        this.distance = distance;
        this.time = time;
        this.speed = speed;
        this.trackImage=trackImage;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getTrackImage() {
        return trackImage;
    }

    public void setTrackImage(String trackImage) {
        this.trackImage = trackImage;
    }
}
