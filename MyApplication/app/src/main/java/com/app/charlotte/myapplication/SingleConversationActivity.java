package com.app.charlotte.myapplication;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.app.charlotte.myapplication.detectedactivities.ActivitiesIntentService;
import com.app.charlotte.myapplication.detectedactivities.ActivityRecognitionConstants;
import com.app.charlotte.myapplication.location.GeoLocation;
import com.app.charlotte.myapplication.spotify.MediaPlayingSingleton;
import com.app.charlotte.myapplication.spotify.Song;
import com.app.charlotte.myapplication.spotify.SpotifyServiceSingleton;
import com.app.charlotte.myapplication.weather.WeatherFetchedCallback;
import com.app.charlotte.myapplication.weather.WeatherHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleConversationActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,SensorEventListener, ResultCallback<Status>, LocationListener {

    private static final double AMBIENT_NOISE_SAMPLE_SIZE = 0.1;
    private static final int MY_ACCESS_PERMISSION_CONSTANT = 666;
    public static final String ACTIVITY_UPDATE_FILTTEr = "activityUpdate";
    public static final int ACTIVITY_UPDATE_FREQUENCY = 20;
    public static final int LOCATION_REQUEST_INTERVAL = 300000;
    private Date lastAcitivtyValueSaved;
    private GoogleApiClient mGoogleApiClient;
    private ListView myList;
    private BroadcastReceiver mBroadcastReceiver;
    //in ms
    public static final long WEATHER_UPDATE_THRESHOLD=600000;
    private Date lastWeatherFetched;
    public static final String UPDATE_CONVERSATION_ACTION="updateConversation";
    private LocationRequest mLocationRequest;
    private List<DetectedActivity> lastDetectedActivityList=new ArrayList<>();

    Location lastLocation=null;
    private int lastActivity=-1;
    private boolean shouldTilt=true;
    private String otherUserName;
    private String otherUserDisplayName;

    public SingleConversationActivity() {
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d("conversation", "on new intent called");
    }

    private float lastAccelerometerValues[][] = new float [25][3];
    private int numMeasures=0;

    private String chatDescription;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));

        super.onCreate(savedInstanceState);



        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        shouldTilt = sharedPref.getBoolean(getResources().getString(R.string.tilt_enabled), true);


        setContentView(R.layout.activity_single_conversation);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_ACCESS_PERMISSION_CONSTANT);
            //TODO: what happens if permission is not granted?
        }

      mBroadcastReceiver = new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
              Log.d("TAG", "broadcast received");
              if (intent.getAction().equals(UPDATE_CONVERSATION_ACTION)) {
                  updateMessages();
              } else if (intent.getAction().equals(ActivityRecognitionConstants.STRING_ACTION)) {

                  ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

                  DetectedActivity mostProbableActivity = result.getMostProbableActivity();

                  Log.d("TAG", "activity type is: " + mostProbableActivity.getType() + " confidence: " + mostProbableActivity.getConfidence() + "shouldTilt: " + shouldTilt);

                  if (mostProbableActivity.getConfidence() > 50 && mostProbableActivity.getType() != DetectedActivity.UNKNOWN && !(!shouldTilt && mostProbableActivity.getType() == DetectedActivity.TILTING)) {

                      lastActivity = mostProbableActivity.getType();
                      lastDetectedActivityList = result.getProbableActivities();
// Get the type of activity
                  }
              }

          }

          ;

      };
        //senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

       // senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Intent intent = getIntent();
       otherUserName = intent.getStringExtra("username");
      otherUserDisplayName=intent.getStringExtra("displayName");
        String currentUserName="";

        final User currentUser=UserSingleton.getInstance().getCurrentUser();

        if (currentUser!=null)
        {
            currentUserName=currentUser.getUsername();
        }

        chatDescription=currentUserName+otherUserName;
        lastWeatherFetched=getDateFromSharedPrefs("weather"+chatDescription);
        lastAcitivtyValueSaved=getDateFromSharedPrefs("activity"+chatDescription);

        Log.d("SingleConversationActiv", lastWeatherFetched.toString());


        myList = (ListView) findViewById(R.id.listView);
        myList.setDividerHeight(0);
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
                editText.setText("");
                createMessage(text, finalCurrentUserName2, otherUserName, finalCurrentUserName, myList);
            }
        });

        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setTitle(otherUserDisplayName);
        }


        //check if user has enabled spotify through shared preferences...can be annoying otherwise


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest= new LocationRequest();
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }



    @Override
    protected void onRestart() {
        super.onRestart();
/*
        if (myList!=null && myList.getAdapter()!=null)
        {

            ((ConversationListAdapter) myList.getAdapter()).initializeAdapter();

        }*/

    }

    public void scrollMyListViewToBottom() {
        myList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                myList.setSelection(myList.getAdapter().getCount() - 1);
            }
        });
    }



    public void writeDateToSharedPrefs(String prefString, Date value)
    {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(prefString, value.getTime());
        editor.commit();
    }

    public Date getDateFromSharedPrefs(String prefString) {
        SharedPreferences sharedPref =getPreferences(Context.MODE_PRIVATE);
        long date = sharedPref.getLong(prefString, 0);
        return new Date(date);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
outState.putString("username", otherUserName);
        outState.putString("displayname", otherUserDisplayName);
        super.onSaveInstanceState(outState);
    }

    private void createMessage(final String text, String finalCurrentUserName2, final String otherUserName, final String finalCurrentUserName, final ListView myList) {

        //TODO: if not has google play

        Log.d("singleconversation", "get accelerometer average: "+getAccelerationAverage(lastAccelerometerValues));

        final Message message = new Message(finalCurrentUserName2, otherUserName, text);
        //wird immer gesetzt, ist im Zweifel einfach -1
        message.setActivityValue(lastActivity);
        message.setDetectedActivityList(lastDetectedActivityList);

        final List<ApiRequest> requestList = new ArrayList<>();

        Location location=null;
        if (lastLocation!=null)
        {
            location=lastLocation;
        }
        else if (LocationHelper.getInstance().isAllowLocation() && mGoogleApiClient.isConnected()){
            location=  LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
        GeoLocation geoLocation=null;

        if (location!=null)
        {
           geoLocation=new GeoLocation(location.getLatitude(), location.getLongitude());
            message.setSenderLocation(geoLocation);
        }
        if (MediaPlayingSingleton.getInstance().isPlaying() && MediaPlayingSingleton.getInstance().getCurrentSong() != null){
            message.setSong(MediaPlayingSingleton.getInstance().getCurrentSong());
        }

        if (location!=null && canRefetchWeather()){
            ApiRequest request = new ApiRequest<Weather>(Weather.class);
            WeatherHelper.getInstance().setRequest(request);
                    requestList.add(request);
            WeatherHelper.getInstance().getWeather(geoLocation, new WeatherFetchedCallback() {
                @Override
                public void onWeatherFetched(WeatherJSON weatherJSON) {
                    if (weatherJSON != null) {
                        message.setWeatherJSON(weatherJSON);
                    }
                    //should proceed anyway
                    WeatherHelper.getInstance().getRequest().setHasFetched(true);
                    checkIfFetchedAllAndSendMessage(message, requestList);
                }
            });
        }
        Song currentPlayingSong = MediaPlayingSingleton.getInstance().getCurrentSong();
        if (MediaPlayingSingleton.getInstance().isPlaying() && MediaPlayingSingleton.getInstance().getCurrentSong() != null && currentPlayingSong.getSpotifyID()==null)
        {
            ApiRequest spotifyIdRequest = new ApiRequest<Song>(Song.class);
            SpotifyServiceSingleton.getInstance().setRequest(spotifyIdRequest);
            requestList.add(spotifyIdRequest);
            final Song song=MediaPlayingSingleton.getInstance().getCurrentSong();
            SpotifyServiceSingleton.getInstance().getSpotifyIdForSongData(song.getArtist(), song.getSongname(), new SpotifyTrackCallback() {
                @Override
                public void trackFetched(String trackId) {
                    if (trackId != null) {
                        song.setSpotifyID(trackId);
                    }
                    //should proceed anyway....
                    SpotifyServiceSingleton.getInstance().getRequest().setHasFetched(true);
                    checkIfFetchedAllAndSendMessage(message, requestList);
                }
            });
        }
        checkIfFetchedAllAndSendMessage(message, requestList);

    }

    public void checkIfFetchedAllAndSendMessage(Message message, List<ApiRequest> requestList){
        for (ApiRequest request: requestList)
        {
            if (!request.isHasFetched()) {
                Log.d("TAG", "request has not been fetched "+request.returnType());
                return;
            }
        }

        sendMessageToServer(message);


    }

    public void updateMessages()
    {
        ((ConversationListAdapter) myList.getAdapter()).initializeAdapter();
    }


    public boolean canRefetchWeather()
    {
        /*
        long diff=WEATHER_UPDATE_THRESHOLD+1;
        Date now = new Date();
        if (lastWeatherFetched!=null) {
            diff = now.getTime() - lastWeatherFetched.getTime();
            Log.d("TAG", "diff is: "+diff);
        }
        else {

            Log.d("TAG", "lastweatherfetched is null");
        }
        Log.d("TAG", "diff is: "+diff);

        lastWeatherFetched=now;
        return diff > WEATHER_UPDATE_THRESHOLD;*/
        return true;

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




    @Override
    public void onConnected(Bundle bundle) {

        requestLocationUpdates();
        requestActivityUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
       // mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e("TAG", "on connection failed: " + connectionResult.getErrorMessage());

    }

    @Override
    protected void onPause() {
        super.onPause();
        Application.setInSingleConversationActivity(false);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        if (mGoogleApiClient.isConnected()) {
            removeActivityUpdates();
            removeLocationUpdates();

        }
        //senSensorManager.unregisterListener(this);

    }

    private void removeLocationUpdates() {

        if (mGoogleApiClient.isConnected())
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.setInSingleConversationActivity(true);

        IntentFilter filter=new IntentFilter(UPDATE_CONVERSATION_ACTION);
        filter.addAction(ActivityRecognitionConstants.STRING_ACTION);
        //filter.addAction(ACTIVITY_UPDATE_FILTTEr);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    if (mGoogleApiClient.isConnected()) {
        requestActivityUpdates();
        requestLocationUpdates();

    }

      //  senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void requestLocationUpdates() {
        if (LocationHelper.getInstance().isAllowLocation()) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
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

    @Override
    public void onSensorChanged(SensorEvent event) {


        numMeasures++;
        final float alpha = 0.8f;

        float[] gravity=new float[3];
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        float[] linear_acceleration = new float[3];
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];


        if (numMeasures>=5)
        {

            numMeasures=1;
        }



        lastAccelerometerValues[numMeasures-1][0]=linear_acceleration[0];
        lastAccelerometerValues[numMeasures-1][1]=linear_acceleration[1];
        lastAccelerometerValues[numMeasures-1][2]=linear_acceleration[2];

        Log.d("TAG","average is: "+getAccelerationAverage(lastAccelerometerValues));
    }

    public float getAccelerationAverage(float[][] measures)
    {
        float sum=0.0f;
        int notNullValues=0;
        for (float[] axxisArray: measures)
        {
           // Log.d("TAG", "magnitude: "+getMagnitude(axxisArray)+ "x: "+axxisArray[0]+" y: "+axxisArray[1]+ " z: "+axxisArray[2]);
            sum+=getMagnitude(axxisArray);
           // Log.d("TAG", "current sum= "+sum);

        }

       // Log.d("TAG", "sum= "+sum+ "measures length: "+measures.length+ "sum/measures.length: "+sum/measures.length);

        return  sum/measures.length;
    }

    public float getMagnitude(float[] axxisMeasure)
    {

        return (float) Math.sqrt(axxisMeasure[0]*axxisMeasure[0]+axxisMeasure[1]*axxisMeasure[1]+axxisMeasure[2]*axxisMeasure[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        writeDateToSharedPrefs("weather"+chatDescription, lastWeatherFetched);
        writeDateToSharedPrefs("activity"+chatDescription, lastAcitivtyValueSaved);
    }

    public void requestActivityUpdates() {
        if (!mGoogleApiClient.isConnected()) {
            //Toast.makeText(this, "GoogleApiClient not yet connected", Toast.LENGTH_SHORT).show();
        } else {


            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, ACTIVITY_UPDATE_FREQUENCY, getActivityDetectionPendingIntent()).setResultCallback(this);
        }
    }

    public void removeActivityUpdates() {
        if (mGoogleApiClient.isConnected())
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, getActivityDetectionPendingIntent()).setResultCallback(this);
    }


    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, ActivitiesIntentService.class);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.e("TAG", "Successfully added activity detection.");

        } else {
            Log.e("TAG", "Error: " + status.getStatusMessage());
        }
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocation=location;


        Log.d("TAG", "new location: " + location.getLatitude() + "  " + location.getLongitude());

    }

}
