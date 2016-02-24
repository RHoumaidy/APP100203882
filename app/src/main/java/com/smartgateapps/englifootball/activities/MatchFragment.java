package com.smartgateapps.englifootball.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.smartgateapps.englifootball.Adapter.DividerItemDecoration;
import com.smartgateapps.englifootball.Adapter.MatchesAdapter;
import com.smartgateapps.englifootball.Adapter.SpinnerAdapter;
import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.Legue;
import com.smartgateapps.englifootball.model.Match;
import com.smartgateapps.englifootball.model.Stage;
import com.smartgateapps.englifootball.engli.MyApplication;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import it.michelelacorte.elasticprogressbar.ElasticDownloadView;

/**
 * Created by Raafat on 16/12/2015.
 */
public class MatchFragment extends Fragment {

    private List<Match> mathList;
    private MatchesAdapter adapter;
    private String urlExtention;
    private WebView webView;
    private RecyclerView recyclerView;
    private LinearLayout progressBarLL;
    private ElasticDownloadView progressBar;
    private LinearLayout matchFilterLL;
    private TextView progressBarTxtV;
    private SpinnerAdapter spinnerAdapter;
    private Spinner stageSpinner;
    private List<Stage> stageList;

    private ProgressBar matchProgressBar;

    private Timer timer;
    private TimerTask timerTask;
    private String[] waiting = new String[]{"يرجى الإنتظار ", "يرجى الإنتظار .", "يرجى الإنتظار ..", "يرجى الإنتظار ..."};
    private int idx = 0;
    private int prevSpinnerSelected = -1;
    private Legue legue;
    Dialog dialog;
    private RelativeLayout relativeLayout;

    public MatchFragment() {
        mathList = new ArrayList<>();
        stageList = new ArrayList<>();


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
        int leagueId = args.getInt("LEAGUE_ID");
        legue = Legue.load(new Long(leagueId)).get(0);
        stageList = Stage.getAllStagesForLeague(legue.getId());
        adapter = new MatchesAdapter(getActivity(), R.layout.fragment_match_item, mathList);
        spinnerAdapter = new SpinnerAdapter(getActivity(), android.R.layout.simple_list_item_checked, stageList);

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
        if (stageList.size() == 0) {
            setListShown(false);
            featchData();
        } else {
            mathList.clear();
            Stage s = ((Stage) stageSpinner.getItemAtPosition(0));
            mathList.addAll(s.getAllMatches());
            if (mathList.size() == 0) {
                setListShown(false);
                featchData();
            } else {
                setListShown(true);
                adapter.notifyDataSetChanged();
            }
        }

    }

    private void setListShown(boolean b) {
        int listViewVisibility = b ? View.VISIBLE : View.GONE;
        int progressBarVisibility = b ? View.GONE : View.VISIBLE;

        recyclerView.setVisibility(listViewVisibility);
        matchFilterLL.setVisibility(listViewVisibility);
        progressBarLL.setVisibility(progressBarVisibility);
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
        matchFilterLL = (LinearLayout) view.findViewById(R.id.choseMathcFilterLL);
        stageSpinner = (Spinner) view.findViewById(R.id.matchStageSpinner);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.listRelativeLayout);
        progressBar = (ElasticDownloadView) view.findViewById(R.id.progressBar);

        stageSpinner.setAdapter(spinnerAdapter);

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


