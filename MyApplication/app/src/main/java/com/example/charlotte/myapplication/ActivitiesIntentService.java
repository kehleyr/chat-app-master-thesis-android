package com.example.charlotte.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

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
        Intent i = new Intent(ActivityRecognitionConstants.STRING_ACTION);

      DetectedActivity detectedActivity =  result.getMostProbableActivity();

        i.putExtra(ActivityRecognitionConstants.STRING_EXTRA, detectedActivity.getType());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }
}
