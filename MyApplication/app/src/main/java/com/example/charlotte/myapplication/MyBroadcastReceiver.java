package com.example.charlotte.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by charlotte on 01.05.16.
 */


    public class MyBroadcastReceiver extends BroadcastReceiver {
        static final class BroadcastTypes {
            static final String PLAYBACK_STATE_CHANGED =  ".playbackstatechanged";
            static final String QUEUE_CHANGED = ".queuechanged";
            static final String METADATA_CHANGED =  ".metadatachanged";
            static final String META_CHANGED=".metachanged";
            static final String PLAYSTATE_CHANGED="playstatechanged";
        }

        @Override
        public void onReceive(Context context, Intent intent) {

                   Log.d("TAG", "on receive");

                   // This is sent with all broadcasts, regardless of type. The value is taken from
            // System.currentTimeMillis(), which you can compare to in order to determine how
            // old the event is.
            long timeSentInMs = intent.getLongExtra("timeSent", 0L);

            String action = intent.getAction();
            Log.d("TAG", "action is "+intent.getAction());

            if (action.contains(BroadcastTypes.METADATA_CHANGED)||action.contains(BroadcastTypes.META_CHANGED)) {
                String trackId = intent.getStringExtra("id");


                String artistName = intent.getStringExtra("artist");
                String albumName = intent.getStringExtra("album");
                String trackName = intent.getStringExtra("track");
                int trackLengthInSec = intent.getIntExtra("length", 0);
               // boolean playing = intent.getBooleanExtra("playing", false);

             //   MediaPlayingSingleton.getInstance().setIsPlaying(playing);

              //  if (MediaPlayingSingleton.getInstance().isPlaying()) {
                    Song song = new Song(artistName, trackName);
                    if (trackId!=null)
                    {

                        Log.d("TAG", "set track id: "+trackId);
                        song.setSpotifyID(trackId);

                    }
                    MediaPlayingSingleton.getInstance().setCurrentSong(song);

              //  }
                // Do something with extracted information...
                //TODO: add correct action for android media player
            } else if (action.contains(BroadcastTypes.PLAYBACK_STATE_CHANGED)||action.contains(BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
                boolean playing = intent.getBooleanExtra("playing", false);
                int positionInMs = intent.getIntExtra("playbackPosition", 0);
                MediaPlayingSingleton.getInstance().setIsPlaying(playing);

                //if (!playing)
                //{

                   // MediaPlayingSingleton.getInstance().setCurrentSong(null);
               // }
                //TODO: check if song finished
                // Do something with extracted information
            } else if (action.contains(BroadcastTypes.QUEUE_CHANGED)) {
                // Sent only as a notification, your app may want to respond accordingly.
            }
        }
    }

