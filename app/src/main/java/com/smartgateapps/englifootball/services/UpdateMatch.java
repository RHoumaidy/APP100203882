package com.smartgateapps.englifootball.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.Html;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.smartgateapps.englifootball.engli.MyApplication;
import com.smartgateapps.englifootball.model.Match;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by Raafat on 09/02/2016.
 */
public class UpdateMatch extends WakefulBroadcastReceiver {

    private static long MINUTE_IN_MILLIS = 60 * 1000;
    private static long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    private WebView webView;
    private Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.intent = intent;

        Long mId = intent.getLongExtra("MATCH_ID", 0);
        Match match = Match.load(mId);

        if (!MyApplication.instance.isNetworkAvailable()) {
            match.setNotifyDateTime(match.getDateTime() + 7 * 60 * 1000);
            match.update();
            match.registerMatchUpdateFirstTime();
        }else {
            featchDate(match);
        }

        boolean isChecked = match.isNotifyMe();
        if (MyApplication.getCurretnDateTime() - match.getDateTime() < 1000*60 && isChecked) {
            Intent toNotification = new Intent(context, MatchNotification.class);
            toNotification.putExtra("MATCH_ID", mId);
            startWakefulService(context, toNotification);
        }
    }


    public void featchDate(final Match match) {
        webView = new WebView(MyApplication.APP_CTX);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);

        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>',"+match.getId()+");");
            }

        });
        webView.loadUrl(MyApplication.BASE_URL + match.getMatchUrl());
    }


    class MyJavaScriptInterface {


        @JavascriptInterface
        @SuppressWarnings("unused")
        public void showHTML(final String html,String mId) {
            Long mIdL = Long.valueOf(mId);
            Match match = Match.load(mIdL);
            String htm = html;
            Document doc = Jsoup.parse(html);
            Toast.makeText(MyApplication.APP_CTX,"Match updated\n"+match.getTeamL().getTeamName()+" x "+match.getTeamR().getTeamName(),Toast.LENGTH_LONG).show();
            try {

                Element mainContent = doc.getElementById("mainContent");
                Element fullcontent = mainContent.getElementById("fullcontent");
                Element matchesTable = fullcontent.getElementById("matchesTable");
                Element tbody = matchesTable.getElementsByTag("tbody").first();

                Element jm5x3 = tbody.getElementById("jm5x3");
                Element colorBlue = jm5x3.getElementsByAttributeValue("color", "blue").first();

                if (colorBlue != null ){// the match is going ...
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

                    if(match.isNotifyMe() &&
                            (!resultL.equalsIgnoreCase(match.getResultL()) || !resultR.equalsIgnoreCase(match.getResultR()))&&
                            !match.getResultR().equalsIgnoreCase("--") && !match.getResultL().equalsIgnoreCase("--") ){
                        Intent toNotification = new Intent(MyApplication.APP_CTX, MatchGoalNotification.class);
                        toNotification.putExtra("MATCH_ID", mId);
                        startWakefulService(MyApplication.APP_CTX, toNotification);
                    }

                    match.setNotifyDateTime(MyApplication.getCurretnDateTime()+4*60*1000);
                    match.setResultL(resultL);
                    match.setResultR(resultR);
                    match.update();
                    match.registerMatchUpdateFirstTime();


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


            } catch (Exception e) {
                match.registerMatchUpdateDate(MyApplication.getCurretnDateTime() + MINUTE_IN_MILLIS * 7);
            }

            completeWakefulIntent(intent);

        }

    }
}
