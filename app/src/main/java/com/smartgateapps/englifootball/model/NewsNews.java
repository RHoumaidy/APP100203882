package com.smartgateapps.englifootball.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.smartgateapps.englifootball.engli.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raafat on 20/01/2016.
 */
public class NewsNews {
    public final static String TABLE_NAME = "NEWS_NEWS";
    public static final String COL_LEFT_ID = "LEFT_ID";
    public static final String COL_RIGHT_ID = "RIGHT_ID";
    public static final String[] COLS = new String[]{COL_LEFT_ID,COL_RIGHT_ID};


    public static String getCreateSql(){
        return "CREATE TABLE IF NOT EXISTS "+TABLE_NAME +"(" +
                COL_LEFT_ID +" INTEGER ," +
                COL_RIGHT_ID +" INTEGER ," +
                "PRIMARY KEY ("+COL_LEFT_ID+","+COL_RIGHT_ID+"));";
    }

    public static List<NewsNews> load(Long leftId , Long righId){
        String where = " 1 = 1 ";
        where += (leftId == null)?"":" AND "+COL_LEFT_ID +" = "+leftId;
        where += (righId == null)?"": " AND "+COL_RIGHT_ID +" = "+righId;

        List<NewsNews> res = new ArrayList<>();
        Cursor c = MyApplication.dbr.query(TABLE_NAME,COLS,where,null,null,null,null);
        if(c.moveToFirst()){
            do {
                NewsNews itme = new NewsNews(
                        c.getLong(c.getColumnIndex(COL_LEFT_ID)),
                        c.getLong(c.getColumnIndex(COL_RIGHT_ID))
                );
                res.add(itme);
            }while (c.moveToNext());
        }
        c.close();

        return res;
    }

    public boolean save(){
        ContentValues cv = new ContentValues();
        cv.put(COL_LEFT_ID,this.getLeftId());
        cv.put(COL_RIGHT_ID,this.getRightId());

        return MyApplication.dbw.insert(TABLE_NAME,null,cv)>=0;

    }

    public static List<News> getAllRelatedNewsForLeftNews(Long leftId){
        List<NewsNews> relatedNews = NewsNews.load(leftId,null);
        List<News> res = new ArrayList<>();

        for(NewsNews itm : relatedNews){
            News news  = News.load(itm.getRightId(),null,null);
            res.add(news);
        }

        return res;

    }



    private Long leftId;
    private Long rightId;

    public NewsNews(Long leftId, Long rightId) {
        this.leftId = leftId;
        this.rightId = rightId;
    }

    public Long getLeftId() {
        return leftId;
    }

    public void setLeftId(Long leftId) {
        this.leftId = leftId;
    }

    public Long getRightId() {
        return rightId;
    }

    public void setRightId(Long rightId) {
        this.rightId = rightId;
    }
}
