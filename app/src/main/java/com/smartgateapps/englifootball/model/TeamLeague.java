package com.smartgateapps.englifootball.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;

import com.smartgateapps.englifootball.engli.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raafat on 24/12/2015.
 */
public class TeamLeague {

    public static final String TABLE_NAME = "TEAM_LEAGUE";
    public static final String COL_ID ="ID";
    public static final String COL_TEAM_ID = "TEAM_ID";
    public static final String COL_LEAGUE_ID = "LEAGUE_ID";
    public static final String[] COLS = new String[]{COL_TEAM_ID, COL_LEAGUE_ID};

    public static String getCreateSql(){
        return "CREATE TABLE IF NOT EXISTS "+TABLE_NAME +" ( "+
                COL_TEAM_ID +" INTEGER ,"+
                COL_LEAGUE_ID + " INTEGER ,"+
                "PRIMARY KEY ("+COL_LEAGUE_ID+","+COL_TEAM_ID+"));";

    }

    public boolean save(){
        ContentValues cv = new ContentValues();
        cv.put(COL_LEAGUE_ID,this.getLeagueId());
        cv.put(COL_TEAM_ID,this.getTeamId());

        try {
            return MyApplication.dbw.insert(TABLE_NAME,null,cv)>0;
        }catch (SQLiteConstraintException ex){

        }

        return false;

    }


    public static List<Team> getAllTeamsForLeagu(int leagueId){
        Cursor c = MyApplication.dbr.query(TABLE_NAME,COLS,COL_LEAGUE_ID +" =? ",new String[]{String.valueOf(leagueId)},null,null,null);
        List<Team> res = null;
        if(c.moveToFirst()){
            res = new ArrayList<>();
            do {
                Team team  = Team.load(c.getLong(c.getColumnIndex(COL_TEAM_ID)),null);
                res.add(team);
            }while (c.moveToNext());
        }
        c.close();

        return res;
    }

    public static void deleteAll(){
        try {
            MyApplication.dbw.delete(TABLE_NAME, null, null);
        }catch (SQLiteException e){

        }
    }


    private int teamId;
    private int leagueId;

    public TeamLeague(int teamId, int leagueId) {
        this.teamId = teamId;
        this.leagueId = leagueId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }
}
