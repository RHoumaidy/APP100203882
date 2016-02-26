package com.smartgateapps.englifootball.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.smartgateapps.englifootball.engli.MyApplication;
import com.smartgateapps.englifootball.services.UpdateMatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raafat on 16/12/2015.
 */
public class Match {

    public static final String TABLE_NAME = "MATCHES";
    public static final String COL_ID = "ID";
    public static final String COL_TEAML_ID = "TEAM_L_ID";
    public static final String COL_TEAMR_ID = "TEAM_R_ID";
    public static final String COL_RESULTR = "RESULT_R";
    public static final String COL_RESULTL = "RESULT_L";
    public static final String COL_DATE_TIME = "DATE_TIME";
    public static final String COL_H_ID = "H_ID";
    public static final String COL_STG_ID = "STG_ID";
    public static final String COL_LEAGUE_ID = "LEAGUE_ID";
    public static final String COL_IS_HEADER = "IS_HEADER";
    public static final String COL_NOTIFY_ME = "NOTIFY_ME";
    public static final String COL_HAS_UPDATED = "HAS_UPDATED";
    public static final String COL_NOTIFY_DATE_TIME = "NOTIFY_DATE";
    public static final String[] COLS = new String[]
            {COL_ID, COL_TEAML_ID, COL_TEAMR_ID, COL_RESULTL, COL_RESULTR, COL_DATE_TIME, COL_H_ID,
                    COL_STG_ID, COL_IS_HEADER, COL_LEAGUE_ID, COL_NOTIFY_ME, COL_HAS_UPDATED, COL_NOTIFY_DATE_TIME};

