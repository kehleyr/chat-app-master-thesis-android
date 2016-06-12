package com.example.charlotte.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
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
import java.util.concurrent.TimeUnit;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class SingleConversationActivity extends AppCompatActivity implements ConnectionStateCallback, PlayerNotificationCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String CLIENT_ID = "7deb76a46e184f88ad88542ed347edcc";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "yourcustomprotocol://callback";
    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_SECRET = "5adf6f19f1bf42298b5e95300f264f3f";
    private static final double AMBIENT_NOISE_SAMPLE_SIZE = 0.1;
    private static final int MY_ACCESS_PERMISSION_CONSTANT = 666;
    private Config playerConfig;
    private GoogleApiClient mGoogleApiClient;
    private ReactiveLocationProvider locationProvider;
    private ListView myList;


    public String getCurrentSongID() {
        return currentSongID;
    }

    public void setCurrentSongID(String currentSongID) {
        this.currentSongID = currentSongID;
    }

    private String currentSongID;

    public boolean isPaused() {
        return isPaused;
    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    private boolean isPaused;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    private boolean isPlaying;

    public Player getmPlayer() {
        return mPlayer;
    }

    public void setmPlayer(Player mPlayer) {
        this.mPlayer = mPlayer;
    }

    private Player mPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_conversation);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_ACCESS_PERMISSION_CONSTANT);
            //TODO: what happens if permission is not granted?
        }





        Intent intent = getIntent();
        final String otherUserName = intent.getStringExtra("username");
        String currentUserName="";

        final User currentUser=UserSingleton.getInstance().getCurrentUser();

        if (currentUser!=null)
        {
            currentUserName=currentUser.getUsername();
        }

      myList = (ListView) findViewById(R.id.listView);
        myList.setAdapter(new ConversationListAdapter(this, R.layout.contact_list_item, currentUserName, otherUserName));

        Button sendButton = (Button) findViewById(R.id.send_button);
        final EditText editText = (EditText) findViewById(R.id.editText);

        final String finalCurrentUserName = currentUserName;
        final String finalCurrentUserName1 = currentUserName;
        final String finalCurrentUserName2 = currentUserName;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();

                //TODO: has to be a post request for metadata...

                sendMessage(text, finalCurrentUserName2, otherUserName, finalCurrentUserName, myList);
            }
        });

        loginToSpotify();
        /*

        Intent aware = new Intent(this, Aware.class);
        startService(aware);
        Aware.setSetting(this, "status_plugin_ambient_noise", true);
        Aware.setSetting(this, "plugin_ambient_noise_sample_size", 10);
        Aware.setSetting(this, "frequency_plugin_ambient_noise", 1);
        Aware.setSetting(this, "plugin_ambient_noise_silence_threshold", 30);
        Aware.startPlugin(this, "com.aware.plugin.ambient_noise");
*/

    }

    public void loginToSpotify() {
        if (getCurrentAccessToken()!=null && isAccessTokenValid())
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

    private void sendMessage(final String text, String finalCurrentUserName2, final String otherUserName, final String finalCurrentUserName, final ListView myList) {

        //TODO: if not has google play


        final Message message = new Message(finalCurrentUserName2, otherUserName, text);

        if (MediaPlayingSingleton.getInstance().isPlaying() && MediaPlayingSingleton.getInstance().getCurrentSong() != null) {

            message.setSong(MediaPlayingSingleton.getInstance().getCurrentSong());
        }



        LocationHelper.getInstance().determineLocation(this, getApplicationContext(), new LocationFetchedInteface() {
            @Override
            public void hasFetchedLocation(Location location) {





                if (location!=null)
                {

                    //use coordinates

                    GeoLocation geoLocation=new GeoLocation(location.getLatitude(), location.getLongitude());
                    message.setSenderLocation(geoLocation);
                    //make asynchronous call to weather api through service
                    WeatherHelper.getInstance().getWeather(geoLocation, new WeatherFetchedCallback() {
                        @Override
                        public void onWeatherFetched(WeatherJSON weatherJSON) {
                            //set weather data in message

                            if (weatherJSON!=null) {
                                message.setWeatherJSON(weatherJSON);
                            }
                            //send message to server after callback
                            sendMessageToServer(message);

                        }
                    });


                }
                else {

                    sendMessageToServer(message);
                }


            }
        });

        //TODO: user another senderLocation if not accurate enough?


        boolean useDecibels=false;
/*
        if (useDecibels) {
            new AsyncTask<Void, Void, Double>() {
                @Override
                protected Double doInBackground(Void... voids) {
                    //Initialize audio recorder. Use MediaRecorder.AudioSource.VOICE_RECOGNITION to disable Automated Voice Gain from microphone and use DSP if available
                    int buffer_size = AudioRecord.getMinBufferSize(AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM), AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) * 10;

                    Log.d("TAG", "hertz rate: "+AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM));

                    //Audio data buffer
                    short[] audio_data = new short[buffer_size];
                    AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM), AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size);

                    if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                        if (recorder.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
                            recorder.startRecording();
                            Log.d("TAG", "start recording");
                        }

                        double now = System.currentTimeMillis();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("TAG", "now read audio data");
                      int recordedShorts= recorder.read(audio_data, 0, buffer_size);


                        recorder.stop();
                        recorder.release();


                        AudioAnalysis audio_analysis = new AudioAnalysis(getApplicationContext(), audio_data, buffer_size);


                        return  audio_analysis.getdB(recordedShorts);
                        // return null;
                    }



                    return null;

                }

                @Override
                protected void onPostExecute(Double aVoid) {
                    super.onPostExecute(aVoid);

                    AmbientNoise ambientNoise = new AmbientNoise(aVoid);
                    message.setAmbientNoise(ambientNoise);

                    Call call = Application.getService().writeMessagePost(message);
                    Log.d("TAG", finalCurrentUserName + " " + otherUserName + text);

                    Log.d("TAG", "created call");


                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            Log.d("TAG", "on response");


                            ((ConversationListAdapter) myList.getAdapter()).initializeAdapter();
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {


                            Log.d("TAG", "send failed: "+t.getMessage()+t.getCause());
                        }


                    });
                }

                @Override
                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                }
            }.execute();

        }

        else
*/



            Log.d("TAG", finalCurrentUserName + " " + otherUserName + text);

            Log.d("TAG", "created call");

    //    sendMessageToServer(message);
//TODO: if/else


    }

    private void sendMessageToServer(Message message) {
        Call call = Application.getService().writeMessagePost(message);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("TAG", "on response");


                ((ConversationListAdapter) myList.getAdapter()).initializeAdapter();
            }

            @Override
            public void onFailure(Call call, Throwable t) {


                Log.d("TAG", "sending failed"+t.getMessage()+t.getCause());
            }


        });
    }


    public void playAndResume()
    {
        if (isPlaying)
        {
            mPlayer.pause();
            setIsPaused(true);
        }
        else {

            setIsPaused(false);
            mPlayer.resume();
        }
        //TODO: error handling

    }

    public void playSong(String songID)
    {

        if (getCurrentSongID()==null|| (!getCurrentSongID().equals(songID))) {

            Log.d("TAG","play current song: "+getCurrentSongID());
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
                        mPlayer.addConnectionStateCallback(SingleConversationActivity.this);
                        mPlayer.addPlayerNotificationCallback(SingleConversationActivity.this);
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

               Call<RefreshAndAccessToken> call =Application.getService().getRefreshAndAccessTokens(code, state);

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
        playerConfig = new Config(SingleConversationActivity.this, accessToken, CLIENT_ID);

        Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                mPlayer = player;
                mPlayer.addConnectionStateCallback(SingleConversationActivity.this);
                mPlayer.addPlayerNotificationCallback(SingleConversationActivity.this);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    public void getNewAccessTokenWithRefreshToken(String refreshToken)
    {
        Call<RefreshAndAccessToken> call =Application.getService().getAccessTokenForRefreshToken(CLIENT_ID, CLIENT_SECRET,refreshToken);


        call.enqueue(new Callback<RefreshAndAccessToken>() {
            @Override
            public void onResponse(Call<RefreshAndAccessToken> call, Response<RefreshAndAccessToken> response) {


               RefreshAndAccessToken refreshAndAccessToken= response.body();
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

        Log.e("TAG", "login failed: "+throwable.getMessage());

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

        Log.d("TAG", "connection message: "+s);

    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        //TODO: add most of the other states
        if (isPlaying && (eventType.equals(EventType.PAUSE)|| eventType.equals(EventType.LOST_PERMISSION) || eventType.equals(EventType.EVENT_UNKNOWN))) {
            setIsPlaying(false);

        }
        else if (eventType.equals(EventType.PLAY))
        {

            setIsPlaying(true);
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "on destroy called");



    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

            switch (requestCode) {
                case MY_ACCESS_PERMISSION_CONSTANT: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                        LocationHelper.getInstance().setAllowLocation(true);
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.

                    } else {

                      LocationHelper.getInstance().setAllowLocation(false);
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request
            }

        }
}
