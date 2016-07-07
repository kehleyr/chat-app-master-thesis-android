package com.example.charlotte.myapplication;

import android.app.MediaRouteButton;
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
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by charlotte on 01.05.16.
 */
public class ConversationListAdapter extends ArrayAdapter<Message>{

    public static final int QUERY_LIMIT = 30;
    private static Typeface materialIconTypeFace;
    private static Typeface weatherIconTypeface;
    private final String fromUser;
    private final String toUser;
    private List itemList;
    private static final int DB_MIN_VALUE=0;
    private static final int DB_MAX_VALUE=120;
    private long startTime;
    public static final long MAX_MINUTES_BETWEEN_DISTANCES =30;

    public ConversationListAdapter(Context context, int resource, String fromUser, String toUser) {
        super(context, resource);
        this.fromUser = fromUser;
        this.toUser = toUser;

        initializeAdapter();
        Log.d("TAG", "new conversation list adapter");
    }




    public static Typeface getMaterialIconTypeface(Context context)
    {
        if (materialIconTypeFace == null)
        {
            materialIconTypeFace = Typeface.createFromAsset(context.getAssets(),"fonts/MaterialIcons-Regular.ttf");
        }
        return  materialIconTypeFace;
    }

    public static Typeface getWeatherIconTypeface(Context context)
    {
        if (weatherIconTypeface == null)
        {
            weatherIconTypeface = Typeface.createFromAsset(context.getAssets(),"fonts/owf-regular2.ttf");
        }
        return  weatherIconTypeface;
    }



    @Override
    public Message getItem(int position) {
        return super.getItem(position);
    }

    public void initializeAdapter() {

        Toast.makeText(getContext(), "initialize list adapter", Toast.LENGTH_SHORT).show();

        startTime = System.nanoTime();

        Call<List<Message>> call = Application.getService().getConversation(fromUser, toUser,
                QUERY_LIMIT);
        Log.d("TAG", "initialize adapter again");

        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {


                List<Message> messages = response.body();

                if (messages != null) {
                    Log.d("TAG", "on response called list isze is " + messages.size());

                    for (Message message : messages) {
                        Log.d("TAG", message.toString());

                    }
                    clear();
                    addAll(messages);
                    Log.d("TAG", "notify dataset changed");
                    notifyDataSetChanged();
                    ((SingleConversationActivity) getContext()).scrollMyListViewToBottom();
                }

            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {

                Toast.makeText(getContext(), "gm failed " + t.getCause(), Toast.LENGTH_SHORT).show();


                Log.e("Conversation", t.getMessage() + t.getCause());
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
        long minutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);

        Log.d("conversation", "minutes is: " + minutes + " amd max minutes is: " + MAX_MINUTES_BETWEEN_DISTANCES);

        return minutes > MAX_MINUTES_BETWEEN_DISTANCES;

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
            viewHolder.weatherImage=(TextView) convertView.findViewById(R.id.weather_image);
            viewHolder.weatherText=(TextView)convertView.findViewById(R.id.weather_text);
            viewHolder.spotifyLine = (View)convertView.findViewById(R.id.distance_view_divider);
            viewHolder.messageSender= (TextView) convertView.findViewById(R.id.sender_name);
            viewHolder.activityImage= (TextView) convertView.findViewById(R.id.activity_image);
            viewHolder.weatherView = (RelativeLayout) convertView.findViewById(R.id.weather_layout);
            viewHolder.messageDate= (TextView) convertView.findViewById(R.id.message_date);
            viewHolder.messageCardView = (CardView) convertView.findViewById(R.id.message_card_view);
            viewHolder.textArtist=(TextView) convertView.findViewById(R.id.textArtist);
            viewHolder.distanceViewDivider=(View)convertView.findViewById(R.id.distance_view_divider);
            viewHolder.musicView = (RelativeLayout) convertView.findViewById(R.id.music_view);

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
                viewHolder.messageCardView.setCardBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
            }
            else {

                viewHolder.messageCardView.setCardBackgroundColor(getContext().getResources().getColor(R.color.cardview_light_background));
            }


            String displayName = "Name";
            if (message.getFromUserDisplayName() != null) {

                displayName = message.getFromUserDisplayName();
                viewHolder.messageSender.setText(displayName);

            }


            viewHolder.textViewItem.setText(message.getMessageText());

            if (message.getTimestamp()!=null)
            {

                Date date = new Date();
                Date fromGmt = new Date(message.getTimestamp().getTime() + TimeZone.getDefault().getOffset(date.getTime()));
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.GERMANY);
                viewHolder.messageDate.setText(formatter.format(fromGmt));
            }


            if (message.getSong() != null) {
                final Song song = message.getSong();
                viewHolder.textArtist.setText(song.getArtist());
                viewHolder.songTitleTextView.setText(song.getSongname());


                //TODO: check if player is alive

                if (song.getSpotifyID() != null) {
                    Log.d("TAG", "spotify song id: " + song.getSpotifyID());

                    final String spotifyId = song.getSpotifyID();
                    showPlayButtonAndSpotifyImage(viewHolder, spotifyId);
                }
                else {

                    hidePlayButtonAndSpotifyImage(viewHolder);
                }

                makeMusicViewVisible(viewHolder, true);
            } else {

                makeMusicViewVisible(viewHolder, false);
            }


