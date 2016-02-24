package com.smartgateapps.englifootball.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.smartgateapps.englifootball.engli.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raafat on 07/01/2016.
 */
public class LeaguNews {
    public static final String TABLE_NAME = "LEAGUE_NEWS";
    public static final String COL_ID = "ID";
    public static final String COL_LEAGUE_ID = "LEAUGE_ID";
    public static final String COL_NEWS_ID = "NEWS_URL";
    public static final String COL_DATE_OF_INSERTION = "INSERTION_DATE";
    public static final String COL_SEEN = "IS_SEEN";

    public static final String[] COLS = new String[]{COL_ID, COL_LEAGUE_ID, COL_NEWS_ID, COL_DATE_OF_INSERTION, COL_SEEN};


    public static String getCreateSql() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COL_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                COL_LEAGUE_ID + " INTEGER ," +
                COL_NEWS_ID + " INTEGER ," +
                COL_DATE_OF_INSERTION + " DATETIME ," +
                COL_SEEN + " BOOLEAN ," +
                "UNIQUE (" + COL_LEAGUE_ID + "," + COL_NEWS_ID + "));";
    }

    public static LeaguNews load(Long leaguId, Long newsId) {
        LeaguNews res = new LeaguNews();

        String where = "1 = 1";
        where += (leaguId == null) ? "" : " AND " + COL_LEAGUE_ID + " =  " + leaguId;
        where += (newsId == null) ? "" : " AND " + COL_NEWS_ID + " = " + newsId;

        Cursor c = MyApplication.dbr.query(TABLE_NAME, COLS, where, null, null, null, null);
        if (c.moveToFirst()) {
            res.setId(c.getInt(c.getColumnIndex(COL_ID)));
            res.setLeaguId(c.getInt(c.getColumnIndex(COL_LEAGUE_ID)));
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
        cv.put(COL_LEAGUE_ID, this.getLeaguId());
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

    public static List<News> getAllNewsForLeagu(int leaguId, int pageNb) {
        List<News> res = new ArrayList<>();

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_LEAGUE_ID + " = " + leaguId +
                " ORDER BY " + COL_DATE_OF_INSERTION + " DESC " +
                " LIMIT " + MyApplication.pageSize + " OFFSET " + (pageNb - 1) * MyApplication.pageSize + ";";
        Cursor c = MyApplication.dbr.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {

                News news = News.load(c.getLong(c.getColumnIndex(COL_NEWS_ID)), null, null);
                LeaguNews.load((long)(leaguId),(news.getId()));


                if (news != null)
                    res.add(news);
            } while (c.moveToNext());
        }
        c.close();

        return res;
    }

    public static List<News> getAllUseenNewsForLeagu(int leaguId) {
        List<News> res = new ArrayList<>();

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_LEAGUE_ID + " = " + leaguId + " AND "+COL_SEEN +" = 0"+
                " ORDER BY " + COL_DATE_OF_INSERTION + " DESC " + ";";
        Cursor c = MyApplication.dbr.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                News news = News.load(c.getLong(c.getColumnIndex(COL_NEWS_ID)), null, null);
                LeaguNews.load((long)(leaguId),(news.getId()));

                //add news to Main NewsList;
                LeaguNews leaguNews;
                leaguNews = new LeaguNews();
                leaguNews.setLeaguId(0);
                leaguNews.setIsSeen(false);
                leaguNews.setPageIdx(1);
                leaguNews.setNewsId(news.getId());
                leaguNews.save();
                if (news != null)
                    res.add(news);
            } while (c.moveToNext());
        }
        c.close();

        return res;
    }


    private int id;
    private long leaguId;
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

    public long getLeaguId() {
        return leaguId;
    }

    public void setLeaguId(long leaguId) {
        this.leaguId = leaguId;
    }

    public long getNewsId() {
        return newsId;
    }

    public void setNewsId(long newsId) {
        this.newsId = newsId;
    }
}
