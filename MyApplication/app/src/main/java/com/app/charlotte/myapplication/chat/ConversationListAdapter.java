package com.app.charlotte.myapplication.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.charlotte.myapplication.Application;
import com.app.charlotte.myapplication.LocationHelper;
import com.app.charlotte.myapplication.R;
import com.app.charlotte.myapplication.Result;
import com.app.charlotte.myapplication.SpotifyPhotoCallback;
import com.app.charlotte.myapplication.UserSingleton;
import com.app.charlotte.myapplication.location.Distance;
import com.app.charlotte.myapplication.location.GeoLocation;
import com.app.charlotte.myapplication.spotify.Song;
import com.app.charlotte.myapplication.spotify.SpotifyServiceSingleton;
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
    private static Message message;
    private final String fromUser;
    private final String toUser;
    private List itemList;
    private static final int DB_MIN_VALUE=0;
    private static final int DB_MAX_VALUE=120;
    private long startTime;
    public static final long MAX_MINUTES_BETWEEN_DISTANCES =30;
    private boolean detailsViewVisible=false;

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
            weatherIconTypeface = Typeface.createFromAsset(context.getAssets(),"fonts/owfont-regular.ttf");
        }
        return  weatherIconTypeface;
    }

    public static Message getMessage() {
        return message;
    }

    public static void setMessage(Message message)
    {

        ConversationListAdapter.message=message;
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
        Date correctMessageDate =new Date(messageDate.getTime() + TimeZone.getDefault().getOffset(date.getTime()));

        long diff = date.getTime()-correctMessageDate.getTime();
        long minutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        long maxMinutesBetweenDistances = Long.parseLong(sharedPref.getString(getContext().getResources().getString(R.string.max_minutes_for_dist_comp), ""+MAX_MINUTES_BETWEEN_DISTANCES));

        return minutes > maxMinutesBetweenDistances;

    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message message = getItem(position);

        if (message==null) return convertView;
        boolean fromOtherUser = (!(message.getFromUser().equals(UserSingleton.getInstance().getCurrentUser(getContext()).getUsername())));
        final  ViewHolder viewHolder;


        if (convertView == null) {

            Log.d("TAG", "convert view is null");

            // inflate the layout


                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.received_message, parent, false);


            //TODO: do the modifications!!!

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.messageText);
            viewHolder.messageSender= (TextView) convertView.findViewById(R.id.sender_name);
            viewHolder.messageDate= (TextView) convertView.findViewById(R.id.message_date);
            viewHolder.messageCardView = (CardView) convertView.findViewById(R.id.message_card_view);
            viewHolder.artistImageSmall = (ImageView) convertView.findViewById(R.id.artistImageSmall);
            viewHolder.weatherImageSmall = (TextView) convertView.findViewById(R.id.weather_image_small);
            viewHolder.activityImageSmall=(TextView) convertView.findViewById(R.id.activity_image_small);
            viewHolder.smallDistanceView = (TextView) convertView.findViewById(R.id.small_distance_view);
            viewHolder.smallDistanceViewImage = (ImageView) convertView.findViewById(R.id.distance_image);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            Log.d("TAG", "convert view is not nullm nessage is " + message.getMessageText());
        }
        int smallMargin = (int) convertDpToPixel(8);
        int middleMargin = (int) convertDpToPixel(15);
        int bigMargin= (int) convertDpToPixel(35);
        if (!fromOtherUser) {
            viewHolder.messageCardView.setCardBackgroundColor(getContext().getResources().getColor(R.color.cardview_light_background));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    viewHolder.messageCardView.getLayoutParams();

            viewHolder.messageCardView.setLayoutParams(layoutParams);

            layoutParams.setMargins(bigMargin, smallMargin,middleMargin, smallMargin);

        }
        else {
            viewHolder.messageCardView.setCardBackgroundColor(getContext().getResources().getColor(R.color.lightBackgroundGrey));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    viewHolder.messageCardView.getLayoutParams();

            layoutParams.setMargins(middleMargin, smallMargin, bigMargin, smallMargin);


            viewHolder.messageCardView.setLayoutParams(layoutParams);


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
            //viewHolder.textArtist.setText(song.getArtist());
           // viewHolder.songTitleTextView.setText(song.getSongname());

           // if (song.getSpotifyID() != null) {
                final String spotifyId = song.getSpotifyID();
                showArtistPreviewImage(viewHolder, message);
           // }
        } else {
           hideSpotifySmallImage(viewHolder);
        }


        if (hasWeatherToDisplay(message)) {
           // viewHolder.weatherText.setText("Temperatur: " + message.getWeatherJSON().getMain().getTemp() + "°");
            ImageLoader imageLoader = ImageLoader.getInstance();
            if (message.getWeatherJSON().getWeatherList() != null) {
                viewHolder.weatherImageSmall.setTypeface(getWeatherIconTypeface(getContext()));
                String iconString = message.getWeatherJSON().getWeatherList().get(0).getIcon();
                String weatherString = getContext().getResources().getString(getWeatherStringForIconString( iconString, getContext()));
                viewHolder.weatherImageSmall.setText(weatherString);

                showSmallWeatherImage(viewHolder);
            } else {

                hideSmallWeatherImage(viewHolder);
            }
        }
            else {

                hideSmallWeatherImage(viewHolder);
            }


        if (hasActivityToDisplay(message))
        {
            //viewHolder.activityImage.setTypeface(getMaterialIconTypeface(getContext()));
            viewHolder.activityImageSmall.setTypeface(getMaterialIconTypeface(getContext()));
            String activityText=getContext().getResources().getString(matchActivityTypesToStringRes(message.getActivityValue()));
          //  viewHolder.activityImage.setText(getContext().getResources().getString(matchActivityTypesToStringRes(message.getActivityValue())));
            viewHolder.activityImageSmall.setText(activityText);

            showSmallActivityImage(viewHolder);

        }
        else {
            hideSmallActivityImage(viewHolder);
        }


        if (hasDistanceToDisplay(message)) {

            float distance = message.getUsersDistance().getDistanceValue();
         //   showDistanceOnDistanceView(viewHolder, LocationHelper.getInstance().computeDistanceString(distance), LocationHelper.getInstance().computeDistanceFractionForView(distance));

            showSmallDistanceText(viewHolder, LocationHelper.getInstance().computeDistanceString(distance));

        }
        else if (message.getSenderLocation() != null && fromOtherUser && !timeoutReachedForDistanceComputation(message.getTimestamp())) {
                final GeoLocation otherUserDistance = message.getSenderLocation();
                        if (((SingleConversationActivity)getContext()).getLastLocation() != null) {
                            Location location = ((SingleConversationActivity)getContext()).getLastLocation();
                            float[] res = new float[1];
                            Location.distanceBetween(otherUserDistance.getLatitude(), otherUserDistance.getLongitude(), location.getLatitude(), location.getLongitude(), res);
                            float distanceInMeters = res[0];
                            message.setUsersDistance(new Distance(distanceInMeters));
                            showSmallDistanceText(viewHolder,  LocationHelper.getInstance().computeDistanceString(distanceInMeters));
                            updateUsersDistance(distanceInMeters, message);
                            Log.d("TAG", "update users distance");
                        } else {
                            hideSmallDistanceText(viewHolder);
                        }
            }
        else {

           hideSmallDistanceText(viewHolder);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toggleDetailViewsVisibility(message, viewHolder);
                setMessage(message);
                Intent intent = new Intent(getContext(), ItemDetailActivity.class);
                intent.putExtra("username", fromUser);

                intent.putExtra("music", hasSpotifyToDisplay(message));
                intent.putExtra("weather", hasWeatherToDisplay(message));
                intent.putExtra("distance", hasDistanceToDisplay(message));
                intent.putExtra("activity", hasActivityToDisplay(message));
                getContext().startActivity(intent);
            }
        });


            return convertView;

    }




    private void hideSmallActivityImage(ViewHolder viewHolder) {
        viewHolder.activityImageSmall.setVisibility(View.GONE);
    }

    private void showSmallActivityImage(ViewHolder viewHolder) {
        viewHolder.activityImageSmall.setVisibility(View.VISIBLE);
    }

    private void showSmallWeatherImage(ViewHolder viewHolder) {
        viewHolder.weatherImageSmall.setVisibility(View.VISIBLE);
    }

    public void hideSmallWeatherImage(ViewHolder viewHolder)
    {
        viewHolder.weatherImageSmall.setVisibility(View.GONE);
    }

    private void hideSpotifySmallImage(ViewHolder viewHolder) {
        viewHolder.artistImageSmall.setVisibility(View.GONE);
    }

    public void showSmallDistanceText(ViewHolder viewHolder, String distanceText)
    {
        viewHolder.smallDistanceView.setVisibility(View.VISIBLE);
        viewHolder.smallDistanceView.setText(distanceText);
        viewHolder.smallDistanceViewImage.setVisibility(View.VISIBLE);

    }

    public  void hideSmallDistanceText(ViewHolder viewHolder)
    {
        viewHolder.smallDistanceView.setVisibility(View.GONE);
        viewHolder.smallDistanceViewImage.setVisibility(View.GONE);

    }

    private void showArtistPreviewImage(final ViewHolder viewHolder, Message message) {
        viewHolder.artistImageSmall.setVisibility(View.VISIBLE);
        final ImageLoader imageLoader = ImageLoader.getInstance();

        if (message.getSong()!=null && message.getSong().getSpotifyID()!=null) {

            if (viewHolder.artistImageSmall.getTag() == null ||
                    !viewHolder.artistImageSmall.getTag().equals(message.getSong().getSpotifyImageURL())) {
                imageLoader.displayImage(message.getSong().getSpotifyImageURL(), viewHolder.artistImageSmall);

            }
        }
      else {

            imageLoader.displayImage("", viewHolder.artistImageSmall);
        }
    }


    public boolean hasWeatherToDisplay(Message message)
    {
        return (message.getWeatherJSON() != null);
    }

    public  boolean hasSpotifyToDisplay(Message message)
    {
        return (message.getSong()!=null);
    }

    public boolean hasActivityToDisplay(Message message)
    {
        return message.getActivityValue()>=0 && message.getActivityValue()!=DetectedActivity.UNKNOWN;
    }

    public boolean hasDistanceToDisplay(Message message)
    {

        return (message.getUsersDistance() != null);
    }






    public void makeActivityViewVisible(ViewHolder viewHolder, boolean visible)
    {
        if (visible)
        {
            viewHolder.activityView.setVisibility(View.VISIBLE);
        }
        else  {

            viewHolder.activityView.setVisibility(View.GONE);
        }


    }


    public static int getWeatherStringForIconString(String iconString, Context context) {
        int id = context.getResources().getIdentifier("w"+iconString.replace("d","").replace("n",""), "string",context.getPackageName());
        Log.d("TAG", "id is: "+id);
        return id;
    }


    public static int matchActivityTypesToStringRes(int activityType)
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
/*
    private void showPlayButtonAndSpotifyImage(final ViewHolder viewHolder, final String spotifyId) {
        SpotifyServiceSingleton.getInstance().getPhotoPathForTrack(spotifyId, new SpotifyPhotoCallback() {
            @Override
            public void photoFetched(String photo) {
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(photo, viewHolder.songImageView);
                imageLoader.displayImage(photo, viewHolder.artistImageSmall);
            }
        });


        viewHolder.button.setVisibility(View.VISIBLE);

        viewHolder.artistImageSmall.setVisibility(View.VISIBLE);



        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() != null) {

                    ((SingleConversationActivity) getContext()).playSong(spotifyId);

                }
            }
        });
    }*/




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
        TextView messageSender;
        TextView messageDate;
        public CardView messageCardView;
        public ImageView artistImageSmall;
        public RelativeLayout activityView;
        public TextView weatherImageSmall;
        public TextView activityImageSmall;
        public TextView smallDistanceView;
        public ImageView smallDistanceViewImage;
    }

    static class ReceivedRowViewHolder extends ViewHolder{};
    static class SendRowViewHolder extends ViewHolder{};
}

