package com.smartgateapps.englifootball.activities;

import android.app.Activity;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartgateapps.englifootball.Adapter.DividerItemDecoration;
import com.smartgateapps.englifootball.Adapter.TeamPlaceAdapter;
import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.Team;
import com.smartgateapps.englifootball.engli.MyApplication;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.michelelacorte.elasticprogressbar.ElasticDownloadView;

/**
 * Created by Raafat on 15/12/2015.
 */
public class PlacedListFragment extends Fragment {

    private List<Team> teams;
    private TeamPlaceAdapter adapter;
    private String urlExtention;
    private WebView webView;
    private RecyclerView recyclerView;
    private LinearLayout progressBarLL;
    private ElasticDownloadView progressBar;
    private TextView progressBarTxtV;
    private RelativeLayout relativeLayout;


    int res;

    private Timer timer;
    private TimerTask timerTask;
    private String[] waiting = new String[]{"يرجى الإنتظار ", "يرجى الإنتظار .", "يرجى الإنتظار ..", "يرجى الإنتظار ..."};
    private int idx = 0;

    public PlacedListFragment() {
        teams = new ArrayList<>();
        webView = new WebView(MyApplication.APP_CTX);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);

        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progressBar.fail();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                progressBar.fail();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                progressBar.fail();
            }
        });

        //setListShown(false);
        //featchData();
        timer = new Timer();


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        urlExtention = args.getString("URL_EXT");
        adapter = new TeamPlaceAdapter(getActivity(), R.layout.fragment_places_item, teams);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBarTxtV.setText(waiting[idx++]);
                        idx %= 4;
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 800);
    }

    @Override
    public void onResume() {
        super.onResume();
//        featchData();
        if (teams.size() == 0) {
            setListShown(false);
            featchData();
        } else
            setListShown(true);
    }

    private void setListShown(boolean b) {
        int listViewVisibility = b ? View.VISIBLE : View.GONE;
        int progressBarVisibility = b ? View.GONE : View.VISIBLE;

        recyclerView.setVisibility(listViewVisibility);
        progressBarLL.setVisibility(progressBarVisibility);
    }

    public void checkInternet() {
        if (!MyApplication.instance.isNetworkAvailable()) {
            Snackbar snackbar = Snackbar.make(relativeLayout, "لا يوجد اتصال بالانترنت", Snackbar.LENGTH_INDEFINITE)
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
    public void onViewCreated(View view, Bundle savedInstanceState) {

        recyclerView = ((RecyclerView) view.findViewById(R.id.playerGoalerListView));
        progressBarLL = (LinearLayout) view.findViewById(R.id.playerGoalerProgressBarLL);
        progressBarTxtV = (TextView) view.findViewById(R.id.playerGoalerProgressBarTxtV);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.listRelativeLayout);
        progressBar = (ElasticDownloadView) view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(headersDecor);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        adapter.registerAdapterDataObserver((new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        }));
        recyclerView.setAdapter(adapter);

        checkInternet();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player_goaler_layout, container, false);
    }

    private void featchData() {

        progressBar.startIntro();
        progressBar.setProgress(0);
        if (!MyApplication.instance.isNetworkAvailable()) {
            try {
                Snackbar snackbar = Snackbar.make(relativeLayout, "لا يوجد اتصال بالانترنت", Snackbar.LENGTH_INDEFINITE)
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
            } catch (Exception e) {

            }
        } else {

            webView.stopLoading();
            webView.loadUrl(MyApplication.BASE_URL + urlExtention);
        }
    }


    class MyJavaScriptInterface {


        @JavascriptInterface
        @SuppressWarnings("unused")
        public void showHTML(final String html) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String htm = html;
                    Document doc = Jsoup.parse(html);

                    try {
                        Element ranksTable = doc.getElementById("ranksTable");
                        Element tbody = ranksTable.getElementsByTag("tbody").first();
                        Elements trs = tbody.getElementsByTag("tr");
                        List<Team> tmpList = new ArrayList<>();


                        for (int i = 0; i < trs.size(); ++i) {
                            Element tr = trs.get(i);

                            if (!tr.getElementsByTag("td").get(0).attr("class").equalsIgnoreCase("s_m"))
                                continue;

                            Elements tds = tr.getElementsByTag("td");
                            Element place = tds.get(0);
                            Element teamName = tds.get(1);
                            Element matchPlayed = tds.get(2);
                            Element matchWinned = tds.get(3);
                            Element matchDrawed = tds.get(4);
                            Element matchLosed = tds.get(5);
                            Element points = tds.get(9);

                            Element tA = teamName.getElementsByTag("a").first();

                            String placeVal = place.text();
                            String teamNameVal = teamName.text();
                            String matchPlayedVal = matchPlayed.text();
                            String matchWinnedVal = matchWinned.text();
                            String matchDrawedVal = matchDrawed.text();
                            String matchLosedVal = matchLosed.text();
                            String pointsVal = points.text();
                            String teamUrl = tA.attr("href");

                            Team team = new Team("");
                            team.setTeamName(teamNameVal);
                            team.setTeamUrl(teamUrl);
                            team.setMatchPlayed(matchPlayedVal);
                            team.setMatchDrawed(matchDrawedVal);
                            team.setMatchLosed(matchLosedVal);
                            team.setMatchWined(matchWinnedVal);
                            team.setPoints(pointsVal);
                            team.setPlace(placeVal);


                            tmpList.add(team);

                        }

                        teams.clear();
                        teams.addAll(tmpList);

                        adapter.notifyDataSetChanged();
                        try {
                            setListShown(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(relativeLayout, "نأسف حدث حطأ في جلب بعض البيانات!", Snackbar.LENGTH_INDEFINITE)
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
                        progressBar.fail();
                        snackbar.show();
                    } finally {

                    }

                }
            });
        }

    }
}
