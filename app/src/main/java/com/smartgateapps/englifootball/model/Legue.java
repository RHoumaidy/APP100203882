package com.smartgateapps.englifootball.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.smartgateapps.englifootball.engli.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raafat on 22/12/2015.
 */
public class Legue {

    public static final String TABLE_NAME = "LEGUES";
    public static final String COL_ID = "ID";
    public static final String COL_NAME = "COL_NAME";
    public static final String COL_URL = "BASE_URL";
    public static final String COL_NEW_URL = "NEWS_URL";
    public static final String COL_DATE = "LEAGUE_DATE";
    public static final String[] COLS = new String[]{COL_ID, COL_NAME,COL_URL,COL_NEW_URL,COL_DATE};

    public static String getCreateSql() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                COL_NAME + " TEXT ," +
                COL_URL +" TEXT ," +
                COL_NEW_URL+" TEXT," +
                COL_DATE +" TEXT);";
    }

    public static List<Legue> load(Long id) {
        String where = " 1 = 1 ";
        where += (id == null) ? "" : " AND " + COL_ID + " = "+id;


        Cursor c = MyApplication.dbr.query(TABLE_NAME, COLS, where, null, null, null, null);
        List<Legue> res = null;

        if (c.moveToFirst()) {
            res = new ArrayList<>();
            do {
                Legue legue = new Legue(
                        c.getLong(c.getColumnIndex(COL_ID)),
                        c.getString(c.getColumnIndex(COL_NAME)),
                        c.getString(c.getColumnIndex(COL_URL)),
                        c.getString(c.getColumnIndex(COL_NEW_URL)));
                res.add(legue);
            } while (c.moveToNext());
        }
        c.close();

        return res;
    }

    public static void deleteAll(){
        MyApplication.dbw.delete(TABLE_NAME,null,null);
    }

    public void update(){
        ContentValues cv = new ContentValues();
        cv.put(COL_DATE,this.getLeagueDate());
        cv.put(COL_NAME,this.getName());
        cv.put(COL_URL,this.getLeagueBaseUrl());
        cv.put(COL_NEW_URL,this.getNewsUrl());

        MyApplication.dbw.update(TABLE_NAME,cv,COL_ID +"=?",new String[]{String.valueOf(this.getId())});
    }

    public boolean save() {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID,this.getId());
        cv.put(COL_DATE,this.getLeagueDate());
        cv.put(COL_NAME,this.getName());
        cv.put(COL_URL,this.getLeagueBaseUrl());
        cv.put(COL_NEW_URL,this.getNewsUrl());

        try {
            return MyApplication.dbw.insert(TABLE_NAME, null, cv) > 0;
        } catch (Exception e) {
            //Error
        }
        return false;

    }

    private Long id;
    private String name;
    private String leagueBaseUrl;
    private String leagueDate;
    private String newsUrl;

    public Legue(Long id, String name,String leagueBaseUrl,String newsUrl) {
        this.id = id;
        this.name = name;
        this.leagueBaseUrl = leagueBaseUrl;
        this.newsUrl = newsUrl;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getLeagueDate() {
        return leagueDate;
    }

    public void setLeagueDate(String leagueDate) {
        this.leagueDate = leagueDate;
    }

    public String getLeagueBaseUrl() {
        return leagueBaseUrl;
    }

    public void setLeagueBaseUrl(String leagueBaseUrl) {
        this.leagueBaseUrl = leagueBaseUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        Legue other = (Legue) o;
        return this.getId() == other.getId();
    }
}
