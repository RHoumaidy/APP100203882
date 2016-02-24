package com.smartgateapps.englifootball.activities;

import android.net.http.SslError;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.smartgateapps.englifootball.model.LeaguNews;
import com.smartgateapps.englifootball.model.Legue;
import com.smartgateapps.englifootball.model.News;
import com.smartgateapps.englifootball.services.GetAllDawriNewsReciever;
import com.smartgateapps.englifootball.engli.MyApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raafat on 12/01/2016.
 */
public class NewsListFragmentBackground {

    public String urlExtention;
    private String urlExtentionPg;

    private WebView webView1;
    private WebView webView2;
    private WebView webView3;
    private WebView webView4;

    public boolean isLeague;
    public static int number = 0;


    public int pageIdx = 1;
    public int leaguId;

    public NewsListFragmentBackground() {
        webView1 = new WebView(MyApplication.APP_CTX);
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.getSettings().setLoadsImagesAutomatically(false);
        webView1.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
        webView1.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                number++;
                webView1.loadUrl(
                        "javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'," +
                                0+");");
                featchData2();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                number++;
                featchData2();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                number++;
                featchData2();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                number++;
                featchData2();
            }
        });

        webView2 = new WebView(MyApplication.APP_CTX);
        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.getSettings().setLoadsImagesAutomatically(false);
        webView2.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
        webView2.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                number++;
                webView2.loadUrl(
                        "javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'," +
                                1+");");
                featchData3();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                number++;
                featchData3();

            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                number++;
                featchData3();

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                number++;
                featchData3();

            }
        });

        webView3 = new WebView(MyApplication.APP_CTX);
        webView3.getSettings().setJavaScriptEnabled(true);
        webView3.getSettings().setLoadsImagesAutomatically(false);
        webView3.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
        webView3.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView3.loadUrl(
                        "javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'," +
                                2 + ");");
                featchData4();

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                number++;
                featchData4();

            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                number++;
                featchData4();

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                number++;
                featchData4();

            }
        });

        webView4 = new WebView(MyApplication.APP_CTX);
        webView4.getSettings().setJavaScriptEnabled(true);
        webView4.getSettings().setLoadsImagesAutomatically(false);
        webView4.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
        webView4.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView4.loadUrl(
                        "javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'," +
                                3+");");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                number++;
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                number++;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                number++;
            }
        });

    }


    public void featchData() {

        if (MyApplication.instance.isNetworkAvailable()) {
            urlExtentionPg = urlExtention + pageIdx;
            final String url = MyApplication.BASE_URL + urlExtentionPg;
            webView1.loadUrl(url);
        } else {
            number++;
            if (number == 4)
                GetAllDawriNewsReciever.completeWakefulIntent(GetAllDawriNewsReciever.instance.intent);

        }

    }

    public void featchData2() {
        urlExtention = Legue.load((long) (1)).get(0).getNewsUrl();
        leaguId = 1;
        if (MyApplication.instance.isNetworkAvailable()) {
            urlExtentionPg = urlExtention + pageIdx;
            final String url = MyApplication.BASE_URL + urlExtentionPg;
            webView2.loadUrl(url);
        } else {
            number++;
            if (number == 4)
                GetAllDawriNewsReciever.completeWakefulIntent(GetAllDawriNewsReciever.instance.intent);

        }

    }

    public void featchData3() {
        urlExtention = Legue.load((long) (2)).get(0).getNewsUrl();
        leaguId = 2;
        if (MyApplication.instance.isNetworkAvailable()) {
            urlExtentionPg = urlExtention + pageIdx;
            final String url = MyApplication.BASE_URL + urlExtentionPg;
            webView3.loadUrl(url);
        } else {
            number++;
            if (number == 4)
                GetAllDawriNewsReciever.completeWakefulIntent(GetAllDawriNewsReciever.instance.intent);

        }

    }

    public void featchData4() {
        urlExtention = Legue.load((long) (3)).get(0).getNewsUrl();
        leaguId = 3;
        if (MyApplication.instance.isNetworkAvailable()) {
            urlExtentionPg = urlExtention + pageIdx;
            final String url = MyApplication.BASE_URL + urlExtentionPg;
            webView4.loadUrl(url);
        } else {
            number++;
            if (number == 4)
                GetAllDawriNewsReciever.completeWakefulIntent(GetAllDawriNewsReciever.instance.intent);

        }

    }

    class MyJavaScriptInterface {


        @JavascriptInterface
        @SuppressWarnings("unused")
        public void showHTML(final String html,String leagueIdS) {
            String htm = html;
            Document doc = Jsoup.parse(html);
            int leaguId = Integer.valueOf(leagueIdS);
            try {
                Element newsList = doc.getElementsByClass("newsList").first();
                Element ul_news_list = newsList.getElementsByTag("ul").first();

                Elements lis = ul_news_list.getElementsByTag("li");
                for (int i = lis.size() - 1; i >= 0; i--) {
                    Element li = lis.get(i);

                    Element a = li.getElementsByTag("a").first();
                    Element img = a.getElementsByTag("img").first();
                    String imgUrl = img.attr("src");

                    Element div = li.getElementsByTag("div").first();
                    a = div.getElementsByTag("a").first();
                    Element p = div.getElementsByTag("p").first();
                    Element strong = a.getElementsByTag("strong").first();
                    String title = strong.text();
                    String subTitle = p.text();


                    News news = new News();
                    news.setUrl(a.attr("href"));
                    news.setImgUrl(imgUrl.substring(0, imgUrl.indexOf("&")));
                    news.setSubTitle(subTitle);
                    news.setTitle(title);
                    news.save();
                    LeaguNews leaguNews = new LeaguNews();
                    leaguNews.setLeaguId(leaguId);
                    leaguNews.setNewsId(news.getId());
                    leaguNews.setPageIdx(pageIdx);
                    leaguNews.setIsSeen(true);
                    leaguNews.save();

                    //adapter.notifyDataSetChanged();
                }


            } catch (Exception e) {
                String st = e.getMessage();

            } finally {

                number++;
//                Toast.makeText(MyApplication.APP_CTX,number+"",Toast.LENGTH_LONG).show();
                if (number == 4)
                    GetAllDawriNewsReciever.completeWakefulIntent(GetAllDawriNewsReciever.instance.intent);


            }


        }


    }

}

