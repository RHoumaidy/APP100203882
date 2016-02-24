package com.smartgateapps.englifootball.model;

/**
 * Created by Raafat on 09/11/2015.
 */
public class Player implements Comparable<Player> {

    private String playerId;
    private String playerName;
    private String playerUrl;
    private String teamName;
    private int pos;
    private int teamId;
    private String goals;
    private int headerType;
    private String montakhab;
    private String number;
    private String monImgUrl;

    public Player(){}



    public Player(String playerId, String playerName, String playerUrl) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.playerUrl = playerUrl;
    }

    public String getMonImgUrl() {
        return monImgUrl;
    }

    public void setMonImgUrl(String monImgUrl) {
        this.monImgUrl = monImgUrl;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMontakhab() {
        return montakhab;
    }

    public void setMontakhab(String montakhab) {
        this.montakhab = montakhab;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getHeaderType() {
        return headerType;
    }

    public void setHeaderType(int headerType) {
        this.headerType = headerType;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String  goals) {
        this.goals = goals;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerUrl() {
        return playerUrl;
    }

    public void setPlayerUrl(String playerUrl) {
        this.playerUrl = playerUrl;
    }


    @Override
    public boolean equals(Object o) {
        Player other = (Player)o;
        return this.getPlayerName().equalsIgnoreCase(other.getPlayerName());
    }

    @Override
    public int compareTo(Player another) {
        Player lhs = this;
        Player rhs = another;
        if(lhs.getPos() != rhs.getPos())
            return lhs.getPos() - rhs.getPos();
        if(lhs.getNumber() == null ||  lhs.getNumber().replaceAll("\\s","").equalsIgnoreCase("") )
            return -1;
        if(rhs.getNumber() == null || rhs.getNumber().replaceAll("\\s","").equalsIgnoreCase(""))
            return 1;
        int lhsN = Integer.parseInt(lhs.getNumber());
        int rhsN = Integer.parseInt(rhs.getNumber());
        return lhsN - rhsN;
    }
}
