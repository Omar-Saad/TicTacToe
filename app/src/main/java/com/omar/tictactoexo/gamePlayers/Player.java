package com.omar.tictactoexo.gamePlayers;

public class Player {
    private String name;
    private int score ;
    private boolean isWinner;

    public Player(String name , int score) {
        this.name = name;
        this.score = score;
        isWinner = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }
}
