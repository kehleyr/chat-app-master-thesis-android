package com.app.charlotte.myapplication.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.charlotte.myapplication.Application;
import com.app.charlotte.myapplication.LocationHelper;
import com.app.charlotte.myapplication.LoggingHelper;
import com.app.charlotte.myapplication.R;
import com.app.charlotte.myapplication.SpotifyPhotoCallback;
import com.app.charlotte.myapplication.UserSingleton;
import com.app.charlotte.myapplication.location.DistanceView;
import com.app.charlotte.myapplication.spotify.RefreshAndAccessToken;
import com.app.charlotte.myapplication.spotify.Song;
import com.app.charlotte.myapplication.spotify.SpotifyServiceSingleton;
import com.google.android.gms.location.DetectedActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a
 */
public class ItemDetailActivity extends AppCompatActivity implements ConnectionStateCallback, PlayerNotificationCallback{

    public static final int MUSIC_VIEW_VISIBILITY = 0;
    public static final int DISTANCE_VIEW_VISIBILITY = 1;
    public static final int WEATHER_VIEW_VISIBILIY = 2;
    public static final int ACTIVITY_VIEW_VISIBILITY = 3;
    private View distanceViewDivider;
    private RelativeLayout musicView;
    private ImageView songImageView;
    private LinearLayout distanceViewLayout;
    private DistanceView distanceView;
    private TextView weatherText;
    private View spotifyLine;
    private TextView textArtist;
    private TextView activityImage;
    private RelativeLayout weatherView;

    private TextView songTitleTextView;
    private ImageButton button;
    private RelativeLayout activityView;
    private Message message;
    private TextView weatherImage;
    private TextView activityText;
    private TextView distanceText;
    private List listDividers;
    private LinearLayout linearLayoutDetail;
    private TextView distanceViewText;

    public String getCurrentSongID() {
        return currentSongID;
    }
    private Config playerConfig;
    private static final String CLIENT_ID = "7deb76a46e184f88ad88542ed347edcc";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "yourcustomprotocol://callback";
    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_SECRET = "5adf6f19f1bf42298b5e95300f264f3f";


    public void setCurrentSongID(String currentSongID) {
        this.currentSongID = currentSongID;
    }

    private String currentSongID;

    public void setIsPaused(boolean isPaused) {
        boolean isPaused1 = isPaused;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    private boolean[] shouldDisplay=new boolean[4];

    private boolean isPlaying;
    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Log.d("TAG", "activity create called");





        // Show the Up button in the action bar.

    message = ConversationListAdapter.getMessage();

       musicView = (RelativeLayout)findViewById(R.id.music_view);
       songImageView = (ImageView) findViewById(R.id.artistImage);
        songTitleTextView = (TextView) findViewById(R.id.textSong);
    button = (ImageButton) findViewById(R.id.button);
     distanceViewLayout = (LinearLayout)findViewById(R.id.distance_view_layout);
        distanceView = (DistanceView) findViewById(R.id.distanceView);
        weatherImage = (TextView) findViewById(R.id.weather_image);
   weatherText=(TextView)findViewById(R.id.weather_text);
   textArtist=(TextView) findViewById(R.id.textArtist);
     activityImage= (TextView) findViewById(R.id.activity_image);
weatherView = (RelativeLayout)findViewById(R.id.weather_layout);
    activityView = (RelativeLayout) findViewById(R.id.activity_view);
        activityText = (TextView) findViewById(R.id.activity_text);
        linearLayoutDetail=(LinearLayout) findViewById(R.id.linear_layout_detail);
        distanceViewText=(TextView)findViewById(R.id.distance_text);



/*    intent.putExtra("music", hasSpotifyToDisplay(message));
                intent.putExtra("weather", hasWeatherToDisplay(message));
                intent.putExtra("distance", hasDistanceToDisplay(message));
                intent.putExtra("activity", hasActivityToDisplay(message));*/
        Intent intent = getIntent();

   shouldDisplay[MUSIC_VIEW_VISIBILITY]=intent.getBooleanExtra("music", false);
     shouldDisplay[WEATHER_VIEW_VISIBILIY]=intent.getBooleanExtra("weather", false);
       shouldDisplay[ACTIVITY_VIEW_VISIBILITY]=intent.getBooleanExtra("activity", false);
      shouldDisplay[DISTANCE_VIEW_VISIBILITY] = intent.getBooleanExtra("distance", false);


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //

       listDividers = getListDividers();
        toggleDetailViewsVisibility();

    }
    public void toggleDetailViewsVisibility()
    {

            makeMusicViewVisible(shouldDisplay[MUSIC_VIEW_VISIBILITY]);
            makeActivityViewVisible(shouldDisplay[ACTIVITY_VIEW_VISIBILITY]);
            makeDistanceViewVisible(shouldDisplay[DISTANCE_VIEW_VISIBILITY]);
            makeWeatherViewVisible(shouldDisplay[WEATHER_VIEW_VISIBILIY]);
    }

