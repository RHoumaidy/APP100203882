package com.smartgateapps.englifootball.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;
import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.activities.MainActivity;
import com.smartgateapps.englifootball.engli.MyApplication;

import org.json.JSONObject;

/**
 * Created by kratos on 14/05/2015.
 */
public class NotificationHandler extends ParsePushBroadcastReceiver {

    public static int numMessages = 0;
    public static int notificationId = 16;

    private void generateNotification(final Context context, final String type, final String content, final String title) {


        Intent viewAction = new Intent(context, MainActivity.class);
        if (type.equalsIgnoreCase("url")) {
            viewAction = new Intent(Intent.ACTION_VIEW);
            viewAction.setData(Uri.parse(content));
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 66, viewAction, 0);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .addAction(android.R.drawable.ic_menu_view, "عرض", pendingIntent)
                .addAction(android.R.drawable.ic_delete, "الغاء", CancleNotification.getDismissIntent(notificationId, context));


        MyApplication.notificationManager.notify(notificationId, mBuilder.build());

    }


    @Override
    protected void onPushReceive(Context context, Intent intent) {
//        super.onPushReceive(context,intent);
        try {
            String intnetJson = intent.getStringExtra("com.parse.Data");
            JSONObject jsonObject = new JSONObject(intnetJson);

            String type = jsonObject.getString("type");
            String content = jsonObject.getString("content");
            String title = jsonObject.getString("title");

            generateNotification(context, type, content, title);


        } catch (Exception e) {
            Log.e("NotificationError", e.getMessage());
        }
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
        MyApplication.notificationManager.cancel(notificationId);
    }
}
