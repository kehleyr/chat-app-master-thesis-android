package com.app.charlotte.myapplication.chat;

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
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.app.charlotte.myapplication.ApiRequest;
import com.app.charlotte.myapplication.Application;
import com.app.charlotte.myapplication.LocationHelper;
import com.app.charlotte.myapplication.LoggingHelper;
import com.app.charlotte.myapplication.R;
import com.app.charlotte.myapplication.SpotifyPhotoCallback;
import com.app.charlotte.myapplication.SpotifyTrackCallback;
import com.app.charlotte.myapplication.User;
import com.app.charlotte.myapplication.UserSingleton;
import com.app.charlotte.myapplication.WeatherJSON;
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
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.models.Track;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleConversationActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,SensorEventListener, ResultCallback<Status>, LocationListener {

    private static final int MY_ACCESS_PERMISSION_CONSTANT = 666;
    public static final int ACTIVITY_UPDATE_FREQUENCY = 1000; // 1 Sekunde
    public static final int LOCATION_REQUEST_INTERVAL = 60000; // 1 Minute
    private static Date lastAcitivtyValueSaved;
    private Date lastLocationValueSaved;
    private GoogleApiClient mGoogleApiClient;
    private ListView myList;
    private BroadcastReceiver mBroadcastReceiver;
    //in ms
    private Date lastWeatherFetched;
    public static final String UPDATE_CONVERSATION_ACTION="updateConversation";
    private LocationRequest mLocationRequest;
    private static List<DetectedActivity>  lastDetectedActivityList=new ArrayList<>();

    Location lastLocation=null;
    private static int lastActivity=-1;
    private boolean shouldTilt=true;
    private String otherUserName;
    private String otherUserDisplayName;
    private int lastActivityValidity;
    private static Date activityOnExitDate;


    public SingleConversationActivity() {
    }
/*
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d("TAG", "on new intent called");
                String currentUserName="";

        otherUserName = intent.getStringExtra("username");


        final User currentUser=UserSingleton.getInstance().getCurrentUser();

        if (currentUser!=null)
        {
            currentUserName=currentUser.getUsername();
        }


        if (myList!=null) {
            myList.setAdapter(new ConversationListAdapter(this, R.layout.contact_list_item, currentUserName, otherUserName));

        }

        otherUserDisplayName=intent.getStringExtra("displayName");
        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setTitle(otherUserDisplayName);
        }



    }*/

    private float lastAccelerometerValues[][] = new float [25][3];
    private int numMeasures=0;

    private String chatDescription;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));

        super.onCreate(savedInstanceState);

        Log.d("TAG", "on create called here");






        checkForLocationPermission();
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);


        shouldTilt = sharedPref.getBoolean(getResources().getString(R.string.tilt_enabled), true);


        setContentView(R.layout.activity_single_conversation);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

        mBroadcastReceiver = new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
              Log.d("TAG", "broadcast received");
              if (intent.getAction().equals(UPDATE_CONVERSATION_ACTION)) {
                  updateMessages();
              } else if (intent.getAction().equals(ActivityRecognitionConstants.STRING_ACTION)) {

                  ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                  DetectedActivity mostProbableActivity = result.getMostProbableActivity();
                  if (mostProbableActivity.getType()==DetectedActivity.ON_FOOT)
                  {
                      DetectedActivity betterActivity = walkingOrRunning(result.getProbableActivities());
                      if (betterActivity!=null)
                      {
                          mostProbableActivity=betterActivity;
                      }
                  }

                  Log.d("TAG", "activity type is: " + mostProbableActivity.getType() + " confidence: " + mostProbableActivity.getConfidence() + "shouldTilt: " + shouldTilt);

                  int minConfidence = Integer.parseInt(sharedPref.getString(getResources().getString(R.string.activity_min_confidence), ""+50));


                  if (mostProbableActivity.getConfidence() > minConfidence && mostProbableActivity.getType() != DetectedActivity.UNKNOWN && !(!shouldTilt && mostProbableActivity.getType() == DetectedActivity.TILTING)) {

                      lastActivity = mostProbableActivity.getType();
                      lastDetectedActivityList = result.getProbableActivities();
                      lastAcitivtyValueSaved=new Date();
// Get the type of activity
                  }
              }

          }

          ;

      };
        //senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

       // senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Intent intent = getIntent();
        if (intent.hasExtra("username")) {
            otherUserName = intent.getStringExtra("username");
            otherUserDisplayName = intent.getStringExtra("displayName");

        }
        else {
            otherUserName=intent.getStringExtra("fromUser");
            otherUserDisplayName=intent.getStringExtra("displayName");

        }
        String currentUserName="";

        final User currentUser= UserSingleton.getInstance().getCurrentUser(this);

        if (currentUser!=null)
        {
            currentUserName=currentUser.getUsername();
        }

        chatDescription=currentUserName+otherUserName;
       // lastAcitivtyValueSaved=getDateFromSharedPrefs("activity"+chatDescription);
       // lastLocationValueSaved=getDateFromSharedPrefs("location"+chatDescription);


        myList = (ListView) findViewById(R.id.listView);
        myList.setDividerHeight(0);
        myList.setAdapter(new ConversationListAdapter(this, R.layout.contact_list_item, currentUserName, otherUserName));

        ImageButton sendButton = (ImageButton) findViewById(R.id.send_button);
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

        int locationUpdatesInterval = Integer.parseInt(sharedPref.getString(getResources().getString(R.string.location_updates_interval), ""+LOCATION_REQUEST_INTERVAL));
        mLocationRequest.setInterval(locationUpdatesInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_ACCESS_PERMISSION_CONSTANT);
        }
        else {

            LocationHelper.getInstance().setAllowLocation(true);
        }
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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int locationValidityInMinutes= Integer.parseInt(sharedPref.getString(getString(R.string.max_minutes_last_location), ""+5));



        final Message message = new Message(finalCurrentUserName2, otherUserName, text);
        //wird immer gesetzt, ist im Zweifel einfach -1




        int currentLastActivity=lastActivity;



        message.setActivityValue(currentLastActivity);
        message.setDetectedActivityList(lastDetectedActivityList);

        final List<ApiRequest> requestList = new ArrayList<>();

        Location location=null;

        if (lastLocation!=null)
        {
            location=lastLocation;
        }
        else if (LocationHelper.getInstance().isAllowLocation() && mGoogleApiClient.isConnected()){
            try {
                location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                lastLocationValueSaved=new Date();

            }
            catch (SecurityException e)
            {
                //
                Log.e("singleconversation", "could not retrieve location");
            }
        }
        GeoLocation geoLocation=null;

        if (location!=null && isStillValid(location.getTime(), locationValidityInMinutes, TimeUnit.MINUTES))
        {
           geoLocation=new GeoLocation(location.getLatitude(), location.getLongitude());
            message.setSenderLocation(geoLocation);
        }
        if (MediaPlayingSingleton.getInstance().isPlaying(this) && MediaPlayingSingleton.getInstance().getCurrentSong(this) != null){
            message.setSong(MediaPlayingSingleton.getInstance().getCurrentSong(this));
        }

        if (geoLocation!=null){
            ApiRequest request = new ApiRequest<WeatherJSON.Weather>(WeatherJSON.Weather.class);
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
        if (MediaPlayingSingleton.getInstance().isPlaying(this) && MediaPlayingSingleton.getInstance().getCurrentSong(this) != null) {
            ApiRequest spotifyIdRequest = new ApiRequest<Song>(Song.class);
            SpotifyServiceSingleton.getInstance().setRequest(spotifyIdRequest);
            requestList.add(spotifyIdRequest);
            final Song song = MediaPlayingSingleton.getInstance().getCurrentSong(this);
            if (song.getSpotifyID() == null) {
                SpotifyServiceSingleton.getInstance().getSpotifyIdForSongData(song.getArtist(), song.getSongname(), new SpotifyTrackCallback() {
                    @Override
                    public void trackFetched(Track track) {
                        if (track != null) {
                            message.getSong().setSpotifyID("spotify:track:"+track.id);
                            message.getSong().setSpotifyImageURL(track.album.images.get(0).url);
                        }
                        //should proceed anyway....
                        SpotifyServiceSingleton.getInstance().getRequest().setHasFetched(true);
                        checkIfFetchedAllAndSendMessage(message, requestList);
                    }
                });
            } else {

                SpotifyServiceSingleton.getInstance().getPhotoPathForTrack(song.getSpotifyID(), new SpotifyPhotoCallback() {
                    @Override
                    public void photoFetched(String photo) {
                        message.getSong().setSpotifyImageURL(photo);

                        SpotifyServiceSingleton.getInstance().getRequest().setHasFetched(true);
                        checkIfFetchedAllAndSendMessage(message, requestList);
                    }
                });
            }
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


                Log.d("TAG", "sending failed" + t.getMessage() + t.getCause());
            }


        });
    }

    public boolean isStillValid(long lastTime, int threshold, TimeUnit timeUnit)
    {


        Date date = new Date();

        long diff = date.getTime()-lastTime;
        long minutes = timeUnit.convert(diff, TimeUnit.MILLISECONDS);

        Log.d("TAG", "minutes dist " + minutes + " last time: " + lastTime + " threshold: " + threshold);

        return minutes < threshold;

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
        Application.setCurrentUsername(null);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        if (mGoogleApiClient.isConnected()) {
            removeActivityUpdates();
            removeLocationUpdates();

        }
        //senSensorManager.unregisterListener(this);

        LoggingHelper.getInstance().logEvent(LoggingHelper.CLOSE_MESSAGE_EVENT, UserSingleton.getInstance().getCurrentUser(this).getUsername());


        activityOnExitDate =lastAcitivtyValueSaved;
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
        Application.setCurrentUsername(otherUserName);


        LoggingHelper.getInstance().logEvent(LoggingHelper.OPEN_MESSAGE_EVENT, UserSingleton.getInstance().getCurrentUser(this).getUsername());
        IntentFilter filter=new IntentFilter(UPDATE_CONVERSATION_ACTION);
        filter.addAction(ActivityRecognitionConstants.STRING_ACTION);
        //filter.addAction(ACTIVITY_UPDATE_FILTTEr);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    if (mGoogleApiClient.isConnected()) {
        requestActivityUpdates();
        requestLocationUpdates();

    }
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        lastActivityValidity=Integer.parseInt(sharedPref.getString(getResources().getString(R.string.max_seconds_last_activity), 10+""));


        //wenn wir rausgegangen sind aus der Activity und die letzte Activity noch gesetzt ist und sie nicht mehr gültig ist....
        if (activityOnExitDate!=null && lastActivity!=-1 && !isStillValid(activityOnExitDate.getTime(), lastActivityValidity, TimeUnit.SECONDS)) {
            lastActivity = -1;
            Log.d("SingleConversation", "activity not valid anymore: "+(new Date().getTime()-lastAcitivtyValueSaved.getTime()));
        }
        else if (activityOnExitDate!=null && lastActivity!=-1)
        {

            Log.d("SingleConversation", "old activity is still valid");
        }
        else
        {
            Log.d("SingleConversation", "no old activity");
        }
        activityOnExitDate =null;

      //  senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);


        Log.d("SingleConversation", "on resume single");
    }

    private void requestLocationUpdates() {
        if (LocationHelper.getInstance().isAllowLocation()) {

            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            catch (SecurityException e)
            {

                Log.e("TAG", "Security Exception");
            }
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

                    } else {

                      LocationHelper.getInstance().setAllowLocation(false);
                    }
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

//if (lastAcitivtyValueSaved!=null)        writeDateToSharedPrefs("activity"+chatDescription, lastAcitivtyValueSaved);

       // if (lastLocationValueSaved!=null) writeDateToSharedPrefs("location"+chatDescription, lastLocationValueSaved);
    }




    public void requestActivityUpdates() {
        if (!mGoogleApiClient.isConnected()) {
            //Toast.makeText(this, "GoogleApiClient not yet connected", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            int activityUpdateFrequency = Integer.parseInt(sharedPref.getString(getResources().getString(R.string.activity_updates_interval), ""+ACTIVITY_UPDATE_FREQUENCY));
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, activityUpdateFrequency, getActivityDetectionPendingIntent()).setResultCallback(this);
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
        lastLocationValueSaved=new Date();
        Log.d("TAG", "new location: " + location.getLatitude() + "  " + location.getLongitude());
    }


    private DetectedActivity walkingOrRunning(List<DetectedActivity> probableActivities) {
        DetectedActivity myActivity = null;
        int confidence = 0;
        for (DetectedActivity activity : probableActivities) {
            if (activity.getType() != DetectedActivity.RUNNING && activity.getType() != DetectedActivity.WALKING)
                continue;

            if (activity.getConfidence() > confidence)
                myActivity = activity;
        }

        return myActivity;
    }
}
