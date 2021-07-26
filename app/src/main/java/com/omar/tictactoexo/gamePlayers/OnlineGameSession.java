package com.omar.tictactoexo.gamePlayers;

import com.omar.tictactoexo.firbasehelper.FirebaseDatabaseHelper;

import java.util.ArrayList;

public class OnlineGameSession {
    private final FirebaseDatabaseHelper firebaseDatabaseHelper;
    private String gameID;
   private int activePlayer;
    private String cellID;
    private int noOfPlayers;

    public OnlineGameSession() {
        firebaseDatabaseHelper = new FirebaseDatabaseHelper();
    }

    public OnlineGameSession(String gameID, int activePlayer, String cellID, int noOfPlayers) {
        firebaseDatabaseHelper = new FirebaseDatabaseHelper();
        this.gameID = gameID;
        this.activePlayer = activePlayer;
        this.cellID = cellID;
        this.noOfPlayers = noOfPlayers;
    }

    public OnlineGameSession createGame(Users user){
        final OnlineGameSession[] game = new OnlineGameSession[1];
        firebaseDatabaseHelper.createGameSession(new FirebaseDatabaseHelper.GameDataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<OnlineGameSession> onlineGameSessions, ArrayList<String> keys) {

            }

            @Override
            public void DataIsInserted(String key, OnlineGameSession session) {
                game[0] =session;
            }

            @Override
            public void DataIsUpdated( ) {

            }

            @Override
            public void SingleDataIsLoaded(OnlineGameSession onlineGameSessions) {

            }

            @Override
            public void DataIsDeleted() {

            }
        }, user);
        return game[0];
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(int activePlayer) {
        this.activePlayer = activePlayer;
    }

    public String getCellID() {
        return cellID;
    }

    public void setCellID(String cellID) {
        this.cellID = cellID;

    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public int getNoOfPlayers() {
        return noOfPlayers;
    }

    public void setNoOfPlayers(int noOfPlayers) {
        this.noOfPlayers = noOfPlayers;
    }


}
