package com.smartgateapps.englifootball.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.parse.ParseAnalytics;
import com.smartgateapps.englifootball.Adapter.ViewPagerAdapter;
import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.Team;
import com.smartgateapps.englifootball.engli.MyApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

public class TeamDetailsActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private ViewPager viewPager;
    private Fragment teamNewsFragment;
    private Fragment teamPlayersFragment;
    private Fragment teamTransFragment;
    private Fragment teamMatchFragment;

    private AdView mAdView;
    private ImageView backdrop;
    private CoordinatorLayout coordinatorLayout;

    private String teamUrl;
    private Team curTeam;

    private WebView webView;

    private void initParallaxValues() {
        CollapsingToolbarLayout.LayoutParams petDetailsLp =
                (CollapsingToolbarLayout.LayoutParams) backdrop.getLayoutParams();


        petDetailsLp.setParallaxMultiplier(0.4f);

        backdrop.setLayoutParams(petDetailsLp);
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
        setContentView(R.layout.activity_team_details);
        mAdView = (AdView) findViewById(R.id.adView);
        webView = new WebView(MyApplication.APP_CTX);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
        backdrop = (ImageView) findViewById(R.id.backdrop);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

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
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        int teamId = getIntent().getIntExtra("TEAM_ID", 0);
//        int  teamId = 144;
        curTeam = Team.load(Long.valueOf(teamId),null);
        teamUrl = curTeam.getTeamUrl();

        Map<String, String> dimensions = new HashMap<>();
        dimensions.put("category", "استعراض فريق");
        ParseAnalytics.trackEventInBackground("open_team", dimensions);
        dimensions.put("category", curTeam.getTeamName());
        ParseAnalytics.trackEventInBackground("open_team", dimensions);

        teamNewsFragment = new NewsListFragment();
        teamPlayersFragment = new PlayersFragment();
        teamTransFragment = new TeamTransFragment();
        teamMatchFragment = new MatchMatchFragment();

        Bundle args1 = new Bundle();
        String tmp = MyApplication.TEAM_NEWS_EXT;

        args1.putString("URL_EXT", tmp.subSequence(0, tmp.length() - (teamId + "").length()).toString() + curTeam.getId() + "&pg=");
        args1.putInt("RES", R.layout.news_card_layout_2);
        args1.putInt("LEAGUE_ID", teamId);
        args1.putBoolean("IS_LEAGUE", false);
        teamNewsFragment.setArguments(args1);

        Bundle args2 = new Bundle();
        args2.putString("URL_EXT", "?team=" + curTeam.getId() + "&mode=p");
        teamPlayersFragment.setArguments(args2);

        Bundle args3 = new Bundle();
        args3.putInt("URL_EXT", (teamId));
        teamTransFragment.setArguments(args3);

        Bundle args4 = new Bundle();
        args4.putString("URL_EXT", MyApplication.TEAM_MATCHES_EXT + teamId);
        teamMatchFragment.setArguments(args4);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(teamNewsFragment, "اخبار");
        adapter.addFrag(teamPlayersFragment, "تشكيلة");
        adapter.addFrag(teamTransFragment, "إنتقالات");
        adapter.addFrag(teamMatchFragment, "مباريات");

        viewPager.setAdapter(adapter);
        collapsingToolbar.setTitle(curTeam.getTeamName());

        tabLayout.setupWithViewPager(viewPager);
        initParallaxValues();
        checkInternet();
        MyApplication.changeTabsFont(tabLayout);

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
        } else {
            featchData();
        }
    }

    private void featchData() {

        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });

        //String url = MyApplication.BASE_URL+MyApplication.ABD_ALATIF_EXT;
        webView.stopLoading();
        webView.loadUrl(teamUrl);
    }

    class MyJavaScriptInterface {


        @JavascriptInterface
        @SuppressWarnings("unused")
        public void showHTML(final String html) {
            TeamDetailsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String htm = html;
                    Document doc = Jsoup.parse(html);

//                   Element koooraPage = doc.getElementById("koooraPage");
//                   Element mainContent = koooraPage.getElementById("mainContent");
                    Element content = doc.getElementById("content");
                    Element mb20 = content.getElementsByClass("mb20").first();
                    Element teamImage_mt20 = mb20.getElementsByTag("img").first();
                    try {
                        MyApplication.picasso
                                .load(teamImage_mt20.attr("src"))
                                .fit()
                                .into(backdrop);
                        backdrop.setScaleType(ImageView.ScaleType.FIT_XY);
                    } catch (Exception e) {
                        Toast.makeText(MyApplication.APP_CTX, R.string.toast_featch_data_error, Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }
}