        stageSpinner.post(new Runnable() {
            @Override
            public void run() {
                stageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Stage selStage = ((Stage) parent.getItemAtPosition(position));
                        urlExtention = "?" + selStage.getUrl();
                        if (position != prevSpinnerSelected) {
                            mathList.clear();
                            mathList.addAll(selStage.getAllMatches());
                            if (mathList.size() == 0) {
                                featchData();
                                setListShown(false);
                            } else
                                adapter.notifyDataSetChanged();

                            Map<String, String> dimensions = new HashMap<>();
                            dimensions.put("category", "استعراض مباريات : " + Legue.load(legue.getId()).get(0).getName());
                            ParseAnalytics.trackEventInBackground("open_match", dimensions);
                            dimensions.put("category", "استعراض مباريات");
                            ParseAnalytics.trackEventInBackground("open_match", dimensions);
                        }

                        prevSpinnerSelected = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

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
                    if (pos < 0)
                        return false;
                    final Match currMatch = mathList.get(pos);


                    dialog = new Dialog(getActivity());
                    dialog.setTitle(MyApplication.formatDateTime(currMatch.getDateTime())[0]);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setContentView(R.layout.fragment_match_item);

                    final CheckedTextView chkdTxtV = (CheckedTextView) dialog.findViewById(R.id.notifiMatchChTxt);
                    TextView teamLtxtV = (TextView) dialog.findViewById(R.id.matchTeamLTxtV);
                    TextView teamRtxtV = (TextView) dialog.findViewById(R.id.matchTeamRTxtV);
                    ImageView teamLImgV = (ImageView) dialog.findViewById(R.id.matchTeamLImgV);
                    ImageView teamRImgV = (ImageView) dialog.findViewById(R.id.matchTEamRImgV);
                    TextView resultRtxtV = (TextView) dialog.findViewById(R.id.matchResultRTxtV);
                    TextView resultLtxtV = (TextView) dialog.findViewById(R.id.matchResultLTxtV);
                    TextView timeTxtV = (TextView) dialog.findViewById(R.id.matchTimeTxtV);
                    TextView endMatchTxtV = (TextView) dialog.findViewById(R.id.matchEdnTxtV);
                    TextView goingMatchtxtV = (TextView) dialog.findViewById(R.id.matchGoingTxtV);
                    matchProgressBar = (ProgressBar) dialog.findViewById(R.id.matchProgressbar);

                    int matchProgress = currMatch.matchProgress();

                    if (matchProgress == 0)
                        featchDate2(currMatch);

                    if (matchProgress < 0)
                        endMatchTxtV.setVisibility(View.VISIBLE);
                    else if (matchProgress == 0)
                        goingMatchtxtV.setVisibility(View.VISIBLE);


                    teamLtxtV.setText(currMatch.getTeamL().getTeamName());
                    teamRtxtV.setText(currMatch.getTeamR().getTeamName());
                    resultLtxtV.setText(currMatch.getResultL());
                    resultRtxtV.setText(currMatch.getResultR());
                    timeTxtV.setText(MyApplication.formatDateTime(currMatch.getDateTime())[1]);


                    MyApplication.picasso
                            .load(currMatch.getTeamL().getTeamLogo())
                            .placeholder(R.drawable.water_mark)
                            .into(teamLImgV);
                    MyApplication.picasso
                            .load(currMatch.getTeamR().getTeamLogo())
                            .placeholder(R.drawable.water_mark)
                            .into(teamRImgV);

                    if (currMatch.matchProgress() >= 0)
                        chkdTxtV.setVisibility(View.VISIBLE);
                    chkdTxtV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chkdTxtV.setChecked(!chkdTxtV.isChecked());
                            currMatch.setNotifyMe(chkdTxtV.isChecked());
                            currMatch.update();
                        }
                    });
                    chkdTxtV.setChecked(currMatch.isNotifyMe());
                    teamLImgV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent toTeamDetail = new Intent(getActivity(), TeamDetailsActivity.class);
                            toTeamDetail.putExtra("TEAM_ID", currMatch.getTeamL().getId());
                            getActivity().startActivity(toTeamDetail);
                        }
                    });

                    teamRImgV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent toTeamDetail = new Intent(getActivity(), TeamDetailsActivity.class);
                            toTeamDetail.putExtra("TEAM_ID", currMatch.getTeamR().getId());
                            getActivity().startActivity(toTeamDetail);
                        }
                    });

                    dialog.show();

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

        stageSpinner.setSelection(0);

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
                    List<Match> tmpList = new ArrayList<>();

                    try {

                        Element contentTable = doc.getElementById("contentTable");
                        Element seasonpicker = contentTable.getElementsByClass("seasonpicker").get(1);
                        stageList.clear();
                        int idx = 0;
                        boolean b = false;
                        for (Element option : seasonpicker.getElementsByTag("option")) {
                            String url = option.attr("value");
                            String name = option.text();
                            if (name.equalsIgnoreCase("المراكز"))
                                continue;
                            Stage stage = new Stage(name, url, legue.getId());
                            stage.save();
                            if (!option.hasAttr("selected") && !b) {
                                idx++;
                            } else
                                b = true;
                            stageList.add(stage);
                        }
                        prevSpinnerSelected = idx;
                        stageSpinner.setSelection(idx);
                        spinnerAdapter.notifyDataSetChanged();


                        Element tbody = contentTable.getElementsByTag("tbody").first();

                        Elements trs = tbody.getElementsByTag("tr");

                        int hIdx = 0;
                        String date = "";
                        if (legue.getLeagueDate() == null || legue.getLeagueDate().equalsIgnoreCase("")) {
                            Element tr0 = trs.get(0);
                            Element table_title = tr0.getElementsByClass("table_title").first();
                            Element span = table_title.getAllElements().get(2);
                            legue.setLeagueDate(Html.fromHtml(span.text()).toString());
                            legue.update();
                        }
                        for (int i = 4; i < trs.size(); ++i) {

                            Elements tds = trs.get(i).getElementsByTag("td");

                            String time;
                            String teamR;
                            String resultR;
                            String resultReX = "0";
                            String resultL;
                            String resultLeX = "0";
                            String teamL;

                            if (trs.get(i).attr("class").equalsIgnoreCase("wNoteTr"))
                                continue;

                            if (tds.size() == 1) { // this is a date header
                                hIdx++;
                                date = tds.first().text();
                            } else {

                                String matchLocation = trs.get(i).attr("onclick");
                                String[] matchLocationSplit = matchLocation.split("=");
                                Long matchId = Long.valueOf(matchLocationSplit[2].substring(0, matchLocationSplit[2].length() - 2));

                                int progress;
                                Long currTime = MyApplication.getCurretnDateTime();
                                Long matchDateTime;
                                if (tds.get(0).getAllElements().size() == 1) {
                                    time = tds.get(0).text();
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

                                Element teamD = tds.get(1);
                                Element teamF = teamD.getElementsByTag("font").first();
                                if (teamF != null)
                                    teamR = teamF.text();
                                else
                                    teamR = tds.get(1).text();
                                Element tdRes = tds.get(2).getElementsByTag("span").first();
                                if (tdRes == null)
                                    tdRes = tds.get(2);
                                Element tdResEx = tds.get(2).getElementsByTag("div").first();
                                resultR = Html.fromHtml(tdRes.text()).toString();
                                resultL = resultR.split(":")[0].replaceAll("\\s+", "");
                                resultR = resultR.split(":")[1].replaceAll("\\s+", "");

                                if (tdResEx != null) {
                                    resultReX = Html.fromHtml(tdResEx.text()).toString();
                                    resultLeX = resultReX.split(":")[0].replaceAll("\\s+", "");
                                    resultReX = resultReX.split(":")[1].replaceAll("\\s+", "");
                                    int resRex = Integer.valueOf(resultReX);
                                    int resLeX = Integer.valueOf(resultLeX);
                                    int resR = Integer.valueOf(resultR);
                                    int resL = Integer.valueOf(resultR);

                                    resultR = resR + resRex + "";
                                    resultL = resL + resLeX + "";
                                }

                                teamD = tds.get(3);
                                teamF = teamD.getElementsByTag("font").first();
                                if (teamF != null)
                                    teamL = teamF.text();
                                else
                                    teamL = tds.get(3).text();

                                Match match = new Match();

                                match.setId(matchId);
                                match.sethId(hIdx);
                                match.setDateTime(matchDateTime);
                                match.setTeamR(teamR);
                                match.setTeamL(teamL);
                                match.setResultL(resultL);
                                match.setResultR(resultR);
                                match.setHasBeenUpdated(progress == -1);
                                match.setStage(((Stage) stageSpinner.getSelectedItem()));
                                match.setNotifyDateTime(matchDateTime);
                                match.setIsHeader(false);
                                match.setNotifyMe(false);
                                match.setLeagueId(legue.getId().intValue());
                                match.save();

                                if (match.getDateTime() + 2 * 60 * 1000 > currTime) {
                                    if (match.getDateTime() <= currTime)
                                        match.setNotifyDateTime(currTime + 2 * 60 * 1000);
                                    match.registerMatchUpdateFirstTime();
                                    match.setHasBeenUpdated(false);
                                    match.update();
                                }

                                tmpList.add(match);
                            }

                        }
//                        Match m = new Match();
//                        if (tmpList.size() == 0)
//                            tmpList.add(m);

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

                        mathList.clear();
                        mathList.addAll(tmpList);
                        if (mathList.size() == 0) {
                            Snackbar snackbar = Snackbar.make(relativeLayout, "جدول مباريات المسابقة غير متوفر حالياً", Snackbar.LENGTH_INDEFINITE);
                            View sbView = snackbar.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.YELLOW);
                            snackbar.show();

                        }
                        adapter.notifyDataSetChanged();
                        try {
                            setListShown(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                }
            });
        }

    }


    public void featchDate2(final Match match) {

        matchProgressBar.setVisibility(View.VISIBLE);
        final WebView webView2 = new WebView(MyApplication.APP_CTX);
        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.getSettings().setLoadsImagesAutomatically(false);

        webView2.addJavascriptInterface(new MyJavaScriptInterface2(), "HtmlViewer");

        webView2.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView2.loadUrl("javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'," + match.getId() + ");");
            }

        });
        webView2.stopLoading();
        webView2.loadUrl(MyApplication.BASE_URL + match.getMatchUrl());
    }


    class MyJavaScriptInterface2 {


        @JavascriptInterface
        @SuppressWarnings("unused")
        public void showHTML(final String html, final long mId) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String htm = html;
                    Document doc = Jsoup.parse(html);
                    try {

                        Element mainContent = doc.getElementById("mainContent");
                        Element fullcontent = mainContent.getElementById("fullcontent");
                        Element matchesTable = fullcontent.getElementById("matchesTable");
                        Element tbody = matchesTable.getElementsByTag("tbody").first();
                        Match match = Match.load(mId);

                        Element jm5x3 = tbody.getElementById("jm5x3");
                        Element colorBlue = jm5x3.getElementsByAttributeValue("color", "blue").first();

                        if (colorBlue != null) {// the match is going ...
                            String resultR;
                            String resultL;
                            String resultReX;
                            String resultLeX;

                            Element tdRes = jm5x3.getElementsByTag("span").first();
                            Element tdResEx = jm5x3.getElementsByTag("div").first();

                            resultR = Html.fromHtml(tdRes.text()).toString();
                            resultL = resultR.split(":")[0].replaceAll("\\s+", "");
                            resultR = resultR.split(":")[1].replaceAll("\\s+", "");

                            if (tdResEx != null) {
                                resultReX = Html.fromHtml(tdResEx.text()).toString();
                                resultLeX = resultReX.split(":")[0].replaceAll("\\s+", "");
                                resultReX = resultReX.split(":")[1].replaceAll("\\s+", "");
                                int resRex = Integer.valueOf(resultReX);
                                int resLeX = Integer.valueOf(resultLeX);
                                int resR = Integer.valueOf(resultR);
                                int resL = Integer.valueOf(resultR);

                                resultR = resR + resRex + "";
                                resultL = resL + resLeX + "";
                            }

                            if (!resultL.equalsIgnoreCase(match.getResultL()) || !resultR.equalsIgnoreCase(match.getResultR())) {
                                Toast.makeText(MyApplication.APP_CTX, "غوووول \n" + match.getTeamL().getTeamName() + " x " + match.getTeamR().getTeamName() +
                                        "\n" + match.getResultL() + "-" + match.getResultR(), Toast.LENGTH_LONG).show();
                            }

                            match.setResultL(resultL);
                            match.setResultR(resultR);
                            match.update();


                        } else { // the match eighter done or have not began yet
                            String resultR;
                            String resultL;

                            String resultReX;
                            String resultLeX;

                            Element tdRes = jm5x3.getElementsByTag("span").first();
                            Element tdResEx = jm5x3.getElementsByTag("div").first();

                            resultR = Html.fromHtml(tdRes.text()).toString();
                            resultL = resultR.split(":")[0].replaceAll("\\s+", "");
                            resultR = resultR.split(":")[1].replaceAll("\\s+", "");

                            if (tdResEx != null) {
                                resultReX = Html.fromHtml(tdResEx.text()).toString();
                                resultLeX = resultReX.split(":")[0].replaceAll("\\s+", "");
                                resultReX = resultReX.split(":")[1].replaceAll("\\s+", "");
                                int resRex = Integer.valueOf(resultReX);
                                int resLeX = Integer.valueOf(resultLeX);
                                int resR = Integer.valueOf(resultR);
                                int resL = Integer.valueOf(resultR);

                                resultR = resR + resRex + "";
                                resultL = resL + resLeX + "";
                            }


                            if (!(resultR.equalsIgnoreCase("--") || resultL.equalsIgnoreCase("--"))) {
                                match.setResultL(resultL);
                                match.setResultR(resultR);
                                match.setHasBeenUpdated(true);
                                match.update();
                            }


                        }
                        if (dialog != null && dialog.isShowing()) {
                            TextView resultRtxtV = (TextView) dialog.findViewById(R.id.matchResultRTxtV);
                            TextView resultLtxtV = (TextView) dialog.findViewById(R.id.matchResultLTxtV);
                            resultLtxtV.setText(match.getResultL());
                            resultRtxtV.setText(match.getResultR());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    matchProgressBar.setVisibility(View.GONE);
                    mathList.clear();
                    Stage s = ((Stage) stageSpinner.getSelectedItem());
                    mathList.addAll(s.getAllMatches());
                    adapter.notifyDataSetChanged();
                }

            });
        }
    }
}
