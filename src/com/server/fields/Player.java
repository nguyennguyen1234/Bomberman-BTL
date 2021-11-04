package com.server.fields;

import com.server.Consts;
import java.util.Map;
import java.util.HashMap;

public class Player extends Field {
    private String nick;
    private float speed;
    private int nBombs;
    private int rangeOfBomb;
    private boolean isAlive;
    private int score;
    private int idColor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public Player(int x, int y, boolean destroyable, String nick, int id) {
        super(x, y, destroyable);
        this.nick = nick;
        this.isAlive = true;
        this.name = "Player" + Integer.toString(id+1);
        System.out.println("Stworzylem gracza: " + this.name);
        this.speed = 10;
        this.nBombs = 1;
        this.rangeOfBomb = 1;
        this.score = 0;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getNick(){
        return nick;
    }

    public int getRangeOfBomb() {
        return rangeOfBomb;
    }

    public int getNBombs(){
        return nBombs;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void move(int diffX, int diffY) {
        this.x += diffX;
        this.y += diffY;
    }

    public void kill() {
        if (isAlive()){
            System.out.print("ZGON!");
            isAlive = false;
        }
    }

    public void incSpeed() {
        if (this.speed < Consts.MAX_SPEED) {
            this.speed++;
        }
    }

    public void incNBombs() {
        if (this.nBombs < Consts.MAX_N_BOMBS) {
            this.nBombs++;
        }
    }

    public void incRange() {
        if (this.rangeOfBomb < Consts.MAX_RANGE_OF_BOMB) {
            this.rangeOfBomb++;
        }
    }

    public void decNBombs() {
        nBombs--;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void incScore(int diff){
        this.score += diff;
        System.out.println("NOWY SKOR: \t\t\t\t\t\t" + Integer.toString(this.score));
    }
}
