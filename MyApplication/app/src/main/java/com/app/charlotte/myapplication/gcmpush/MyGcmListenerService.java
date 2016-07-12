package com.app.charlotte.myapplication.gcmpush;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.app.charlotte.myapplication.Application;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by charlotte on 08.05.16.
 */
public class MyGcmListenerService extends GcmListenerService {


    private LocalBroadcastManager mBroadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        mBroadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        Log.d("TAG", "received push "+data);

        if (Application.isInSingleConversationActivity())
        {
            Intent i = new Intent("updateConversation");
            i.putExtra("data", data);
            mBroadcaster.sendBroadcast(i);
        }





    }
}
