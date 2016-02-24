package com.smartgateapps.englifootball.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.smartgateapps.englifootball.engli.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raafat on 19/01/2016.
 */
public class Stage {
    public static final String TABLE_NAME = "STAGES";
    public static final String COL_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_URL = "URL";
    public static final String COL_LEAGUE_ID = "LEAGUE_ID";
    public static final String[] COLS = new String[]
            {COL_ID, COL_NAME, COL_URL, COL_LEAGUE_ID};

    public static String getCreateSql() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                COL_NAME + " TEXT," +
                COL_URL + " TEXT UNIQUE ," +
                COL_LEAGUE_ID + " INTEGER );";
    }

    public static Stage load(Long id, String name, String url) {
        String where = " 1=1 ";
        where += (id == null) ? "" : " AND " + COL_ID + "=" + id;
        where += (name == null) ? "" : " AND " + COL_NAME + "='" + name + "'";
        where += (url == null) ? "" : " AND " + COL_URL + "='" + url + "'";

        Stage res = null;
        Cursor c = MyApplication.dbr.query(TABLE_NAME, COLS, where, null, null, null, null);
        if (c.moveToFirst())
            res = new Stage(
                    c.getString(c.getColumnIndex(COL_NAME)),
                    c.getString(c.getColumnIndex(COL_URL)),
                    c.getLong(c.getColumnIndex(COL_LEAGUE_ID))
            );
        c.close();

        return res;
    }

    public static List<Stage> getAllStagesForLeague(Long leagueId) {
        Cursor c = MyApplication.dbr.query(TABLE_NAME, COLS, COL_LEAGUE_ID + "=?", new String[]{String.valueOf(leagueId)}, null, null, null);
        List<Stage> res = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Stage item = new Stage(
                        c.getString(c.getColumnIndex(COL_NAME)),
                        c.getString(c.getColumnIndex(COL_URL)),
                        c.getLong(c.getColumnIndex(COL_LEAGUE_ID))
                );
                res.add(item);

            } while (c.moveToNext());
        }
        c.close();

        return res;
    }

    public List<Match> getAllMatches() {
        List<Match> res = new ArrayList<>();
        Cursor c = MyApplication.dbr.query(Match.TABLE_NAME, Match.COLS, Match.COL_STG_ID + "=?",
                new String[]{String.valueOf(this.getUrl())}, null, null, Match.COL_DATE_TIME);

        if (c.moveToFirst()) {
            do {
                Match item = new Match();
                item.setTeamL(Team.load(null, c.getString(c.getColumnIndex(Match.COL_TEAML_ID))));
                item.setTeamR(Team.load(null, c.getString(c.getColumnIndex(Match.COL_TEAMR_ID))));
                item.setResultL(c.getString(c.getColumnIndex(Match.COL_RESULTL)));
                item.setResultR(c.getString(c.getColumnIndex(Match.COL_RESULTR)));
                item.setDateTime(c.getLong(c.getColumnIndex(Match.COL_DATE_TIME)));
                item.sethId(c.getInt(c.getColumnIndex(Match.COL_H_ID)));
                item.setIsHeader(c.getInt(c.getColumnIndex(Match.COL_IS_HEADER)) == 1);
                item.setHasBeenUpdated(c.getInt(c.getColumnIndex(Match.COL_HAS_UPDATED)) == 1);
                item.setNotifyMe(c.getInt(c.getColumnIndex(Match.COL_NOTIFY_ME)) == 1);
                item.setNotifyDateTime(c.getLong(c.getColumnIndex(Match.COL_NOTIFY_DATE_TIME)));
                item.setStage(this);
                item.setId(c.getLong(c.getColumnIndex(Match.COL_ID)));

                res.add(item);
            }while (c.moveToNext());
        }
        c.close();
        return res;
    }

    public void save() {
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, this.getName());
        cv.put(COL_URL, this.getUrl());
        cv.put(COL_LEAGUE_ID, this.getLeagueId());
        try {
            this.id = MyApplication.dbw.insert(TABLE_NAME, null, cv);
        } catch (SQLException e) {
            Log.e(TABLE_NAME, e.getMessage());
        }
    }
    public static void deleteAll(){
        try {
            MyApplication.dbw.delete(TABLE_NAME, null, null);
        }catch (SQLiteException e){

        }
    }

    private Long id;
    private String name;
    private String url;
    private Long leagueId;

    public Stage(String name, String url, Long leagueId) {
        this.name = name;
        this.url = url;
        this.leagueId = leagueId;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        Stage other = (Stage) o;
        return this.getName().equalsIgnoreCase(other.getName());
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
