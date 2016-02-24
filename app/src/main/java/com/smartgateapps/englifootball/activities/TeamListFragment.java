package com.smartgateapps.englifootball.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import com.smartgateapps.englifootball.Adapter.TeamsAdapter;
import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.Team;
import com.smartgateapps.englifootball.model.TeamLeague;
import com.smartgateapps.englifootball.engli.MyApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import it.michelelacorte.elasticprogressbar.ElasticDownloadView;

/**
 * Created by Raafat on 04/11/2015.
 */
public class TeamListFragment extends Fragment {

    private TeamsAdapter adapter;
    private List<Team> allTeams;
    private String urlExtention;
    private WebView webView;
    private RecyclerView recyclerView;
    private LinearLayout progressBarLL;
    private ElasticDownloadView progressBar;
    private TextView progressBarTxtV;
    private RelativeLayout relativeLayout;

    private Timer timer;
    private TimerTask timerTask;
    private String[] waiting = new String[]{"يرجى الإنتظار ", "يرجى الإنتظار .", "يرجى الإنتظار ..", "يرجى الإنتظار ..."};
    private int idx = 0;
    private int leaguId;

    Set<Integer> keys;

    public TeamListFragment() {
        keys = MyApplication.teamsLogos.keySet();
        allTeams = new ArrayList<>();
        allTeams = TeamLeague.getAllTeamsForLeagu(leaguId);
        if (allTeams == null)
            allTeams = new ArrayList<>();
        webView = new WebView(MyApplication.APP_CTX);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);

        webView.addJavascriptInterface(new MyJavaScriptInterface1(), "HtmlViewer");
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

        // featchData();
        timer = new Timer();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TeamsAdapter(allTeams, getContext(), R.layout.team_list_row_layout);
        Bundle args = getArguments();
        urlExtention = args.getString("URL_EXT");
        leaguId = args.getInt("LEAGUE_ID");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_goaler_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        recyclerView = ((RecyclerView) view.findViewById(R.id.playerGoalerListView));
        progressBarLL = (LinearLayout) view.findViewById(R.id.playerGoalerProgressBarLL);
        progressBarTxtV = (TextView) view.findViewById(R.id.playerGoalerProgressBarTxtV);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.listRelativeLayout);
        progressBar = (ElasticDownloadView) view.findViewById(R.id.progressBar);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager lnLayoutMgr;
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            lnLayoutMgr = new GridLayoutManager(getActivity(), 2);
        else
            lnLayoutMgr = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(lnLayoutMgr);
        recyclerView.setAdapter(adapter);


        final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
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
                    Team currTeam = allTeams.get(pos);
                    Intent toTeamDetail = new Intent(getActivity(), TeamDetailsActivity.class);
                    toTeamDetail.putExtra("TEAM_ID", currTeam.getId());
                    startActivity(toTeamDetail);
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
        List<Team> tmpList = TeamLeague.getAllTeamsForLeagu(leaguId);
        if (tmpList == null || tmpList.size() == 0) {
            setListShown(false);
            featchData();
        } else {
            setListShown(true);
            allTeams.clear();
            allTeams.addAll(tmpList);
        }

        adapter.notifyDataSetChanged();
    }

    private void setListShown(boolean b) {
        int listViewVisibility = b ? View.VISIBLE : View.GONE;
        int progressBarVisibility = b ? View.GONE : View.VISIBLE;

        recyclerView.setVisibility(listViewVisibility);
        progressBarLL.setVisibility(progressBarVisibility);
    }


    private void featchData() {

        progressBar.startIntro();
        progressBar.setProgress(0);
        if (!MyApplication.instance.isNetworkAvailable()) {
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
        } else {


            webView.stopLoading();
            webView.loadUrl(MyApplication.BASE_URL + urlExtention);
        }
    }

    class MyJavaScriptInterface1 {


        @JavascriptInterface
        @SuppressWarnings("unused")
        public void showHTML(final String html) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String htm = html;
                    Document doc = Jsoup.parse(html);

                    try {
                        Element content = doc.getElementById("content");
                        Element div_mb20 = content.getElementsByClass("mb20").get(0);
                        Element table = div_mb20.getElementsByTag("table").first();
                        Element table_body = table.getElementsByTag("tbody").first();

                        List<Team> tmpTeams = new ArrayList<Team>();
                        for (Element tr : table_body.getElementsByTag("tr")) {

                            Element tdl = tr.getElementsByTag("td").get(0);
                            Element tdr = tr.getElementsByTag("td").get(1);

                            Element al = tdl.getElementsByTag("a").first();
                            Element ar = tdr.getElementsByTag("a").first();

                            Team team1 = new Team("");
                            Team team2 = new Team("");

                            String team1N = al.text();
                            String team2N = ar.text();

                            String team1U = al.attr("href");
                            String team2U = ar.attr("href");

                            String[] stA1 = team1U.split("\\|");
                            String[] stA2 = team2U.split("\\|");

                            int team1Id = Integer.parseInt(stA1[1]);
                            int team2Id = Integer.parseInt(stA2[1]);

                            String team1Url = MyApplication.BASE_URL + "?team=" + team1Id;
                            String team2Url = MyApplication.BASE_URL + "?team=" + team2Id;

                            team1.setTeamName(team1N);
                            team1.setId(team1Id);
                            team1.setTeamUrl(team1Url);
                            team1.setLeagueId(leaguId);

                            team2.setTeamName(team2N);
                            team2.setId(team2Id);
                            team2.setTeamUrl(team2Url);
                            team2.setLeagueId(leaguId);

                            int logo1 = keys.contains(new Integer(team1Id)) ? MyApplication.teamsLogos.get(team1Id) : R.mipmap.t_unknown;
                            int logo2 = keys.contains(new Integer(team2Id)) ? MyApplication.teamsLogos.get(team2Id) : R.mipmap.t_unknown;

                            team1.setTeamLogo(logo1);
                            team2.setTeamLogo(logo2);

                            team1.save();
                            team2.save();
                            tmpTeams.add(team1);
                            tmpTeams.add(team2);
                        }
                        allTeams.clear();
                        allTeams.addAll(tmpTeams);
                        adapter.notifyDataSetChanged();
                        setListShown(true);

                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(recyclerView, "نأسف حدث حطأ في جلب بعض البيانات", Snackbar.LENGTH_INDEFINITE)
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
