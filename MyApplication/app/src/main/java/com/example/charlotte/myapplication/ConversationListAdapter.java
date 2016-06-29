package com.example.charlotte.myapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    public static final long MAX_HOURS_BETWEEN_DISTANCES=3;

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

                if (messages!=null) {
                    Log.d("TAG", "on response called list isze is " + messages.size());

                    for (Message message : messages) {
                        Log.d("TAG", message.toString());

                    }
                    clear();
                    addAll(messages);
                    Log.d("TAG", "notify dataset changed");
                    notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {


                Log.e("Conversation", t.getMessage()+t.getCause());
            }


        });


        //TODO: test

    }


    public boolean timeoutReachedForDistanceComputation(Date messageDate)
    {

        if (messageDate==null)
        {
            Log.e("TAG", "message date is null");
            return true;

        }

        Log.d("TAG", "hurray message date is not null!");
        Date date = new Date();
        long diff = date.getTime()-messageDate.getTime();
        long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);

        return hours > MAX_HOURS_BETWEEN_DISTANCES;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // return super.getView(position, convertView, parent);



        if (position == 0) {
            long difference = System.nanoTime() - startTime;
            Log.d("TAG", "time between data loading and get view call " + TimeUnit.NANOSECONDS.toMillis(difference) + "ms");
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
            viewHolder.songImageView = (ImageView) convertView.findViewById(R.id.artistImage);
            viewHolder.songTitleTextView = (TextView) convertView.findViewById(R.id.textSong);
            viewHolder.button = (ImageButton) convertView.findViewById(R.id.button);
            viewHolder.distanceViewLayout = (LinearLayout) convertView.findViewById(R.id.distance_view_layout);
            viewHolder.distanceView = (DistanceView) convertView.findViewById(R.id.distanceView);
            viewHolder.weatherImage=(ImageView) convertView.findViewById(R.id.weather_image);
            viewHolder.weatherText=(TextView)convertView.findViewById(R.id.weather_text);
            viewHolder.spotifyLine = (View)convertView.findViewById(R.id.spotify_divider);
            viewHolder.messageSender= (TextView) convertView.findViewById(R.id.sender_name);
            viewHolder.activityImage= (ImageView) convertView.findViewById(R.id.activity_image);
            viewHolder.weatherView = (RelativeLayout) convertView.findViewById(R.id.weather_layout);
            viewHolder.messageDate= (TextView) convertView.findViewById(R.id.message_date);
            viewHolder.messageCardView = (CardView) convertView.findViewById(R.id.message_card_view);
            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            Log.d("TAG", "convert view is not null");
        }

        final Message message = getItem(position);
        if (message != null) {
            boolean fromOtherUser = (!(message.getFromUser().equals(UserSingleton.getInstance().getCurrentUser().getUsername())));

            if (fromOtherUser)
            {
                viewHolder.messageCardView.setCardBackgroundColor(R.color.colorAccent);
            }


            String displayName = "Name";
            if (message.getFromUserDisplayName() != null) {

                displayName = message.getFromUserDisplayName();
                viewHolder.messageSender.setText(displayName);

            }


            viewHolder.textViewItem.setText(message.getMessageText());

            if (message.getTimestamp()!=null)
            {

                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy hh:mm", Locale.GERMANY);
                viewHolder.messageDate.setText(formatter.format(message.getTimestamp()));
            }

            RelativeLayout musicView = (RelativeLayout) convertView.findViewById(R.id.music_view);

            if (message.getSong() != null) {
                final Song song = message.getSong();
                viewHolder.songTitleTextView.setText(song.getArtist() + " - " + song.getSongname());


                if (song.getSpotifyID() != null) {
                    Log.d("TAG", "spotify song id: " + song.getSpotifyID());

                    final String spotifyId = song.getSpotifyID();
                    showPlayButtonAndSpotifyImage(viewHolder, spotifyId);


                } else {

                    SpotifyServiceSingleton.getInstance().getSpotifyIdForSongData(song.getArtist(), song.getSongname(), new SpotifyTrackCallback() {
                        @Override
                        public void trackFetched(String trackId) {

                            if (trackId != null) {
                                Log.d("TAG", "track id");
                                showPlayButtonAndSpotifyImage(viewHolder, trackId);

                                //TODO: add track id to server!
                            }
                        }
                    });


                }


                musicView.setVisibility(View.VISIBLE);
                viewHolder.spotifyLine.setVisibility(View.VISIBLE);
            } else {
                musicView.setVisibility(View.GONE);
                viewHolder.button.setVisibility(View.GONE);
                viewHolder.spotifyLine.setVisibility(View.GONE);
            }


            if (message.getWeatherJSON() != null) {
                viewHolder.weatherView.setVisibility(View.VISIBLE);
                viewHolder.weatherText.setText("Temperatur: " + message.getWeatherJSON().getMain().getTemp() + " °");
                ImageLoader imageLoader = ImageLoader.getInstance();
                if (message.getWeatherJSON().getWeatherList() != null) {
                    String iconString = message.getWeatherJSON().getWeatherList().get(0).getIcon();
                    //TODO: download and save icon
                    imageLoader.displayImage(WeatherHelper.getInstance().getURLForIconString(iconString), viewHolder.weatherImage);
                }

            }
            else {

                viewHolder.weatherView.setVisibility(View.GONE);
            }

            if (message.getActivityValue()>=0)
            {
                viewHolder.activityImage.setImageDrawable(getContext().getResources().getDrawable(matchActivityTypesToDrawableRes(message.getActivityValue())));
            }


            if (message.getUsersDistance() != null) {

                float distance = message.getUsersDistance().getDistanceValue();
                //TODO: format correctly
                viewHolder.distanceView.setDistanceAnnotation(""+distance+ " m");
                viewHolder.distanceView.setDistanceFraction(LocationHelper.getInstance().computeDistanceFractionForView(distance));
                viewHolder.distanceViewLayout.setVisibility(View.VISIBLE);

            }
            else if (message.getSenderLocation() != null && fromOtherUser) {
                    final GeoLocation otherUserDistance = message.getSenderLocation();

                    LocationHelper.getInstance().determineLocation(getContext(), getContext().getApplicationContext(), new LocationFetchedInteface() {
                        @Override
                        public void hasFetchedLocation(Location location) {

                            if (location != null) {
                                Log.d("TAG", "my own locatin: " + location.toString());
                                float[] res = new float[1];
                                Location.distanceBetween(otherUserDistance.getLatitude(), otherUserDistance.getLongitude(), location.getLatitude(), location.getLongitude(), res);
                                float distanceInMeters = res[0];

                                Log.d("TAG", "my distance in meters: " + distanceInMeters);
                                viewHolder.distanceView.setDistanceAnnotation(""+distanceInMeters+ " m");
                                viewHolder.distanceView.setDistanceFraction(LocationHelper.getInstance().computeDistanceFractionForView(distanceInMeters));
                                viewHolder.distanceViewLayout.setVisibility(View.VISIBLE);

                                //call distance update function
                                Log.d("TAG", "not sender of message, updating distance");

                                //if timeout for distance computation has not yet been reached
                                if (!timeoutReachedForDistanceComputation(message.getTimestamp())) {
                                    updateUsersDistance(distanceInMeters, message);

                                }

                            } else {

                                viewHolder.distanceViewLayout.setVisibility(View.GONE);

                                Log.d("TAG", "location is null here, why?");
                            }

                        }
                    });


                }
            else {

                viewHolder.distanceViewLayout.setVisibility(View.GONE);

            }
            }


            return convertView;

    }


    public int matchActivityTypesToDrawableRes(int activityType)
    {

        switch (activityType){
            case DetectedActivity.IN_VEHICLE: return R.drawable.car_icon;
            case DetectedActivity.ON_BICYCLE: return R.drawable.bicycle;
            case DetectedActivity.ON_FOOT: return R.drawable.walking;
            case DetectedActivity.WALKING: return  R.drawable.walking;
            case DetectedActivity.RUNNING: return  R.drawable.running;
            case DetectedActivity.STILL: return R.drawable.sitting;
            case DetectedActivity.TILTING: return R.drawable.tilting;
            case DetectedActivity.UNKNOWN: return R.drawable.unknown;
            default: return R.drawable.unknown;

        }

    }

    private void updateUsersDistance(float distanceInMeters, Message message) {

        Call<Result> call = Application.getService().updateMessageDistance(message.get_id(), distanceInMeters);
        Log.d("TAG", "initialize adapter again");

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Log.d("TAG", "message updated");
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                Log.e("TAG", "failure: " + t.getCause() + t.getMessage());

            }
        });
    }

    private void showPlayButtonAndSpotifyImage(final ViewHolder viewHolder, final String spotifyId) {
        SpotifyServiceSingleton.getInstance().getPhotoPathForTrack(spotifyId, new SpotifyPhotoCallback() {
            @Override
            public void photoFetched(String photo) {
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(photo, viewHolder.songImageView);
            }
        });


        viewHolder.button.setVisibility(View.VISIBLE);


        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext()!=null) {

                    ((SingleConversationActivity) getContext()).playSong(spotifyId);

                }
            }
        });
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
        ImageView weatherImage;
        TextView weatherText;
        TextView messageSender;
        TextView messageDate;
        public ImageButton button;
        public RatingBar ratingBar;
        public LinearLayout distanceViewLayout;
        public View spotifyLine;
        ImageView activityImage;
        public DistanceView distanceView;
        public RelativeLayout weatherView;
        public CardView messageCardView;
    }
}
interface SpotifyPhotoCallback {

    void photoFetched(String photo);
}

interface SpotifyTrackCallback {

    void trackFetched(String trackId);
}