package com.smartgateapps.englifootball.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;

import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.engli.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raafat on 04/11/2015.
 */
public class Team {

    public static final String TABLE_NAME = "TEAMS";
    public static final String COL_ID = "ID";
    public static final String COL_NAME = "NEAM";
    public static final String COL_LOGO = "TEAM_LOGO";
    public static final String COL_TEAM_URL = "TEAM_URL";
    public static final String[] COLS = new String[]{COL_ID, COL_NAME, COL_LOGO, COL_TEAM_URL};

    public static String getCreateSql() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                COL_NAME + " TEXT ," +
                COL_LOGO + " INTEGER ," +
                COL_TEAM_URL + " TEXT );";
    }


    public static Team load(Long id, String teamName) {

        String where = " 1 = 1 ";
        where += (id == null) ? "" : " AND " + COL_ID + " =? ";
        where += (teamName == null) ? "" : " AND " + COL_NAME + " =? ";
        List<String> args = new ArrayList<>();
        if (id != null)
            args.add(String.valueOf(id));
        if (teamName != null)
            args.add(teamName);

        Cursor c = MyApplication.dbr.query(TABLE_NAME, COLS, where, args.toArray(new String[args.size()]), null, null, null);

        Team res = new Team(teamName);
        if (c.moveToFirst()) {
            res.setTeamName(c.getString(c.getColumnIndex(COL_NAME)));
            res.setId(c.getInt(c.getColumnIndex(COL_ID)));
            res.setTeamLogo(c.getInt(c.getColumnIndex(COL_LOGO)));
            res.setTeamUrl(c.getString(c.getColumnIndex(COL_TEAM_URL)));
        }
        c.close();

        return res;
    }

    public boolean save() {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, this.getId());
        cv.put(COL_NAME, this.getTeamName());
        cv.put(COL_LOGO, this.getTeamLogo());
        cv.put(COL_TEAM_URL, this.getTeamUrl());

        try {
            TeamLeague teamLeague = new TeamLeague(this.id, this.leagueId);
            teamLeague.save();
            return MyApplication.dbw.insert(TABLE_NAME, null, cv) > 0;
        } catch (SQLException e) {
            //Error
        }
        return false;
    }

    public static void deleteAll() {
        try {
            MyApplication.dbw.delete(TABLE_NAME, null, null);
        } catch (SQLiteException e) {

        }
    }

    private int id;
    private String teamName;
    private int teamLogo;
    private String place;
    private String matchPlayed;
    private String matchWined;
    private String matchDrawed;
    private String matchLosed;
    private String points;
    private String teamUrl;
    private String countryFlageUrl;
    private int leagueId;
    private List<Player> players;

    public Team(String teamName) {
        this.setTeamLogo(R.mipmap.t_unknown);
        this.setTeamName(teamName);
    }


    public String getCountryFlageUrl() {
        return countryFlageUrl;
    }

    public void setCountryFlageUrl(String countryFlageUrl) {
        this.countryFlageUrl = countryFlageUrl;
    }

    public String getTeamUrl() {
        return teamUrl;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }

    public void setTeamUrl(String teamUrl) {
        this.teamUrl = teamUrl;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getMatchPlayed() {
        return matchPlayed;
    }

    public void setMatchPlayed(String matchPlayed) {
        this.matchPlayed = matchPlayed;
    }

    public String getMatchWined() {
        return matchWined;
    }

    public void setMatchWined(String matchWined) {
        this.matchWined = matchWined;
    }

    public String getMatchDrawed() {
        return matchDrawed;
    }

    public void setMatchDrawed(String matchDrawed) {
        this.matchDrawed = matchDrawed;
    }

    public String getMatchLosed() {
        return matchLosed;
    }

    public void setMatchLosed(String matchLosed) {
        this.matchLosed = matchLosed;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getTeamLogo() {
        return teamLogo;
    }

    public void setTeamLogo(int teamLogo) {
        this.teamLogo = teamLogo;
    }


    @Override
    public boolean equals(Object o) {
        Team other = (Team) o;
        return this.getId() == (other.getId());
    }
}
