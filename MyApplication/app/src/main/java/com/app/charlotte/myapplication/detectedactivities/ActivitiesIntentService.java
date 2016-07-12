package com.app.charlotte.myapplication.detectedactivities;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by charlotte on 24.06.16.
 */
public class ActivitiesIntentService extends IntentService {

    private static final String TAG = "ActivitiesIntentService";

    public ActivitiesIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        for (DetectedActivity detectedActivity: result.getProbableActivities())
        {

            Log.d("ActivitiesIntentService", "activitiy is: "+detectedActivity+ " "+detectedActivity.getType()+ " confidence: "+detectedActivity.getConfidence());
        }

      //  ArrayList<DetectedActivity> list = new ArrayList(result.getProbableActivities());

      DetectedActivity detectedActivity =  result.getMostProbableActivity();
       // if (detectedActivity.getConfidence()>50&& detectedActivity.getType()!=DetectedActivity.UNKNOWN && !()) {
           // Intent i = new Intent(ActivityRecognitionConstants.STRING_ACTION);
           // i.putExtra(ActivityRecognitionConstants.STRING_EXTRA, detectedActivity.getType());
           // i.putParcelableArrayListExtra(ActivityRecognitionConstants.LIST_EXTRA,list);
        intent.setAction(ActivityRecognitionConstants.STRING_ACTION);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

       // }
    }
}
