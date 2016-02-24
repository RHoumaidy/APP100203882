package com.smartgateapps.englifootball.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.smartgateapps.englifootball.engli.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raafat on 07/01/2016.
 */
public class TeamNews {
    public static final String TABLE_NAME = "TEAM_NEWS";
    public static final String COL_ID = "ID";
    public static final String COL_TEAM_ID = "TEAM_ID";
    public static final String COL_NEWS_ID = "NEWS_ID";
    public static final String COL_DATE_OF_INSERTION = "INSERTION_DATE";
    public static final String COL_SEEN = "IS_SEEN";

    public static final String[] COLS = new String[]{COL_ID, COL_TEAM_ID, COL_NEWS_ID, COL_DATE_OF_INSERTION, COL_SEEN};


    public static String getCreateSql() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COL_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                COL_TEAM_ID + " INTEGER ," +
                COL_NEWS_ID + " INTEGER ," +
                COL_DATE_OF_INSERTION + " DATETIME ," +
                COL_SEEN + " BOOLEAN ," +
                "UNIQUE (" + COL_TEAM_ID + "," + COL_NEWS_ID + "));";
    }

    public static TeamNews load(Long teamId, Long newsId) {
        TeamNews res = new TeamNews();

        String where = "1 = 1";
        where += (teamId == null) ? "" : " AND " + COL_TEAM_ID + " =  " + teamId;
        where += (newsId == null) ? "" : " AND " + COL_NEWS_ID + " = " + newsId;

        Cursor c = MyApplication.dbr.query(TABLE_NAME, COLS, where, null, null, null, null);
        if (c.moveToFirst()) {
            res.setId(c.getInt(c.getColumnIndex(COL_ID)));
            res.setTeamId(c.getInt(c.getColumnIndex(COL_TEAM_ID)));
            res.setNewsId(c.getInt(c.getColumnIndex(COL_NEWS_ID)));
            res.setIsSeen(true);
            res.updateThis();

        }
        c.close();

        return res;
    }

    public void updateThis() {
        ContentValues cv = new ContentValues();
        cv.put(COL_SEEN, this.getIsSeen());

        MyApplication.dbw.update(TABLE_NAME,cv,COL_ID +" =? ",new String[]{String.valueOf(this.getId())});

    }

    public boolean save() {
        ContentValues cv = new ContentValues();
        cv.put(COL_TEAM_ID, this.getTeamId());
        cv.put(COL_NEWS_ID, this.getNewsId());
        cv.put(COL_DATE_OF_INSERTION, MyApplication.getCurretnDateTime() / pageIdx);
        cv.put(COL_SEEN, this.getIsSeen());

        long id = -1;
        try {
            id = MyApplication.dbw.insert(TABLE_NAME, null, cv);
        } catch (Exception e) {

        }
        return id > -1;
    }

    public static List<News> getAllNewsForTeam(int leaguId, int pageNb) {
        List<News> res = new ArrayList<>();

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_TEAM_ID + " = " + leaguId +
                " ORDER BY " + COL_DATE_OF_INSERTION + " DESC " +
                " LIMIT " + MyApplication.pageSize + " OFFSET " + (pageNb - 1) * MyApplication.pageSize + ";";
        Cursor c = MyApplication.dbr.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {

                News news = News.load(c.getLong(c.getColumnIndex(COL_NEWS_ID)), null, null);
                TeamNews leaguNews = TeamNews.load(new Long(leaguId), new Long(news.getId()));
                if (news != null)
                    res.add(news);
            } while (c.moveToNext());
        }
        c.close();

        return res;
    }

    public static List<News> getAllUseenNewsForTeam(int leaguId) {
        List<News> res = new ArrayList<>();

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_TEAM_ID + " = " + leaguId + " AND "+COL_SEEN +" = 0"+
                " ORDER BY " + COL_DATE_OF_INSERTION + " DESC " + ";";
        Cursor c = MyApplication.dbr.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                News news = News.load(c.getLong(c.getColumnIndex(COL_NEWS_ID)), null, null);
                TeamNews teamNews = TeamNews.load(new Long(leaguId), new Long(news.getId()));
                if (news != null)
                    res.add(news);
            } while (c.moveToNext());
        }
        c.close();

        return res;
    }


    private int id;
    private long teamId;
    private long newsId;
    private int pageIdx;
    private short isSeen;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public short getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen ? (short) 1 : (short) 0;
    }

    public int getPageIdx() {
        return pageIdx;
    }

    public void setPageIdx(int pageIdx) {
        this.pageIdx = pageIdx;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public long getNewsId() {
        return newsId;
    }

    public void setNewsId(long newsId) {
        this.newsId = newsId;
    }
}
