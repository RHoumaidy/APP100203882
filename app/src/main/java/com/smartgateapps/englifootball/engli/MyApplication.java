package com.smartgateapps.englifootball.engli;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.Legue;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Raafat on 04/11/2015.
 */
public class MyApplication extends Application {

    public static AlarmManager alarmManager;
    public static NotificationManager notificationManager;
    public static SharedPreferences pref;
    public static DbHelper dbHelper;
    public static SQLiteDatabase dbw, dbr;

    public static int pageSize = 15;
    public static Typeface font;

    public static final String BASE_URL = "http://m.kooora.com/";
    public static final String ENGLI_EXT_HOME = "?n=0&o=ncen&pg=";
    public static final String PRIMIUM_LEAGUE_EXT = "?c=7666";
    public static final String EXPERTS_CUP_EXT = "?c=7633";
    public static final String UNION_CUP_EXT = "?c=9720";
    public static final String PRIMIUM_LEAGUE_NEWS_EXT = "?n=0&o=n7666&pg=";
    public static final String EXPERTS_CUP_NEWS_EXT = "?n=0&o=n7633&pg=";
    public static final String UNION_CUP_NEWS_EXT = "?n=0&o=n9720&pg=";
    public static final String TEAM_NEWS_EXT = "?n=0&o=n1000000";
    public static final String TEAM_MATCHES_EXT = "?region=-6&team=";

    public static final String TEAMS_CM = "&cm=t";
    public static final String POSES_CM = "&cm=i";
    public static final String MATCHES_CM = "&cm=m";
    public static final String SCORERS_CM = "&scorers=true";

    public static Context APP_CTX;
    public static final String LIVE_CAST_APP_PACKAGE_NAME = "com.smartgateapps.livesports";

    public static Picasso picasso;
    public static WebView webView;

    public static final int HEADER_TYPE_GOALERS = 0;

    public static String[] PLAYERS_POS = new String[]{"", "مدرب", "حارس", "دفاع", "وسط", "هجوم", "مساعد مدرب", " مدرب حراس", "مدرب بدني", "طبيب الفريق"};

    public static HashMap<String, Integer> monthOfTheYear = new HashMap<>(12);
    public static MyApplication instance;
    public static HashMap<Integer, Integer> teamsLogos = new HashMap<>();


    public static SimpleDateFormat sourceTimeFormate = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat destTimeFormate = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat sourceDateFormat = new SimpleDateFormat("E d MMMM yyy", new Locale("ar"));
    public static SimpleDateFormat destDateFormat = new SimpleDateFormat("E d MMMM yyy", new Locale("ar"));

    public static TimeZone currentTimeZone;

    public static InterstitialAd mInterstitialAd;


