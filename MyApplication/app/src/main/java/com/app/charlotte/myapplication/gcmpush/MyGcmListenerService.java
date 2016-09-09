package com.app.charlotte.myapplication.gcmpush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.app.charlotte.myapplication.Application;
import com.app.charlotte.myapplication.MainActivity;
import com.app.charlotte.myapplication.R;
import com.app.charlotte.myapplication.chat.SingleConversationActivity;
import com.google.android.gms.gcm.GcmListenerService;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

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

        if (Application.isInSingleConversationActivity() && fromUser!=null && fromUser.equals(Application.getCurrentUsername()))
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

String messageText = (data.getString("messageText")!=null?data.getString("messageText"):null);
    String displayName=(data.getString("displayName")!=null?data.getString("displayName"):null);

    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    String password= sharedPref.getString(getBaseContext().getString(R.string.group_pass), "");
    Log.d("TAG", "password ="+password);

    if (!password.equals("")&& messageText!=null && !messageText.equals("")) {
        try {
            messageText = AESCrypt.decrypt(password, messageText);
        } catch (GeneralSecurityException e) {
            //handle error - could be due to incorrect password or tampered encryptedMsg
        }
        catch(IllegalArgumentException e)
        {
            Log.e("TAG", "Illegal argument exception");
        }
        catch (Exception e)
        {
            Log.e("TAG", e.getLocalizedMessage());
        }
    }



    android.support.v4.app.NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(getBaseContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                  .setContentTitle("Neue Nachricht von "+displayName)
                   .setContentText(messageText);
    mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE);
mBuilder.setAutoCancel(true);

String fromUser=data.getString("fromUser");
    Log.d("TAG", "fromUser = "+fromUser);

    Intent resultIntent = new Intent(this, SingleConversationActivity.class);
    resultIntent.putExtra("username", fromUser);
    resultIntent.putExtra("displayName", displayName);
PendingIntent resultPendingIntent;
// Adds the back stack
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

        Log.d("TAG", "create back stack");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SingleConversationActivity.class);
        stackBuilder.addNextIntent(resultIntent);
      resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    else {

        resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

    }
// Adds the Intent to the top of the stack




    Log.d("TAG", "fromuser: "+fromUser+" displayname: "+displayName);

    //TODO: get displayname...
    //  otherUserName = intent.getStringExtra("username");
   // otherUserDisplayName=intent.getStringExtra("displayName");



    mBuilder.setContentIntent(resultPendingIntent);

    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
    mNotificationManager.notify(1234, mBuilder.build());
}


}
