package com.omar.tictactoexo.gamePlayers;

public class Users {
    private String player_uid;
    private boolean isOnline;
    private boolean isPlaying;
    private String cellID;
    String uid;

    public Users() {
    }

    public Users( String player_uid,boolean isOnline, boolean isPlaying , String cellID) {
        this.player_uid = player_uid;
        this.isOnline = isOnline;
        this.isPlaying = isPlaying;
        this.cellID = cellID;
    }

    public String getCellID() {
        return cellID;
    }

    public void setCellID(String cellID) {
        this.cellID = cellID;
    }

    public String getPlayer_uid() {
        return player_uid;
    }

    public void setPlayer_uid(String player_uid) {
        this.player_uid = player_uid;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
