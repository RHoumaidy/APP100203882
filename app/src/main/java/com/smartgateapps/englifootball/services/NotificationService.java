package com.smartgateapps.englifootball.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;

import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.activities.MainActivity;
import com.smartgateapps.englifootball.engli.MyApplication;
import com.smartgateapps.englifootball.model.LeaguNews;
import com.smartgateapps.englifootball.model.News;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Raafat on 13/09/2015.
 */
public class NotificationService extends IntentService {

    public NotificationService() {
        super("AlarmService");
    }

    HashMap<Integer, List<News>> unSeenNewsMap = new HashMap<>();

    @Override
    protected void onHandleIntent(Intent intent) {

        Set<String> selectedLeagues =
                MyApplication.pref.getStringSet(MyApplication.APP_CTX.getString(R.string.selected_leagues_pref_key),
                        new HashSet<String>());

        for (String st : selectedLeagues) {
            int id = Integer.valueOf(st);
            List<News> unSeenNews = LeaguNews.getAllUseenNewsForLeagu(id);
            if (unSeenNews.size() > 0)
                unSeenNewsMap.put(id, unSeenNews);
        }

        if (unSeenNewsMap.size() > 0)
            sentNotification();

    }

    private void sentNotification() {

        int listSize = 0;
        Integer[] keys = unSeenNewsMap.keySet().toArray(new Integer[]{});
        List<News> allUnSeenNewsList = new ArrayList<>();
        for (int i = 0; i < unSeenNewsMap.size(); ++i) {
            allUnSeenNewsList.addAll(unSeenNewsMap.get(Integer.valueOf(keys[i])));
        }

        listSize = allUnSeenNewsList.size();

        String[] lines = new String[5];
        for (int i = 0; i < lines.length && i < listSize; ++i) {
            lines[i] = allUnSeenNewsList.get(i).getTitle();
        }

        String contentTitle = "يوجد " + listSize + " من الاخبار الجديدة ";
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        String summaryText = (listSize > 5) ? "+" + (listSize - 5) + "more" : "";
        summaryText += "   يمكن ايقاف الاشعارات من اعدادات التطبيق ..";
        Notification.Style style = new Notification.InboxStyle()
                .addLine(lines[0])
                .addLine(lines[1])
                .addLine(lines[2])
                .addLine(lines[3])
                .addLine(lines[4])
                .setSummaryText(summaryText);

        Notification builder = new Notification.Builder(this)
                .setContentTitle(contentTitle)
                .setSmallIcon(R.mipmap.ic_notification)
                .setLargeIcon(icon)
                .setStyle(style)
                .setContentIntent(PendingIntent.getActivity(MyApplication.APP_CTX, 2, new Intent(MyApplication.APP_CTX, MainActivity.class), 0))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[]{0, 100, 500, 1000})
                .build();

        int id = 77;

        MyApplication.notificationManager.notify(id, builder);

    }
}
