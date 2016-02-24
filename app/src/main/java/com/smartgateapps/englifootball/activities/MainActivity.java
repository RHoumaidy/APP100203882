package com.smartgateapps.englifootball.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.parse.ParseAnalytics;
import com.smartgateapps.englifootball.Adapter.CustomTypefaceSpan;
import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.engli.MyApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ActionBar actionBar;
    public Toolbar toolbar;
    public DrawerLayout drawer;
    private CoordinatorLayout coordinatorLayout;

    public LinearLayout matchFilterLL;
    private int prevSelectedId = R.id.homeItemId;

    private android.support.v4.app.FragmentManager supportManager;
    private FragmentManager manager;
    private Fragment newsListFragment;
    private Fragment premiumLeagueFragment;
    private Fragment expertsCupFragment;
    private Fragment unionCupFragment;
    private Fragment aboutfraFragment;
    private Fragment contactUsFragment;
    private PreferenceFragment prefFragment;

    private AdView mAdView;
    private Fragment prevFragment = null;
    private PreferenceFragment prevFragment1 = null;
    private NavigationView navigationView;


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        matchFilterLL = (LinearLayout) findViewById(R.id.choseMathcFilterLL);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        mAdView = (AdView) findViewById(R.id.adView);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestNewInterstitial();
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionBar.setTitle(getString(R.string.news_menu_item));

        supportManager = getSupportFragmentManager();
        manager = getFragmentManager();

        newsListFragment = new NewsListFragment();
        premiumLeagueFragment = new PremiumLeagueFragment();
        expertsCupFragment = new ExpertsCupFragment();
        unionCupFragment = new UnionCupFragment();
        contactUsFragment = new SendEmailFragment();
        aboutfraFragment = new AboutFragment();
        prefFragment = new PrefFragment();

        Bundle args = new Bundle();
        args.putString("URL_EXT", MyApplication.ENGLI_EXT_HOME);
        args.putInt("RES", R.layout.news_card_layout);
        args.putInt("LEAGUE_ID", 0);
        newsListFragment.setArguments(args);

        supportManager.beginTransaction()
                .replace(R.id.fragmentContainer, newsListFragment)
                .commit();
        prevFragment = newsListFragment;
        navigationView.getMenu().getItem(0).setChecked(true);

        Menu menu = navigationView.getMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

    }

    private void applyFontToMenuItem(MenuItem mi) {

        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", MyApplication.font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public void checkInternet() {
        if (!MyApplication.instance.isNetworkAvailable()) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "لا يوجد اتصال بالانترنت", Snackbar.LENGTH_INDEFINITE)
                    .setAction("اعد المحاولة", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkInternet();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);

            snackbar.show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        android.support.v4.app.Fragment fragment = null;
        PreferenceFragment fragment1 = null;

        toolbar.setTitle(item.getTitle());

        if (id == prevSelectedId) {
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }

        switch (id) {
            case R.id.homeItemId:
                fragment = newsListFragment;
                break;

            case R.id.premiumLegueItemId:
                fragment = premiumLeagueFragment;
                if (MyApplication.mInterstitialAd.isLoaded())
                    MyApplication.mInterstitialAd.show();
                break;
            case R.id.expertsCupItemId:
                fragment = expertsCupFragment;
                break;
            case R.id.unionCupItemId:
                fragment = unionCupFragment;
                break;
            case R.id.settingItemId:
                fragment1 = prefFragment;
                break;
            case R.id.contactUsItemId:
                fragment = contactUsFragment;
                break;
            case R.id.aboutItmeId:
                fragment = aboutfraFragment;
                break;
            case R.id.liveCastItemId:

                Map<String, String> dimensions = new HashMap<>();
                dimensions.put("category", "البث المباشر");
                ParseAnalytics.trackEventInBackground("open_app", dimensions);

                navigationView.getMenu().findItem(prevSelectedId).setChecked(true);
                MenuItem prevItem = navigationView.getMenu().findItem(prevSelectedId);
                toolbar.setTitle(prevItem.getTitle());

                openNewApplication(MyApplication.LIVE_CAST_APP_PACKAGE_NAME);
                drawer.closeDrawer(GravityCompat.START);
                return true;

        }
        prevSelectedId = id;

        if (fragment != null) {
            supportManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            prevFragment = fragment;
            if (prevFragment1 != null)
                manager.beginTransaction()
                        .remove(prevFragment1)
                        .commit();
        }

        if (fragment1 != null) {
            prevFragment1 = fragment1;
            if (prevFragment != null)
                supportManager.beginTransaction()
                        .remove(prevFragment)
                        .commit();
            manager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment1)
                    .commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean backPressed = false;

    @Override
    public void onBackPressed() {

        if (!backPressed) {
            backPressed = true;
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    backPressed = false;
                }
            }, 3500);
            Toast.makeText(this, ("اضغط مرة اخرى للاغلاق"), Toast.LENGTH_LONG).show();
        } else {
            if (MyApplication.mInterstitialAd.isLoaded())
                MyApplication.mInterstitialAd.show();
            super.onBackPressed();
        }
    }

    public boolean isPackageInstalled(String packagename) {
        PackageManager pm = this.getPackageManager();
        try {
            pm.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public void openNewApplication(final String appPackageName) {
        if (isPackageInstalled(appPackageName)) {
            Intent toLiveSport = this.getPackageManager().getLaunchIntentForPackage(appPackageName);
            this.startActivity(toLiveSport);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("لم يتم العثور على تطبيق ")
                    .setMessage("لا يوجد تطبيق البث المباشر مثبت على جهازك \n هل تريد تثبيت التطبيق ؟")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyApplication.openPlayStor("http://makanisyria.com/assets/downloads/LiveSport-1.0.apk   ");
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();

        }
    }
}
