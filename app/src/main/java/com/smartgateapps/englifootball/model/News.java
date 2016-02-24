package com.smartgateapps.englifootball.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.smartgateapps.englifootball.engli.MyApplication;

import java.io.Serializable;

/**
 * Created by Raafat on 11/12/2015.
 */

public class News implements Serializable {


    public static final String TABLE_NAME = "NEWS";
    public static final String COL_ID = "ID";
    public static final String COL_URL = "URL";
    public static final String COL_IMG_URL = "IMAGE_URL";
    public static final String COL_TITL = "TITLE";
    public static final String COL_SUBTITLE = "SUBTITLE";
    public static final String COL_DATE = "DATE";
    public static final String COL_CONTENT = "CONTENT";


    public static final String[] COLS = new String[]{COL_ID,COL_URL,COL_IMG_URL,COL_TITL,COL_SUBTITLE,
            COL_DATE,COL_CONTENT};

    public static String getCreateSql(){
        return "CREATE TABLE IF NOT EXISTS "+TABLE_NAME +" ("+
                COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT ,"+
                COL_URL +" TEXT  ," +
                COL_IMG_URL +" TEXT," +
                COL_TITL +" TEXT UNIQUE," +
                COL_SUBTITLE + " TEXT ," +
                COL_CONTENT + " TEXT ," +
                COL_DATE +" TEXT );";
    }

    public static News load(Long id,String url,String title ){
        String where = "1 = 1 ";
        where += (id == null)?"":" AND "+COL_ID +" = "+id;
        where += (url == null)?"":" AND "+COL_URL +" = '"+url+"'";
        where += (title == null)?"":" AND "+COL_TITL+" = '"+title+"';";

        News res = null;


        Cursor c = MyApplication.dbr.query(TABLE_NAME,COLS,where,null,null,null,null);
        if(c.moveToFirst()){
            res = new News();
            res.setId(c.getInt(c.getColumnIndex(COL_ID)));
            res.setUrl(c.getString(c.getColumnIndex(COL_URL)));
            res.setTitle(c.getString(c.getColumnIndex(COL_TITL)));
            res.setSubTitle(c.getString(c.getColumnIndex(COL_SUBTITLE)));
            res.setDate(c.getString(c.getColumnIndex(COL_DATE)));
            res.setContent(c.getString(c.getColumnIndex(COL_CONTENT)));
            res.setImgUrl(c.getString(c.getColumnIndex(COL_IMG_URL)));
        }
        c.close();

        return res;
    }

    public static void deleteAll(){
        MyApplication.dbw.delete(TABLE_NAME,null,null);
    }

    public long save(){
        ContentValues cv = new ContentValues();
        cv.put(COL_URL,this.getUrl());
        cv.put(COL_IMG_URL,this.getImgUrl());
        cv.put(COL_TITL,this.getTitle());
        cv.put(COL_SUBTITLE,this.getSubTitle());

        long id = -1;
        try {
            id = MyApplication.dbw.insert(TABLE_NAME, null, cv);
        }catch (Exception e){

        }

        if(id>-1)
            this.setId(id);

        else {
            News news = News.load(null, null,this.getTitle());
            this.setId(news.getId());
        }

        return this.getId();

    }

    public void updateThis(){
        ContentValues cv = new ContentValues();
        cv.put(COL_CONTENT,this.getContent());
        cv.put(COL_DATE,this.getDate());

        MyApplication.dbw.update(TABLE_NAME,cv,COL_ID +"=?",new String[]{String.valueOf(this.getId())});
    }



    private long id;
    private String url;
    private String imgUrl;
    private String title;
    private String subTitle;
    private String date;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @Override
    public boolean equals(Object o) {
        News other = ((News) o);
        return this.getId() == other.getId();
    }
}