    public static Long parseDateTime(String date, String time) {

        Long dateL = 0L;
        Long timeL = MyApplication.getCurretnDateTime();
        try {
            dateL = sourceDateFormat.parse(date).getTime();
            timeL = sourceTimeFormate.parse(time).getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateL + timeL - getCurrentOffset();
    }

    public static String[] formatDateTime(Long dateTime) {
        dateTime += getCurrentOffset();
        String date = sourceDateFormat.format(dateTime);
        String time = sourceTimeFormate.format(dateTime);

        return new String[]{date, time};
    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();

        currentTimeZone = TimeZone.getDefault();
        sourceTimeFormate.setTimeZone(TimeZone.getTimeZone("UTC"));
        destTimeFormate.setTimeZone(currentTimeZone);
        sourceDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        destDateFormat.setTimeZone(currentTimeZone);

        teamsLogos.put(1, R.mipmap.t1);
        teamsLogos.put(11,R.mipmap.t11);
        teamsLogos.put(12,R.mipmap.t12);
        teamsLogos.put(13,R.mipmap.t13);
        teamsLogos.put(15,R.mipmap.t15);
        teamsLogos.put(16,R.mipmap.t16);
        teamsLogos.put(17,R.mipmap.t17);
        teamsLogos.put(18,R.mipmap.t18);
        teamsLogos.put(19,R.mipmap.t19);
        teamsLogos.put(2,R.mipmap.t2);
        teamsLogos.put(20,R.mipmap.t20);
        teamsLogos.put(600,R.mipmap.t600);
        teamsLogos.put(602,R.mipmap.t602);
        teamsLogos.put(607,R.mipmap.t607);
        teamsLogos.put(609,R.mipmap.t609);
        teamsLogos.put(7,R.mipmap.t7);
        teamsLogos.put(795,R.mipmap.t795);
        teamsLogos.put(796,R.mipmap.t796);
        teamsLogos.put(8,R.mipmap.t8);
        teamsLogos.put(850,R.mipmap.t850);

        APP_CTX = getApplicationContext();
        font = Typeface.createFromAsset(APP_CTX.getAssets(), "fonts/jf_flat_regular.ttf");
        dbHelper = new DbHelper(APP_CTX);
        dbw = dbHelper.getWritableDatabase();
        dbr = dbHelper.getReadableDatabase();

        picasso = Picasso.with(this);

        Legue engli = new Legue(0L, "الاخبار البريطانية", "?y=en", ENGLI_EXT_HOME);
        Legue primiumLeague = new Legue(1L, "الدوري الإنجليزي الممتاز", PRIMIUM_LEAGUE_EXT, PRIMIUM_LEAGUE_NEWS_EXT);
        Legue expertsCup = new Legue(2L, "كأس رابطة المحترفين الإنجليزية", EXPERTS_CUP_EXT, EXPERTS_CUP_NEWS_EXT);
        Legue unionCup = new Legue(3L, "كأس الإتحاد الإنجليزي", UNION_CUP_EXT, UNION_CUP_NEWS_EXT);

        engli.save();
        primiumLeague.save();
        expertsCup.save();
        unionCup.save();

        pref = PreferenceManager.getDefaultSharedPreferences(MyApplication.APP_CTX);
        boolean b = pref.getBoolean(getString(R.string.premium_league_notificatin_pref_key), true);
        pref.edit().putBoolean(getString(R.string.premium_league_notificatin_pref_key), b).apply();
        notificationManager = (NotificationManager) APP_CTX.getSystemService(NOTIFICATION_SERVICE);

        alarmManager = (AlarmManager) APP_CTX.getSystemService(ALARM_SERVICE);


        monthOfTheYear.put("يناير", 1);
        monthOfTheYear.put("فبراير", 2);
        monthOfTheYear.put("مارس", 3);
        monthOfTheYear.put("أبريل", 4);
        monthOfTheYear.put("مايو", 5);
        monthOfTheYear.put("يونيو", 6);
        monthOfTheYear.put("يوليو", 7);
        monthOfTheYear.put("أغسطس", 8);
        monthOfTheYear.put("سبتمبر", 9);
        monthOfTheYear.put("أكتوبر", 10);
        monthOfTheYear.put("نوفمبر", 11);
        monthOfTheYear.put("ديسمبر", 12);

        //Parse.enableLocalDatastore(this);
        Parse.initialize(this, "rrBjKZPdNIhTyGfIgvlyf5MJuc6ARMOy54hUL6WD", "ia63gS8cA1yzmkWMoalipbrQwzO1aYCM33V4lEP7");
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    public static void openPlayStor(String appPackageName) {
        try {
            APP_CTX.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(appPackageName)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (android.content.ActivityNotFoundException anfe) {
            APP_CTX.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public static void changeTabsFont(TabLayout tabLayout) {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(MyApplication.font);
                }
            }
        }
    }

    public static Long getCurretnDateTime() {
        Calendar rightNow = Calendar.getInstance();
        return (rightNow.getTimeInMillis());
    }

    public static Long getCurrentOffset() {
        Calendar rightNow = Calendar.getInstance();
        long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                rightNow.get(Calendar.DST_OFFSET);

        return offset;
    }


}
