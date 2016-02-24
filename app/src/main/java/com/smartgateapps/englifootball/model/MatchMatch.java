package com.smartgateapps.englifootball.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raafat on 15/01/2016.
 */
public class MatchMatch {
    private String league;
    private List<Match> matches;
    private int hdId;
    private String leagueImageUrl;

    public String getLeagueImageUrl() {
        return leagueImageUrl;
    }

    public void setLeagueImageUrl(String leagueImageUrl) {
        this.leagueImageUrl = leagueImageUrl;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public List<Match> getMatches() {
        return new ArrayList<>(this.matches);
    }

    public void setMatches(List<Match> matches) {
        this.matches = new ArrayList<>(matches);
    }

    public int getHdId() {
        return hdId;
    }

    public void setHdId(int hdId) {
        this.hdId = hdId;
    }
}
