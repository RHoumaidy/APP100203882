package com.smartgateapps.englifootball.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.parse.ParseAnalytics;
import com.smartgateapps.englifootball.Adapter.NewsRecyclerViewAdapter;
import com.smartgateapps.englifootball.Adapter.WrappingLinearLayoutMgr;
import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.LeaguNews;
import com.smartgateapps.englifootball.model.Legue;
import com.smartgateapps.englifootball.model.News;
import com.smartgateapps.englifootball.model.NewsNews;
import com.smartgateapps.englifootball.model.TeamNews;
import com.smartgateapps.englifootball.engli.MyApplication;
import com.squareup.picasso.NetworkPolicy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class NewsDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {


    private String url;
    private ImageView newsImge;
    private TextView newsToolBarTitleTxtV;
    private TextView newsTitleTxtV;
    private LinearLayout mTitleLayout;
    private TextView newsDetailTxtV;
    private AppBarLayout appBarLayout;
    private FrameLayout titleFrameLayout;
    private ProgressBar progressBar;
    private List<News> relatedNews;
    private RecyclerView recyclerView;
    private NewsRecyclerViewAdapter adapter;
    private TextView dateTxtView;
    private CoordinatorLayout coordinatorLayout;
    private AdView mAdView;

    private News news;
    private Long leaguId;
    boolean isLeague;

    Toolbar toolbar;
    FloatingActionButton fab;

    Timer timer = new Timer();

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.9f;
    private static final int ALPHA_ANIMATIONS_DURATION = 800;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private String[] waiting = new String[]{"يرجى الإنتظار ", "يرجى الإنتظار .", "يرجى الإنتظار ..", "يرجى الإنتظار ..."};
    private int idx = 0;

    private WebView webView;


    private void initParallaxValues() {
        CollapsingToolbarLayout.LayoutParams petDetailsLp =
                (CollapsingToolbarLayout.LayoutParams) newsImge.getLayoutParams();
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        if(behavior!=null) {
//            behavior.setTopAndBottomOffset(0);
//            behavior.onNestedPreScroll(coordinatorLayout, appBarLayout, null, 0, 1, new int[2]);
//        }
        CollapsingToolbarLayout.LayoutParams petBackgroundLp =
                (CollapsingToolbarLayout.LayoutParams) titleFrameLayout.getLayoutParams();

        petDetailsLp.setParallaxMultiplier(0.4f);
        petBackgroundLp.setParallaxMultiplier(0.2f);

        newsImge.setLayoutParams(petDetailsLp);
        titleFrameLayout.setLayoutParams(petBackgroundLp);
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    public static void startRotatAnimation(View v, boolean b) {
        if (!b) {
            v.setAnimation(null);
            v.setRotationX(0);
            v.setRotationY(0);
            return;
        }
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
        int centerX = layoutParams.leftMargin + (v.getWidth() / 2);
        int centerY = layoutParams.topMargin + (v.getHeight() / 2);
        RotateAnimation r = new RotateAnimation(0f, 360f, centerX, centerY); // HERE
        r.setStartOffset(0);
        r.setDuration(1000);
        r.setRepeatCount(Animation.INFINITE);
        r.setInterpolator(new LinearInterpolator());
        r.setFillAfter(true); //HERE
        v.setAnimation(r);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(toolbar, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(toolbar, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }


    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleLayout, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                startAlphaAnimation(fab, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleLayout, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                startAlphaAnimation(fab, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mAdView.loadAd(adRequest);
    }

    private Intent getShareNewsIntent(News news) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, MyApplication.BASE_URL + news.getUrl());
        return intent;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);


        webView = new WebView(MyApplication.APP_CTX);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        relatedNews = new ArrayList<>();
        adapter = new NewsRecyclerViewAdapter(relatedNews, R.layout.news_card_layout);

        newsImge = (ImageView) findViewById(R.id.newsDetailImV);
        newsToolBarTitleTxtV = (TextView) findViewById(R.id.toolBarTitle);
        newsTitleTxtV = (TextView) findViewById(R.id.newsDetailTitleTxtV);
        newsDetailTxtV = (TextView) findViewById(R.id.newDetailDetailTxtV);
        mTitleLayout = (LinearLayout) findViewById(R.id.titleLinearLayout);
        titleFrameLayout = (FrameLayout) findViewById(R.id.titleFrameLayout);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        recyclerView = (RecyclerView) findViewById(R.id.newsListRycV);
        dateTxtView = (TextView) findViewById(R.id.newsDetailDateTxtV);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        dateTxtView.setText("");
        TextView releatedNewsTitle = (TextView)findViewById(R.id.relatedNewsTitleTxtV);
        releatedNewsTitle.setTypeface(MyApplication.font);

        newsToolBarTitleTxtV.setTypeface(MyApplication.font);
        newsTitleTxtV.setTypeface(MyApplication.font);
        newsDetailTxtV.setTypeface(MyApplication.font);
        dateTxtView.setTypeface(MyApplication.font);

        //appBarLayout.offsetTopAndBottom(450);
        appBarLayout.addOnOffsetChangedListener(this);

//        recyclerView.addItemDecoration(new SpacesItemDecoration(15));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new WrappingLinearLayoutMgr(this, 2));
        recyclerView.setAdapter(adapter);

        startAlphaAnimation(toolbar, 0, View.INVISIBLE);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.mInterstitialAd.isLoaded())
                    MyApplication.mInterstitialAd.show();

                startActivity(Intent.createChooser(getShareNewsIntent(news), "مشاركة بواسطة .."));
            }
        });

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final GestureDetector mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mGestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    News news = relatedNews.get(pos);
                    Intent intent1 = new Intent(NewsDetailsActivity.this, NewsDetailsActivity.class);
                    intent1.putExtra("NEW", news);
                    intent1.putExtra("LEAGUE_ID", leaguId);
                    intent1.putExtra("IS_LEAGUE", isLeague);
                    NewsDetailsActivity.this.startActivity(intent1);
                    NewsDetailsActivity.this.finish();

                    return true;
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


        isLeague = getIntent().getBooleanExtra("IS_LEAGUE", true);
        leaguId = getIntent().getLongExtra("LEAGUE_ID", 0);

        news = (News) getIntent().getSerializableExtra("NEW");
        news = News.load(news.getId(), null, null);

        Map<String, String> dimensions = new HashMap<>();
        dimensions.put("category", Legue.load(leaguId).get(0).getName());
        dimensions.put("dayType", "weekday");
        ParseAnalytics.trackEventInBackground("read_news", dimensions);
        dimensions.put("category", "قراءة خبر ");
        ParseAnalytics.trackEventInBackground("read_news", dimensions);


        url = news.getUrl();
        MyApplication.picasso
                .load(news.getImgUrl())
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(newsImge);
        newsTitleTxtV.setText(news.getTitle());
        newsToolBarTitleTxtV.setText(news.getTitle());
        if (news.getContent() == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            newsDetailTxtV.setText(waiting[idx++]);
                            idx %= 4;
                        }
                    });
                }
            }, 0, 1000);
            checkInternet();

        } else {
            timer.cancel();
            newsDetailTxtV.setText(Html.fromHtml(news.getContent()));
            dateTxtView.setText(news.getDate());
            relatedNews.clear();
            relatedNews.addAll(NewsNews.getAllRelatedNewsForLeftNews(news.getId()));
            adapter.notifyDataSetChanged();
        }


        initParallaxValues();

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
        startRotatAnimation(fab, true);
        fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_popup_sync));
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:window.HtmlViewer.showHTML('<html>'+document.getElementById('content').innerHTML+'</html>');");
            }
        });

        //String url = MyApplication.BASE_URL+MyApplication.ABD_ALATIF_EXT
        webView.stopLoading();
        webView.loadUrl(MyApplication.BASE_URL + url);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(i) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void showHTML(final String html) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document doc = Jsoup.parse(html);
                        Element divDate = doc.getElementById("mv-pub-date");
                        Element articlePage = doc.getElementsByClass("articlePage").first();
                        Element articleBody = articlePage.getElementsByClass("articleBody").first();
                        String text = articleBody.toString();
                        String dateText = divDate.text();

                        Element newsList = articlePage.getElementsByClass("newsList").first();
                        Element ul = newsList.getElementsByTag("ul").first();
                        relatedNews.clear();

                        timer.cancel();


                        news.setContent(text);
                        news.setDate(dateText);
                        news.updateThis();

                        for (Element li : ul.getElementsByTag("li")) {

                            Element a = li.getElementsByTag("a").first();
                            Element img = a.getElementsByTag("img").first();
                            String imgUrl = img.attr("src");

                            Element div = li.getElementsByTag("div").first();
                            a = div.getElementsByTag("a").first();
                            Element strong = a.getElementsByTag("strong").first();
                            String title = strong.text();

                            NewsNews newsNews = new NewsNews(news.getId(), 0L);
                            News news = new News();
                            news.setUrl(a.attr("href"));
                            news.setImgUrl(imgUrl.substring(0, imgUrl.indexOf("&")));
                            news.setTitle(title);
                            news.save();
                            newsNews.setRightId(news.getId());
                            newsNews.save();
                            relatedNews.add(news);
                            if (isLeague) {
                                LeaguNews leaguNews = new LeaguNews();
                                leaguNews.setLeaguId(leaguId);
                                leaguNews.setNewsId(news.getId());
                                leaguNews.setPageIdx(3);
                                leaguNews.setIsSeen(true);
                                leaguNews.save();
                            } else {
                                TeamNews teamN = new TeamNews();
                                teamN.setTeamId(leaguId);
                                teamN.setNewsId(news.getId());
                                teamN.setPageIdx(3);
                                teamN.setIsSeen(true);
                                teamN.save();
                            }


                        }

                        newsDetailTxtV.setText(Html.fromHtml(news.getContent()));
                        dateTxtView.setText(news.getDate());

                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "نأسف حدث حطأ في جلب بعض البيانات!", Snackbar.LENGTH_INDEFINITE)
                                .setAction("اعد المحاولة", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        featchData();
                                    }
                                });
                        snackbar.setActionTextColor(Color.RED);

                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);

                        snackbar.show();
                    } finally {
                        startRotatAnimation(fab, false);
                        fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_share));
                        adapter.notifyDataSetChanged();

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
