package com.smartgateapps.englifootball.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import com.smartgateapps.englifootball.Adapter.MatchAdapterAdapter;
import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.Match;
import com.smartgateapps.englifootball.model.MatchMatch;
import com.smartgateapps.englifootball.engli.MyApplication;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.michelelacorte.elasticprogressbar.ElasticDownloadView;

/**
 * Created by Raafat on 15/01/2016.
 */
public class MatchMatchFragment extends Fragment {

    private List<MatchMatch> mathList;
    private MatchAdapterAdapter adapter;
    private String urlExtention;
    private WebView webView;
    private RecyclerView recyclerView;
    private LinearLayout progressBarLL;
    private ElasticDownloadView progressBar;
    private TextView progressBarTxtV;

    private Timer timer;
    private TimerTask timerTask;
    private String[] waiting = new String[]{"يرجى الإنتظار ", "يرجى الإنتظار .", "يرجى الإنتظار ..", "يرجى الإنتظار ..."};
    private int idx = 0;
    private int prevSpinnerSelected = 0;

    private RelativeLayout relativeLayout;

    public MatchMatchFragment() {
        mathList = new ArrayList<>();

        webView = new WebView(MyApplication.APP_CTX);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setGeolocationEnabled(true);

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
//        featchData();
        timer = new Timer();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        urlExtention = args.getString("URL_EXT");

        adapter = new MatchAdapterAdapter(getActivity(), R.layout.match_match_item_layout, R.layout.match_mathc_header_layut, mathList);

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
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
//        featchData();
        if (mathList.size() == 0) {
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
        if (progressBarLL.getVisibility() == View.VISIBLE)
            progressBar.startIntro();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player_goaler_layout, container, false);
    }

    private int getLayoutManagerOrientation(int activityOrientation) {
        if (activityOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            return LinearLayoutManager.VERTICAL;
        } else {
            return LinearLayoutManager.HORIZONTAL;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        recyclerView = ((RecyclerView) view.findViewById(R.id.playerGoalerListView));
        progressBarLL = (LinearLayout) view.findViewById(R.id.playerGoalerProgressBarLL);
        progressBarTxtV = (TextView) view.findViewById(R.id.playerGoalerProgressBarTxtV);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.listRelativeLayout);
        progressBar = (ElasticDownloadView) view.findViewById(R.id.progressBar);

        int orientation = getLayoutManagerOrientation(getResources().getConfiguration().orientation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), orientation, false));

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

                        Element matchesTable = doc.getElementById("matchesTable");
                        Element tbody = matchesTable.getElementsByTag("tbody").first();

                        Elements trs = tbody.getElementsByTag("tr");

                        List<MatchMatch> tmpList = new ArrayList<>();
                        List<Match> tmpChildList = new ArrayList<>();

                        int hIdx1 = 0;
                        int hIdx2 = 0;
                        String date = "";
                        String leageName = "";
                        String leagoIconUrl = "";

                        for (int i = 1; i < trs.size(); ++i) {

                            Elements tds = trs.get(i).getElementsByTag("td");

                            String time;
                            String teamR;
                            String resultR;
                            String resultL;
                            String teamL;

                            if (trs.get(i).attr("class").equalsIgnoreCase("wNoteTr"))
                                continue;

                            if (tds.size() == 1) { // this is a date header
                                if (tds.get(0).attr("class").equalsIgnoreCase("match_league")) {
                                    if (tmpChildList.size() > 0) {
                                        MatchMatch matchMatch = new MatchMatch();
                                        matchMatch.setHdId(hIdx1);
                                        matchMatch.setLeague(leageName);
                                        matchMatch.setLeagueImageUrl(leagoIconUrl);
                                        matchMatch.setMatches(tmpChildList);
                                        tmpList.add(matchMatch);
                                    }
                                    hIdx2 = 0;
                                    hIdx1++;
                                    leageName = tds.get(0).text();
                                    leagoIconUrl = tds.get(0).getElementsByTag("img").attr("src");
                                    tmpChildList.clear();


                                } else if (tds.get(0).attr("class").equalsIgnoreCase("match_date")) {
                                    hIdx2++;
                                    date = tds.first().text();
                                    Match header = new Match();
                                    header.setIsHeader(true);
                                    time = "12:00";
                                    header.setDateTime(MyApplication.parseDateTime(date, time));
                                    tmpChildList.add(header);
                                }
                            } else {

                                String matchLocation = trs.get(i).attr("onclick");
                                String[] matchLocationSplit = matchLocation.split("=");
                                Long matchId = Long.valueOf(matchLocationSplit[2].substring(0, matchLocationSplit[2].length() - 2));

                                int progress;
                                Long currTime = MyApplication.getCurretnDateTime();
                                Long matchDateTime;
                                if (tds.get(1).getAllElements().size() == 1) {

                                    time = tds.get(1).text();
                                    matchDateTime = MyApplication.parseDateTime(date, time);
                                    if (matchDateTime < currTime)
                                        progress = -1;
                                    else
                                        progress = 1;
                                } else {
                                    time = MyApplication.sourceTimeFormate.format(new Date(currTime));
                                    progress = 0;
                                    matchDateTime = MyApplication.parseDateTime(date, time);
                                }

                                Element teamD = tds.get(2);
                                Element teamF = teamD.getElementsByTag("font").first();
                                if (teamF != null)
                                    teamR = teamF.text();
                                else
                                    teamR = tds.get(2).text();
                                Element tdRes = tds.get(3);
                                resultR = Html.fromHtml(tdRes.text()).toString();
                                resultL = resultR.split(":")[0].replaceAll("\\s+", "");
                                resultR = resultR.split(":")[1].replaceAll("\\s+", "");

                                teamD = tds.get(4);
                                teamF = teamD.getElementsByTag("font").first();
                                if (teamF != null)
                                    teamL = teamF.text();
                                else
                                    teamL = tds.get(4).text();

                                Match match = new Match();

                                String tmpDate = date;
//                                tmpDate = MyApplication.converteDate(time,date);
//                                time = MyApplication.converteTime(time,date);

                                match.setId(matchId);
                                match.sethId(hIdx2);
                                match.setDateTime(matchDateTime);
                                match.setTeamR(teamR);
                                match.setTeamL(teamL);
                                match.setResultL(resultL);
                                match.setResultR(resultR);
                                match.setHasBeenUpdated(progress == -1);
                                match.setNotifyDateTime(matchDateTime);
                                match.setIsHeader(false);
                                match.setNotifyMe(false);
                                tmpChildList.add(match);
                            }
                        }

                        MatchMatch matchMatch = new MatchMatch();
                        matchMatch.setHdId(hIdx1);
                        matchMatch.setLeague(leageName);
                        matchMatch.setLeagueImageUrl(leagoIconUrl);
                        matchMatch.setMatches(tmpChildList);
                        tmpList.add(matchMatch);
                        mathList.clear();
                        mathList.addAll(tmpList);

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
                    }


                }
            });
        }

    }
}
