package com.server;

import java.io.Serializable;

class SingleScore implements Serializable {
    private String playersName;
    private int score;

    public SingleScore() {
        this.playersName = "Not set yet!";
        this.score = 0;
    }
    
    void set(String playersName, int score) {
        this.playersName = playersName;
        this.score = score;
    }
    
    public String getName() {
        return playersName;
    }

    public int getScore(){
        return score;
    }
    boolean compareTo(SingleScore input) {
        return this.score < input.score;
    }
    
    
}