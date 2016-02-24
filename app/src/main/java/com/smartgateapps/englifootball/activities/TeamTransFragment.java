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
import com.smartgateapps.englifootball.Adapter.TeamTransformationAdapter;
import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.TeamTransformation;
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
 * Created by Raafat on 30/12/2015.
 */
public class TeamTransFragment extends Fragment {

    private List<TeamTransformation> dataListV;
    private TeamTransformationAdapter adapter;
    private String urlExtention;
    private int teamId;
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

    public TeamTransFragment() {

        dataListV = new ArrayList<>();
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
//        featchData();
        timer = new Timer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        teamId = args.getInt("URL_EXT");
        urlExtention = "?team=" + teamId + "&mode=t";
        adapter = new TeamTransformationAdapter(getActivity(), dataListV);


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
        if (dataListV.size() == 0) {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player_goaler_layout, container, false);
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
        recyclerView.setNestedScrollingEnabled(true);
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
                    List<TeamTransformation> tmpList = new ArrayList<TeamTransformation>();

                    try {
                        Element mb20 = doc.getElementsByClass("mb20").first();
                        Element side_transfer = mb20.getElementsByClass("side_transfer").first();
                        Element table = side_transfer.getElementsByClass("border_w").first();
                        Element tableBody = table.getElementsByTag("tbody").first();

                        Elements trs = tableBody.getElementsByTag("tr");


                        for (Element tr : trs) {
                            if (tr.attr("class").equalsIgnoreCase("bg_dark"))
                                continue;

                            Elements tds = tr.getElementsByTag("td");
                            Element dateTd = tds.get(0);
                            Element playerTd = tds.get(1);
                            Element teamFrTd = tds.get(2);
                            Element teamToTd = tds.get(3);
                            Element typeTd = tds.get(4);

                            String dateOF = dateTd.text();
                            String[] dataParts = dateOF.split("\\s");
                            int month = MyApplication.monthOfTheYear.get(dataParts[1]);
                            int year = Integer.parseInt(dataParts[2]);
                            int day = Integer.parseInt(dataParts[0]);
                            String date = year + "/" + month + "/" + day;
                            String playerName = playerTd.getElementsByTag("a").first().text();
                            String oTeamN = "";
                            String teamCurl = null;
                            Element teamFr = teamFrTd.getElementsByTag("a").first();
                            if (teamFr != null) {
                                String teamFrN = teamFr.text();
                                Element img = teamFr.getElementsByTag("img").first();
                                String imgUrl = img.attr("src");
                                String url = teamFr.attr("href");
                                int teamFrId = Integer.parseInt(url.split("=")[1]);
                                if (teamFrId != teamId) {
                                    oTeamN = "من " + teamFrN;
                                    teamCurl = imgUrl;
                                }
                            }
                            Element teamTo = teamToTd.getElementsByTag("a").first();
                            if (teamTo != null) {
                                String teamToN = teamTo.text();
                                Element img = teamTo.getElementsByTag("img").first();
                                String imgUrl = img.attr("src");
                                String url = teamTo.attr("href");
                                int teamToId = Integer.parseInt(url.split("=")[1]);
                                if (teamToId != teamId) {
                                    oTeamN = "الى " + teamToN;
                                    teamCurl = imgUrl;
                                }
                            }

                            TeamTransformation item = new TeamTransformation();
                            item.setDate(date);
                            item.setTeamId(teamId);
                            item.setoTeam(oTeamN);
                            item.setoTeamCUrl(teamCurl);
                            item.setpName(playerName);
                            item.setType(typeTd.text());

                            tmpList.add(item);


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

                        dataListV.clear();
                        dataListV.addAll(tmpList);
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
}
