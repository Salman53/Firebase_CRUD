package com.example.firebase_crud;

public class Track {

     String trackId;
     String trackName;
     int trackRating;

    public Track() {
    }

    public Track(String trackId, String trackName, int trackRating) {
        this.trackId = trackId;
        this.trackName = trackName;
        this.trackRating = trackRating;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public int getTrackRating() {
        return trackRating;
    }
}