    public void loginToSpotify() {
        if (getCurrentAccessToken()!=null && isAccessTokenValid() && mPlayer!=null && mPlayer.isInitialized())
        {

            Log.d("ItemDetail", "player is already working, do nothing");
        }

        else if (getCurrentAccessToken()!=null && isAccessTokenValid())
        {
            Log.d("TAG", "has valid access token");
            Log.d("TAG", "access token is: " + getCurrentAccessToken());
            updateSpotifyPlayerWithConfig(getCurrentAccessToken());

        }

        else if (getCurrentAccessToken()!=null && getCurrentRefreshToken()!=null && !isAccessTokenValid())
        {

            Log.d("TAG", "get new access token");

            getNewAccessTokenWithRefreshToken(getCurrentRefreshToken());
        }
        else {

            Log.d("TAG", "new login for spotify");

            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                    AuthenticationResponse.Type.CODE,
                    REDIRECT_URI);
            builder.setScopes(new String[]{"user-read-private", "streaming"});
            AuthenticationRequest request = builder.build();
            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);



        }
    }

    private void makeWeatherViewVisible(boolean detailsViewVisible) {
        if (detailsViewVisible)
        {
            weatherView.setVisibility(View.VISIBLE);

            Spanned text = Html.fromHtml("<b><font color='#4a494d'>Temperatur: </font></b>" + message.getWeatherJSON().getMain().getTemp() + "°"
            );

          weatherText.setText(text);

            if (message.getWeatherJSON().getWeatherList() != null) {
             weatherImage.setTypeface(ConversationListAdapter.getWeatherIconTypeface(this));
                String iconString = message.getWeatherJSON().getWeatherList().get(0).getIcon();
                String weatherString = getResources().getString(ConversationListAdapter.getWeatherStringForIconString(iconString, this));
               weatherImage.setText(weatherString);


            }

        }
        else {
            weatherView.setVisibility(View.GONE);
        }

        if (listDividers.contains(WEATHER_VIEW_VISIBILIY))
        {

            View separator = getSeparator();
           int index = linearLayoutDetail.indexOfChild(weatherView);
            linearLayoutDetail.addView(separator, index+1);
            Log.d("TAG", "add separator for weather view");
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hasSpotify  =sharedPref.getBoolean(getString(R.string.spotify_enabled_string), false);

        if (hasSpotify) {
            loginToSpotify();
        }

        LoggingHelper.getInstance().logEvent(LoggingHelper.OPEN_MESSAGE_DETAILS_EVENT, UserSingleton.getInstance().getCurrentUser(this).getUsername());

    }

    @NonNull
    private View getSeparator() {
        View separator= new View(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        separator.setBackground(getResources().getDrawable(android.R.drawable.divider_horizontal_bright));
        int dpValue = 20; // margin in dips
        float d = getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d);

        Log.d("TAG", "margin is: "+margin);

        layoutParams.setMargins(margin,0,margin,0);
        separator.setLayoutParams(layoutParams);
        return separator;
    }

    private void showDistanceOnDistanceView(String distance2, float distance3) {
        distanceView.setDistanceAnnotation(distance2);
       distanceView.setDistanceFraction(distance3);
    }
    private void makeDistanceViewVisible(boolean detailsViewVisible) {
        if (detailsViewVisible)
        {
           showDistanceOnDistanceView(LocationHelper.getInstance().computeDistanceString(message.getUsersDistance().getDistanceValue()), LocationHelper.getInstance().computeDistanceFractionForView(message.getUsersDistance().getDistanceValue()));

            distanceView.setVisibility(View.VISIBLE);
            distanceViewText.setVisibility(View.VISIBLE);
            Spanned text = Html.fromHtml("Eure <b><font color='#4a494d'>Distanz</font></b>");
            distanceViewText.setText(text);
          //  distanceText.setVisibility(View.VISIBLE);
        }
        else {
            distanceView.setVisibility(View.GONE);
            distanceViewText.setVisibility(View.GONE);
        }

        if (listDividers.contains(DISTANCE_VIEW_VISIBILITY))
        {

            View separator = getSeparator();
            int index = linearLayoutDetail.indexOfChild(distanceViewLayout);
            linearLayoutDetail.addView(separator, index+1);
            Log.d("TAG", "add separator for distance view");

        }

    }

    private void makeActivityViewVisible(boolean detailsViewVisible) {
        if (detailsViewVisible)
        {
            activityView.setVisibility(View.VISIBLE);
         activityImage.setTypeface(ConversationListAdapter.getMaterialIconTypeface(this));
        activityImage.setText(getResources().getString(ConversationListAdapter.matchActivityTypesToStringRes(message.getActivityValue())));

            Spanned text = Html.fromHtml("<b><font color='#4a494d'>Aktivität: </font></b>" +String.format(getResources().getString(getStringIdForActivity(message.getActivityValue())), message.getFromUserDisplayName()));

                    activityText.setText(text);

        }
        else {

            activityView.setVisibility(View.GONE);
        }

        if (listDividers.contains(ACTIVITY_VIEW_VISIBILITY))
        {

            View separator = getSeparator();
            int index = linearLayoutDetail.indexOfChild(activityView);
            linearLayoutDetail.addView(separator, index+1);
            Log.d("TAG", "add separator for activity view");

        }
    }

    private void makeMusicViewVisible(boolean detailsViewVisible) {
        if (detailsViewVisible)
        {
            musicView.setVisibility(View.VISIBLE);
            Song song= message.getSong();
      textArtist.setText(song.getArtist());
           songTitleTextView.setText(song.getSongname());

            if (song.getSpotifyID()!=null)
            {

                showPlayButtonAndSpotifyImage(message);
            }
        }
        else {

            musicView.setVisibility(View.GONE);
        }

        if (listDividers.contains(MUSIC_VIEW_VISIBILITY))
        {

            View separator = getSeparator();
            int index = linearLayoutDetail.indexOfChild(musicView);
            linearLayoutDetail.addView(separator, index+1);
            Log.d("TAG", "add separator for music view");

        }
    }


    private void showPlayButtonAndSpotifyImage(final Message message) {
         ImageLoader imageLoader = ImageLoader.getInstance();
        if (message.getSong() != null && message.getSong().getSpotifyImageURL() != null)
        {
            imageLoader.displayImage(message.getSong().getSpotifyImageURL(), songImageView);

        }


     button.setVisibility(View.VISIBLE);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              playSong(message.getSong().getSpotifyID());


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent intent = new Intent(this, SingleConversationActivity.class);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void playAndResume()
    {
        if (isPlaying)
        {
            mPlayer.pause();
            setIsPaused(true);
            LoggingHelper.getInstance().logEvent(LoggingHelper.STOP_PLAYING_SONG, UserSingleton.getInstance().getCurrentUser(this).getUsername());

        }
        else {

            setIsPaused(false);
            mPlayer.resume();
        }
        //TODO: error handling

    }

    public void playSong(String songID)
    {
        if (mPlayer==null) return;

        if (getCurrentSongID()==null|| (!getCurrentSongID().equals(songID))) {

            Log.d("TAG","play current song: "+getCurrentSongID());

            LoggingHelper.getInstance().logEvent(LoggingHelper.START_PLAYING_SONG, UserSingleton.getInstance().getCurrentUser(this).getUsername());


            setCurrentSongID(songID);
            mPlayer.clearQueue();
            mPlayer.play(currentSongID);
        }
        else {

            playAndResume();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);

                Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer = player;
                        mPlayer.addConnectionStateCallback(ItemDetailActivity.this);
                        mPlayer.addPlayerNotificationCallback(ItemDetailActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                        loginToSpotify();
                    }
                });
            }
            else if (response.getType() == AuthenticationResponse.Type.CODE)
            {



                String code=response.getCode();
                String state =response.getState();

                Call<RefreshAndAccessToken> call = Application.getService().getRefreshAndAccessTokens(code, state);

                call.enqueue(new Callback<RefreshAndAccessToken>() {
                    @Override
                    public void onResponse(Call<RefreshAndAccessToken> call, Response<RefreshAndAccessToken> response) {


                        RefreshAndAccessToken refreshAndAccessToken= response.body();

                        String accessToken = refreshAndAccessToken.getAccess_token();
                        String refreshToken = refreshAndAccessToken.getRefresh_token();

                        Log.d("TAG", "access token is: "+accessToken);
                        Log.d("TAG", "refresh token is: "+refreshToken);

                        int expiresIn = refreshAndAccessToken.getExpires_in();
                        Log.d("TAG", "expires in "+expiresIn);

                        saveAccessTokenToSharedPreferencess(accessToken, expiresIn);
                        saveRefreshTokenToSharedPreferences(refreshToken);

                        updateSpotifyPlayerWithConfig(accessToken);


                    }




                    @Override
                    public void onFailure(Call call, Throwable t) {

                    }
                });

            }
        }
    }

    private void updateSpotifyPlayerWithConfig(String accessToken) {
        playerConfig = new Config(ItemDetailActivity.this, accessToken, CLIENT_ID);

        Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                mPlayer = player;
                mPlayer.addConnectionStateCallback(ItemDetailActivity.this);
                mPlayer.addPlayerNotificationCallback(ItemDetailActivity.this);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    public void getNewAccessTokenWithRefreshToken(String refreshToken)
    {
        Call<RefreshAndAccessToken> call =Application.getService().getAccessTokenForRefreshToken(CLIENT_ID, CLIENT_SECRET, refreshToken);


        call.enqueue(new Callback<RefreshAndAccessToken>() {
            @Override
            public void onResponse(Call<RefreshAndAccessToken> call, Response<RefreshAndAccessToken> response) {


                RefreshAndAccessToken refreshAndAccessToken = response.body();
                String accessToken = refreshAndAccessToken.getAccess_token();
                // String refreshToken = refreshAndAccessToken.getRefresh_token();
                int expiresIn = refreshAndAccessToken.getExpires_in();

                saveAccessTokenToSharedPreferencess(accessToken, expiresIn);
                updateSpotifyPlayerWithConfig(accessToken);


                //  saveRefreshTokenToSharedPreferences(refreshToken);

            }


            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

    }

    private void saveRefreshTokenToSharedPreferences(String refreshToken) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("refreshToken", refreshToken);
        editor.commit();
    }

    private void saveAccessTokenToSharedPreferencess(String accessToken, int expiresIn) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("accessToken", accessToken);
        editor.putInt("expiresIn", expiresIn);
        editor.putLong("expiryDate", new Date().getTime() + expiresIn * 1000);
        editor.commit();
    }


    public String getCurrentAccessToken()
    {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return   sharedPref.getString("accessToken", null);

    }


    public boolean isAccessTokenValid()
    {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        long millis = sharedPref.getLong("expiryDate", 0L);
        Date theDate = new Date(millis);

        Date now = new Date();

        return !theDate.before(now);


    }

    public String getCurrentRefreshToken()
    {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString("refreshToken", null);

    }

    @Override
    public void onLoggedIn() {

    }

    @Override
    public void onLoggedOut() {
        Log.e("TAG", "on logged out");
        loginToSpotify();

    }

    @Override
    public void onLoginFailed(Throwable throwable) {

        Log.e("TAG", "login failed: " + throwable.getMessage());

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

        Log.d("TAG", "connection message: "+s);

    }

    @Override
    public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
        //TODO: add most of the other states
        if (isPlaying && (eventType.equals(PlayerNotificationCallback.EventType.PAUSE)|| eventType.equals(PlayerNotificationCallback.EventType.LOST_PERMISSION) || eventType.equals(PlayerNotificationCallback.EventType.EVENT_UNKNOWN))) {
            setIsPlaying(false);

        }
        else if (eventType.equals(PlayerNotificationCallback.EventType.PLAY))
        {

            setIsPlaying(true);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        LoggingHelper.getInstance().logEvent(LoggingHelper.CLOSE_MESSAGE_DETAILS_EVENT, UserSingleton.getInstance().getCurrentUser(this).getUsername());

    }

    @Override
    public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String s) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Spotify.destroyPlayer(this);

        Log.d("TAG", "on destroy called");

    }

    public int getStringIdForActivity(int activity)
    {

        Resources res = getResources();
        switch (activity)
        {
            case DetectedActivity.TILTING: return R.string.tilt_desc;
            case DetectedActivity.IN_VEHICLE: return  R.string.vehicle_desc;
            case DetectedActivity.ON_BICYCLE: return  R.string.bicycle_desc;
            case DetectedActivity.ON_FOOT: return  R.string.onfoot_desc;
            case DetectedActivity.RUNNING: return  R.string.running_desc;
            case DetectedActivity.STILL: return R.string.still_desc;
            case DetectedActivity.WALKING: return R.string.walking_desc;
            default: return R.string.default_desc;
        }

    }

    public List getListDividers()
    {
            LinkedList<Integer> displayCandidates = new LinkedList<>();

        for (int i=0; i<shouldDisplay.length; i++)
        {

            if (shouldDisplay[i]) displayCandidates.add(i);
        }


if (displayCandidates.size()>0)        displayCandidates.removeLast();

        return  displayCandidates;


    }
}
