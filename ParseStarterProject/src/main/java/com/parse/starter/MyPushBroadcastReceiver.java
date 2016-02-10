package com.parse.starter;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HaoWu on 1/11/2016.
 */
public class MyPushBroadcastReceiver extends ParsePushBroadcastReceiver {

    public static final String PARSE_DATA_KEY = "com.parse.Data";
    private static final String REQUEST_TYPE = "REQUEST";
    private static final String MESSAGE_TYPE = "MESSAGE";

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        // deactivate standard notification
        return null;
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        // I am only using the default open behaviour at this moment
        super.onPushOpen(context, intent);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        JSONObject data = getDataFromIntent(intent);
        // Do something with the data. To create a notification do:
        if (data == null) {
            Log.d("GET PUSH DATA", "FAILED");
        }

        String type = null;
        String description = null;

        try {
            type = data.getString("TYPE");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Push Receive Exception", "failed to retrieve type");
            return;
        }

        if (type.equals(REQUEST_TYPE)){
            /*RequestObject requestObject = new RequestObject(data);
            description = requestObject.getNote();*/
            try {
                description = data.getString("note");
            } catch (JSONException e) {
                e.printStackTrace();
                description = "EMPTY";
            }
            Intent i = new Intent();
            i.putExtra("CONTENT", data.toString());
            i.setAction("com.parse.favourama.HANDLE_FAVOURAMA_REQUESTS");
            context.sendBroadcast(i);
        } else if (type.equals(MESSAGE_TYPE)){

            /* HAO to JEREMY
            *Everytime you receive a message, you write to the message file and notify there has been changes made to the files
            * Make subclass MessageObject and make a constructor and constructs from JSONObject
            *
            * MessageObject messageObject = new MessageObject(data);
            * description = messageObject.getNote();
            * */
            Intent i = new Intent();
            i.putExtra("CONTENT", data.toString());
            i.setAction("com.parse.favourama.HANDLE_FAVOURAMA_MESSAGES");
            context.sendBroadcast(i);
        }

        if ( !StarterApplication.isActivityVisible() ) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("New" + type);
            builder.setContentText(description);
            builder.setSmallIcon(R.drawable.logo);
            builder.setAutoCancel(true);
            notificationManager.notify("MyTag", 0, builder.build());
        }
        // OPTIONAL create soundUri and set sound:
        /*builder.setSound(soundUri);*/

    }

    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
        } catch (JSONException e) {
            // Json was not readable...
            Log.d("PUSH RECEIVE FAILURE", ">>>COULD NOT PROCESS JSON DATA");
        }
        return data;
    }
}