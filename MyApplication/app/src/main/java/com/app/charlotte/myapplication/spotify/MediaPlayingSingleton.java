package com.app.charlotte.myapplication.spotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by charlotte on 01.05.16.
 */
public class MediaPlayingSingleton {

  // Song currentSong;
    boolean isPlaying;
    private static MediaPlayingSingleton ourInstance = new MediaPlayingSingleton();

    public static MediaPlayingSingleton getInstance() {
        return ourInstance;
    }

    private MediaPlayingSingleton() {
    }

    public Song getCurrentSong(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("media", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String currentSongGSON = sharedPref.getString("currentSong", "");

      Song currentSong  = gson.fromJson(currentSongGSON, Song.class);

       return currentSong;
    }

    public void setCurrentSong(Song currentSong, Context context) {
        if (currentSong!=null) {
            Log.d("TAG", "set current song to: " + currentSong.toString());
        }
        else return;
       // this.currentSong = currentSong;

        SharedPreferences sharedPref = context.getSharedPreferences("media", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();

        String currentSongGSON = gson.toJson(currentSong);
        editor.putString("currentSong", currentSongGSON);
       // editor.putString("artist", (currentSong.getArtist()==null)?"":currentSong.getArtist());
      //  editor.putString("songname", (currentSong.getSongname()==null)?"":currentSong.getSongname());
       // editor.putString("songname", (currentSong.getSpotifyID()==null)?"":currentSong.getSpotifyID());
        editor.commit();

    }

    public boolean isPlaying(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("media", Context.MODE_PRIVATE);
        return sharedPref.getBoolean("playing", false);

        //return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying, Context context) {
        Log.d("TAG", "set is playing to "+isPlaying);
       // this.isPlaying = isPlaying;

        SharedPreferences sharedPref = context.getSharedPreferences("media", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("playing", isPlaying);
        editor.commit();

    }
}
