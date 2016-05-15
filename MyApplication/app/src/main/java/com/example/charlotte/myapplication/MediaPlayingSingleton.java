package com.example.charlotte.myapplication;

import android.util.Log;

/**
 * Created by charlotte on 01.05.16.
 */
public class MediaPlayingSingleton {

    Song currentSong;
    boolean isPlaying;
    private static MediaPlayingSingleton ourInstance = new MediaPlayingSingleton();

    public static MediaPlayingSingleton getInstance() {
        return ourInstance;
    }

    private MediaPlayingSingleton() {
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        if (currentSong!=null) {
            Log.d("TAG", "set current song to: " + currentSong.toString());
        }
        this.currentSong = currentSong;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        Log.d("TAG", "set is playing to "+isPlaying);
        this.isPlaying = isPlaying;
    }
}
