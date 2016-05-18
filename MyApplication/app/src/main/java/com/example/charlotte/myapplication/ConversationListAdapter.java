package com.example.charlotte.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by charlotte on 01.05.16.
 */
public class ConversationListAdapter extends ArrayAdapter<Message>{

    private final String fromUser;
    private final String toUser;
    private List itemList;
    private static final int DB_MIN_VALUE=0;
    private static final int DB_MAX_VALUE=120;
    private long startTime;

    public ConversationListAdapter(Context context, int resource, String fromUser, String toUser) {
        super(context, resource);
        this.fromUser = fromUser;
        this.toUser = toUser;
        initializeAdapter();
        Log.d("TAG", "new conversation list adapter");


    }

    @Override
    public Message getItem(int position) {
        return super.getItem(position);
    }

    public void initializeAdapter() {
        startTime = System.nanoTime();

        Call<List<Message>> call = Application.getService().getConversation(fromUser, toUser, 0);
        Log.d("TAG", "initialize adapter again");

        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {


                List<Message> messages = response.body();

                Log.d("TAG", "on response called list isze is " + messages.size());

                for (Message message : messages) {
                    Log.d("TAG", message.toString());

                }
                clear();
                addAll(messages);
                Log.d("TAG", "notify dataset changed");
                notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {

            }


        });


        //TODO: test

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // return super.getView(position, convertView, parent);

        if (position==0)
        {
            long difference = System.nanoTime() - startTime;
            Log.d("TAG", "time between data loading and get view call " + TimeUnit.NANOSECONDS.toMillis(difference)+ "ms");
        }

        final ViewHolder viewHolder;

        if (convertView == null) {

            Log.d("TAG", "convert view is null");

            // inflate the layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.conversation_list_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.messageText);

            viewHolder.songImageView= (ImageView) convertView.findViewById(R.id.artistImage);
            viewHolder.songTitleTextView= (TextView) convertView.findViewById(R.id.textSong);
            viewHolder.button = (ImageButton) convertView.findViewById(R.id.button);
            viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            Log.d("TAG", "convert view is not null");


        }
        Message message = getItem(position);
        if (message!=null) {


         //   if (message.getAmbientNoise()!=null)
         //   {

                 //   message.setMessageText(message.getMessageText()+message.getAmbientNoise());
         //       int stars = getStartsForRatingBar(message.getAmbientNoise().decibels);
           //     viewHolder.ratingBar.setVisibility(View.VISIBLE);
             //   viewHolder.ratingBar.setRating(stars);
//
           // }

           // else{

                viewHolder.ratingBar.setVisibility(View.GONE);
          //  }
            //message from other user, better check something else! TODO
            String displayName="Name";
            if (message.getFromUserDisplayName() != null) {

                displayName= message.getFromUserDisplayName();
            }

            viewHolder.textViewItem.setText(displayName+ " schrieb: " + message.getMessageText());

            RelativeLayout musicView = (RelativeLayout) convertView.findViewById(R.id.music_view);

            if (message.getSong()!=null)
            {
                final Song song=message.getSong();
                viewHolder.songTitleTextView.setText(song.getArtist() + " - " + song.getSongname());


                if (song.getSpotifyID()!=null)
                {

                    Log.d("TAG", "spotify song id: "+song.getSpotifyID());

                    SpotifyServiceSingleton.getInstance().getPhotoPathForTrack(song.getSpotifyID(), new SpotifyPhotoCallback() {
                        @Override
                        public void photoFetched(String photo) {
                            ImageLoader imageLoader = ImageLoader.getInstance();
                            imageLoader.displayImage(photo, viewHolder.songImageView);
                        }
                    });




                }

                viewHolder.button.setVisibility(View.VISIBLE);


                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getContext()!=null) {

                            ((SingleConversationActivity) getContext()).playSong(song.getSpotifyID());

                        }
                    }
                });


                musicView.setVisibility(View.VISIBLE);


            }
            else {


                musicView.setVisibility(View.GONE);
                viewHolder.button.setVisibility(View.GONE);


            }






    }

        return convertView;


    }

    public int getStartsForRatingBar(double db)
    {

        if (db<DB_MIN_VALUE){

            db=DB_MIN_VALUE;

            }
        if (db>DB_MAX_VALUE)
        {
            db=DB_MAX_VALUE;
        }

        db=db-20;
        //TODO: noch mal nachrechnen
        int stars = (int) Math.round((db/20.0));

        return stars;

    }


    static class ViewHolder {
        TextView textViewItem;
        TextView songTitleTextView;
        ImageView songImageView;
        public ImageButton button;
        public RatingBar ratingBar;
    }
}
interface SpotifyPhotoCallback {

    void photoFetched(String photo);
}

interface SpotifyTrackCallback {

    void trackFetched(String trackId);
}