package com.smartgateapps.englifootball.model;

/**
 * Created by Raafat on 29/12/2015.
 */
public class TeamTransformation {

    public static final String TABLE_NAME = "TEAM_TRANSFORMATIONS";
    public static final String COL_ID  = "ID";
    public static final String COL_DATE = "DATE";
    public static final String COL_PLAYER_NAME = "P_NAME";
    public static final String COL_TEAM_ID = "TEAM_ID";
    public static final String COL_TEAM_TO = "TEAM_TO_NAME";
    public static final String COL_TYPE = "TYPE";



    private int teamId;
    private String date;
    private String pName;
    private String oTeam;
    private String type;
    private String oTeamCUrl;


    public String getoTeamCUrl() {
        return oTeamCUrl;
    }

    public void setoTeamCUrl(String oTeamCUrl) {
        this.oTeamCUrl = oTeamCUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getoTeam() {
        return oTeam;
    }

    public void setoTeam(String oTeam) {
        this.oTeam = oTeam;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
