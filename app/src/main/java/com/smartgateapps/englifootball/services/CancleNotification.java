package com.smartgateapps.englifootball.services;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.smartgateapps.englifootball.engli.MyApplication;


/**
 * Created by Raafat on 01/12/2015.
 */
public class CancleNotification extends Activity {

    public static final String NOTIFICATION_ID = "NOTIFICATOIN_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MyApplication.notificationManager.cancel(getIntent().getIntExtra(NOTIFICATION_ID, -1));
        finish();
    }

    public static PendingIntent getDismissIntent(int notificationId,Context ctx){
        Intent intent = new Intent(ctx,CancleNotification.class);
        intent.putExtra(NOTIFICATION_ID,notificationId);
        PendingIntent res = PendingIntent.getActivity(ctx, 43, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return res;
    }
}