    public static String getCreateSql() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                COL_TEAML_ID + " TEXT ," +
                COL_TEAMR_ID + " TEXT ," +
                COL_RESULTL + " TEXT ," +
                COL_RESULTR + " TEXT ," +
                COL_DATE_TIME + " DATETIME ," +
                COL_H_ID + " INTEGER ," +
                COL_STG_ID + " TEXT ," +
                COL_LEAGUE_ID + " INTEGER DEFAULT NULL," +
                COL_IS_HEADER + " BOOLEAN DEFAULT 0 ," +
                COL_NOTIFY_ME + " BOOLEAN DEFAULT 0," +
                COL_HAS_UPDATED + " BOOLEAN DEFAULT 0," +
                COL_NOTIFY_DATE_TIME + " DATETIME );";
    }

    public void save() {
        ContentValues cv = new ContentValues();

        cv.put(COL_ID, this.getId());
        cv.put(COL_TEAML_ID, this.getTeamL().getTeamName());
        cv.put(COL_TEAMR_ID, this.getTeamR().getTeamName());
        cv.put(COL_RESULTL, this.getResultL());
        cv.put(COL_RESULTR, this.getResultR());
        cv.put(COL_DATE_TIME, this.getDateTime());
        cv.put(COL_H_ID, this.gethId());
        cv.put(COL_STG_ID, this.getStage().getUrl());
        if (this.isHeader() != null)
            cv.put(COL_IS_HEADER, this.isHeader());
        if (this.isNotifyMe() != null)
            cv.put(COL_NOTIFY_ME, this.isNotifyMe());
        if (this.isHasBeenUpdated() != null)
            cv.put(COL_HAS_UPDATED, this.isHasBeenUpdated());
        cv.put(COL_NOTIFY_DATE_TIME, this.getNotifyDateTime());

        if (this.getLeagueId() != null)
            cv.put(COL_LEAGUE_ID, this.getLeagueId());

        try {
            MyApplication.dbw.insert(TABLE_NAME, null, cv);
        } catch (SQLException e) {
            Log.e(TABLE_NAME, e.getMessage());
        }
    }

    public void update() {
        ContentValues cv = new ContentValues();
        if (this.getResultL() != null)
            cv.put(COL_RESULTL, this.getResultL());
        if (this.getResultR() != null)
            cv.put(COL_RESULTR, this.getResultR());
        if (this.getTeamL() != null)
            cv.put(COL_TEAML_ID, this.getTeamL().getTeamName());
        if (this.getTeamR() != null)
            cv.put(COL_TEAMR_ID, this.getTeamR().getTeamName());
        if (this.getDateTime() != null)
            cv.put(COL_DATE_TIME, this.getDateTime());
        if (this.gethId() != null)
            cv.put(COL_H_ID, this.gethId());
        if (this.getStage() != null)
            cv.put(COL_STG_ID, this.getStage().getUrl());
        if (this.isHeader() != null)
            cv.put(COL_IS_HEADER, this.isHeader());
        if (this.isNotifyMe() != null)
            cv.put(COL_NOTIFY_ME, this.isNotifyMe());
        if (this.isHasBeenUpdated() != null)
            cv.put(COL_HAS_UPDATED, this.isHasBeenUpdated());
        if (this.getNotifyDateTime() != null)
            cv.put(COL_NOTIFY_DATE_TIME, this.getNotifyDateTime());

        if (this.getLeagueId() != null)
            cv.put(COL_LEAGUE_ID, this.getLeagueId());

        try {
            MyApplication.dbw.update(TABLE_NAME, cv, COL_ID + "=?", new String[]{String.valueOf(this.getId())});
        } catch (SQLException e) {
            Log.e(TABLE_NAME, e.getMessage());
        }
    }

    public static Match load(Long id) {
        Match res = null;
        Cursor c = MyApplication.dbr.query(TABLE_NAME, COLS, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (c.moveToFirst()) {
            res = new Match();
            res.setId(id);
            res.setDateTime(c.getLong(c.getColumnIndex(COL_DATE_TIME)));
            res.setStage(Stage.load(null, c.getString(c.getColumnIndex(COL_STG_ID)), null));
            res.sethId(c.getInt(c.getColumnIndex(COL_H_ID)));
            res.setIsHeader(c.getInt(c.getColumnIndex(COL_IS_HEADER)) == 1);
            res.setLeagueId(c.getInt(c.getColumnIndex(COL_LEAGUE_ID)));
            res.setResultL(c.getString(c.getColumnIndex(COL_RESULTL)));
            res.setResultR(c.getString(c.getColumnIndex(COL_RESULTR)));
            res.setTeamL(c.getString(c.getColumnIndex(COL_TEAML_ID)));
            res.setTeamR(c.getString(c.getColumnIndex(COL_TEAMR_ID)));
            res.setHasBeenUpdated(c.getInt(c.getColumnIndex(COL_HAS_UPDATED)) == 1);
            res.setNotifyMe(c.getInt(c.getColumnIndex(COL_NOTIFY_ME)) == 1);
            res.setNotifyDateTime(c.getLong(c.getColumnIndex(COL_NOTIFY_DATE_TIME)));
        }
        c.close();
        return res;
    }

    public static List<Match> getAllUnUpdatedMatches() {
        List<Match> resL = new ArrayList<>();
        Cursor c = MyApplication.dbr.query(TABLE_NAME, COLS, COL_HAS_UPDATED + "=?",
                new String[]{String.valueOf(false)}, null, null, null);
        if (c.moveToFirst()) {
            do {
                Match res = new Match();
                res.setId(c.getLong(c.getColumnIndex(COL_ID)));
                res.setDateTime(c.getLong(c.getColumnIndex(COL_DATE_TIME)));
                res.setStage(Stage.load(null, c.getString(c.getColumnIndex(COL_STG_ID)), null));
                res.sethId(c.getInt(c.getColumnIndex(COL_H_ID)));
                res.setIsHeader(c.getInt(c.getColumnIndex(COL_IS_HEADER)) == 1);
                res.setLeagueId(c.getInt(c.getColumnIndex(COL_LEAGUE_ID)));
                res.setResultL(c.getString(c.getColumnIndex(COL_RESULTL)));
                res.setResultR(c.getString(c.getColumnIndex(COL_RESULTR)));
                res.setTeamL(c.getString(c.getColumnIndex(COL_TEAML_ID)));
                res.setTeamR(c.getString(c.getColumnIndex(COL_TEAMR_ID)));
                res.setHasBeenUpdated(c.getInt(c.getColumnIndex(COL_HAS_UPDATED)) == 1);
                res.setNotifyMe(c.getInt(c.getColumnIndex(COL_NOTIFY_ME)) == 1);
                res.setNotifyDateTime(c.getLong(c.getColumnIndex(COL_NOTIFY_DATE_TIME)));

                resL.add(res);
            } while (c.moveToNext());

        }
        c.close();
        return resL;
    }

    public static List<Match> getAllNextMatches() {
        List<Match> resL = new ArrayList<>();
        Cursor c = MyApplication.dbr.query(TABLE_NAME, COLS, COL_DATE_TIME + ">=?",
                new String[]{String.valueOf(MyApplication.getCurretnDateTime())}, null, null, null);
        if (c.moveToFirst()) {
            do {
                Match res = new Match();
                res.setId(c.getLong(c.getColumnIndex(COL_ID)));
                res.setDateTime(c.getLong(c.getColumnIndex(COL_DATE_TIME)));
                res.setStage(Stage.load(null, c.getString(c.getColumnIndex(COL_STG_ID)), null));
                res.sethId(c.getInt(c.getColumnIndex(COL_H_ID)));
                res.setIsHeader(c.getInt(c.getColumnIndex(COL_IS_HEADER)) == 1);
                res.setLeagueId(c.getInt(c.getColumnIndex(COL_LEAGUE_ID)));
                res.setResultL(c.getString(c.getColumnIndex(COL_RESULTL)));
                res.setResultR(c.getString(c.getColumnIndex(COL_RESULTR)));
                res.setTeamL(c.getString(c.getColumnIndex(COL_TEAML_ID)));
                res.setTeamR(c.getString(c.getColumnIndex(COL_TEAMR_ID)));
                res.setHasBeenUpdated(c.getInt(c.getColumnIndex(COL_HAS_UPDATED)) == 1);
                res.setNotifyMe(c.getInt(c.getColumnIndex(COL_NOTIFY_ME)) == 1);
                res.setNotifyDateTime(c.getLong(c.getColumnIndex(COL_NOTIFY_DATE_TIME)));

                resL.add(res);
            } while (c.moveToNext());

        }
        c.close();

        return resL;
    }

    public static void deleteAll() {
        try {
            MyApplication.dbw.delete(TABLE_NAME, null, null);
        } catch (SQLiteException e) {

        }
    }

    private Long id;
    private Team teamL;
    private Team teamR;
    private Long dateTime;
    private String resultR;
    private String resultL;
    private Integer hId;
    private Stage stage;
    private Integer leagueId;
    private Boolean isHeader;
    private Boolean notifyMe;
    private Boolean hasBeenUpdated;
    private Long notifyDateTime;

    public Long getNotifyDateTime() {
        return notifyDateTime;
    }

    public void setNotifyDateTime(Long notifyDateTime) {
        this.notifyDateTime = notifyDateTime;
    }

    public Boolean isNotifyMe() {
        return notifyMe;
    }

    public void setNotifyMe(Boolean notifyMe) {
        this.notifyMe = notifyMe;
    }

    public Boolean isHasBeenUpdated() {
        return hasBeenUpdated;
    }

    public void setHasBeenUpdated(Boolean hasBeenUpdated) {
        this.hasBeenUpdated = hasBeenUpdated;
    }

    public Match() {
        this.leagueId = null;
    }

    public String getMatchUrl() {
        return "?m=" + this.getId();
    }

    public void registerMatchUpdateDate(long dateTime) {

        Intent updateMatchIntent = new Intent(MyApplication.APP_CTX, UpdateMatch.class);
        updateMatchIntent.putExtra("MATCH_ID", this.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApplication.APP_CTX, this.getId().intValue(), updateMatchIntent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
            pendingIntent.cancel();
        pendingIntent = PendingIntent.getBroadcast(MyApplication.APP_CTX, this.getId().intValue(), updateMatchIntent, PendingIntent.FLAG_ONE_SHOT);

        MyApplication.alarmManager.set(AlarmManager.RTC_WAKEUP, dateTime, pendingIntent);

    }

    public void registerMatchUpdateFirstTime() {
        Intent updateMatchIntent = new Intent(MyApplication.APP_CTX,UpdateMatch.class);
        updateMatchIntent.putExtra("MATCH_ID", this.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApplication.APP_CTX, this.getId().intValue(), updateMatchIntent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
            pendingIntent.cancel();

        pendingIntent = PendingIntent.getBroadcast(MyApplication.APP_CTX, this.getId().intValue(), updateMatchIntent, PendingIntent.FLAG_ONE_SHOT);

        MyApplication.alarmManager.set(AlarmManager.RTC_WAKEUP, this.getNotifyDateTime(), pendingIntent);
    }

    public int matchProgress() {
        Long currTime = MyApplication.getCurretnDateTime();

        if (this.isHasBeenUpdated())
            return -1;
        if (this.getDateTime() > currTime)
            return 1;
        return 0;
    }

    public Integer getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Integer leagueId) {
        this.leagueId = leagueId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Boolean isHeader() {
        return isHeader;
    }

    public void setIsHeader(Boolean isHeader) {
        this.isHeader = isHeader;
    }


    public String getResultL() {
        return resultL;
    }

    public void setResultL(String resultL) {
        this.resultL = resultL;
    }

    public Integer gethId() {
        return hId;
    }

    public void sethId(int hId) {
        this.hId = hId;
    }


    public Team getTeamL() {
        return teamL;
    }

    public void setTeamL(Team teamL) {
        this.teamL = teamL;
    }

    public void setTeamL(String teamL) {
        this.teamL = Team.load(null, teamL);
    }

    public Team getTeamR() {
        return teamR;
    }

    public void setTeamR(Team teamR) {
        this.teamR = teamR;
    }

    public void setTeamR(String teamR) {
        this.teamR = Team.load(null, teamR);
    }

    public String getResultR() {
        return resultR;
    }

    public void setResultR(String resultR) {
        this.resultR = resultR;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }
}
