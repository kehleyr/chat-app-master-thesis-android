package com.app.charlotte.myapplication.gcmpush;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.app.charlotte.myapplication.Application;
import com.app.charlotte.myapplication.R;
import com.app.charlotte.myapplication.chat.SingleConversationActivity;
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

        String fromUser=data.getString("fromUser");

        Log.d("TAG", "received push "+data);

        if (Application.isInSingleConversationActivity() && fromUser.equals(Application.getCurrentUsername()))
        {
            Intent i = new Intent("updateConversation");
            i.putExtra("data", data);
            mBroadcaster.sendBroadcast(i);
        }
        else {

            startMessageView(data);
        }
    }


public void startMessageView(Bundle data)
{

    android.support.v4.app.NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(getBaseContext())
                    .setSmallIcon(R.drawable.speech_bubble)
                    .setContentTitle(data.getBundle("notification").getString("title"))
                    .setContentText(data.getBundle("notification").getString("body"));

String fromUser=data.getString("fromUser");
    String displayName=data.getString("displayName");
    Log.d("TAG", "fromUser = "+fromUser);

    Intent resultIntent = new Intent(this, SingleConversationActivity.class);

    resultIntent.putExtra("username", fromUser);
    resultIntent.putExtra("displayName", displayName);


    //TODO: get displayname...
    //  otherUserName = intent.getStringExtra("username");
   // otherUserDisplayName=intent.getStringExtra("displayName");

// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
    PendingIntent resultPendingIntent =
            PendingIntent.getActivity(
                    this,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );


    mBuilder.setContentIntent(resultPendingIntent);

    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
    mNotificationManager.notify(1234, mBuilder.build());
}


}