            if (message.getWeatherJSON() != null) {
                makeWeatherViewVisible(viewHolder, true);
                viewHolder.weatherText.setText("Temperatur: " + message.getWeatherJSON().getMain().getTemp() + "°");
                ImageLoader imageLoader = ImageLoader.getInstance();
                if (message.getWeatherJSON().getWeatherList() != null) {
                    viewHolder.weatherImage.setTypeface(getWeatherIconTypeface(getContext()));
                    String iconString = message.getWeatherJSON().getWeatherList().get(0).getIcon();
                    viewHolder.weatherImage.setText(getContext().getResources().getString(getWeatherStringForIconString(iconString)));
                    //TODO: download and save icon
                    //imageLoader.displayImage(WeatherHelper.getInstance().getURLForIconString(iconString), viewHolder.weatherImage);
                }

            }
            else {

                makeWeatherViewVisible(viewHolder,false);
            }

            if (message.getActivityValue()>=0 && message.getActivityValue()!=DetectedActivity.UNKNOWN)
            {

                viewHolder.activityImage.setVisibility(View.VISIBLE);
                viewHolder.activityImage.setTypeface(getMaterialIconTypeface(getContext()));
                viewHolder.activityImage.setText(getContext().getResources().getString(matchActivityTypesToStringRes(message.getActivityValue())));
            }
            else {

                viewHolder.activityImage.setVisibility(View.GONE);
            }


            if (message.getUsersDistance() != null) {

                float distance = message.getUsersDistance().getDistanceValue();
                Log.d("TAG", "distanz = "+distance);

                //TODO: format correctly
                showDistanceOnDistanceView(viewHolder, LocationHelper.getInstance().computeDistanceString(distance), LocationHelper.getInstance().computeDistanceFractionForView(distance));
                makeDistanceViewVisible(viewHolder, true);

            }
            else if (message.getSenderLocation() != null && fromOtherUser && !timeoutReachedForDistanceComputation(message.getTimestamp())) {
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
                                showDistanceOnDistanceView(viewHolder, ""+distanceInMeters+ " m", LocationHelper.getInstance().computeDistanceFractionForView(distanceInMeters));
                                makeDistanceViewVisible(viewHolder, true);

                                //call distance update function
                                Log.d("TAG", "not sender of message, updating distance");

                                //if timeout for distance computation has not yet been reached

                                    updateUsersDistance(distanceInMeters, message);



                            } else {

                                makeDistanceViewVisible(viewHolder, false);


                                Log.d("TAG", "location is null here, why?");
                            }

                        }
                    });


                }
            else {

                makeDistanceViewVisible(viewHolder, false);

            }
            }


            return convertView;

    }

    private void makeMusicViewVisible(ViewHolder viewHolder, boolean visible) {
        if (visible) {
            viewHolder.musicView.setVisibility(View.VISIBLE);
            viewHolder.spotifyLine.setVisibility(View.VISIBLE);
        }
        else{

            viewHolder.musicView.setVisibility(View.GONE);
            viewHolder.button.setVisibility(View.GONE);
            viewHolder.spotifyLine.setVisibility(View.GONE);

        }
    }

    private void makeWeatherViewVisible(ViewHolder viewHolder, boolean visible) {
        if (visible) {
            viewHolder.weatherView.setVisibility(View.VISIBLE);
        }
        else {

            viewHolder.weatherView.setVisibility(View.GONE);
        }
    }

    private void showDistanceOnDistanceView(ViewHolder viewHolder, String distance2, float distance3) {
        viewHolder.distanceView.setDistanceAnnotation(distance2);
        viewHolder.distanceView.setDistanceFraction(distance3);
    }

    private void makeDistanceViewVisible(ViewHolder viewHolder, boolean visible) {
        if (visible) {
            viewHolder.distanceViewLayout.setVisibility(View.VISIBLE);
            viewHolder.distanceViewDivider.setVisibility(View.VISIBLE);
        }
        else {

            viewHolder.distanceViewLayout.setVisibility(View.GONE);
            viewHolder.distanceViewDivider.setVisibility(View.GONE);
        }
    }


    private int getWeatherStringForIconString(String iconString) {
        int id = getContext().getResources().getIdentifier("w"+iconString.replace("d","").replace("n",""), "string", getContext().getPackageName());
        Log.d("TAG", "id is: "+id);
        return id;
    }


    public int matchActivityTypesToStringRes(int activityType)
    {

        switch (activityType){
            case DetectedActivity.IN_VEHICLE: return R.string.car;
            case DetectedActivity.ON_BICYCLE: return R.string.bike;
            case DetectedActivity.ON_FOOT: return R.string.walking;
            case DetectedActivity.WALKING: return  R.string.walking;
            case DetectedActivity.RUNNING: return  R.string.running;
            case DetectedActivity.STILL: return R.string.still;
            case DetectedActivity.TILTING: return R.string.tilt;
            case DetectedActivity.UNKNOWN: return R.string.unknown;
            default: return R.string.unknown;

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
                if (getContext() != null) {

                    ((SingleConversationActivity) getContext()).playSong(spotifyId);

                }
            }
        });
    }


    public void hidePlayButtonAndSpotifyImage(final ViewHolder viewHolder)
    {
        viewHolder.button.setVisibility(View.GONE);
        viewHolder.songImageView.setVisibility(View.GONE);

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
        TextView weatherImage;
        TextView weatherText;
        TextView textArtist;
        TextView messageSender;
        TextView messageDate;
        public ImageButton button;
        public RatingBar ratingBar;
        public LinearLayout distanceViewLayout;
        public View spotifyLine;
        TextView activityImage;
        public DistanceView distanceView;
        public RelativeLayout weatherView;
        public CardView messageCardView;
        public View distanceViewDivider;
        public RelativeLayout musicView;
    }
}
interface SpotifyPhotoCallback {

    void photoFetched(String photo);
}

interface SpotifyTrackCallback {

    void trackFetched(String trackId);
}