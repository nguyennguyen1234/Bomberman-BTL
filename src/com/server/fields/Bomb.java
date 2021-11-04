package com.server.fields;

import com.server.Consts;
import com.server.Controllers.LogicController;

public class Bomb extends Field {
    private int range;
    private long startTime;
    private Player ownerOfBomb;
    private LogicController map;

    public Bomb(int x, int y, boolean destroyable, Player player) {
        super(x, y, destroyable);
        this.startTime = System.currentTimeMillis();
        this.name = "Bomb";
        this.ownerOfBomb = player;
        this.range = player.getRangeOfBomb();
    }

    public synchronized long getStartTime() {
        return startTime;
    }

    public Player getOwnerOfBomb(){
        return ownerOfBomb;
    }

    public int getRange(){
        return range;
    }

    public void decTime() {
        this.startTime = System.currentTimeMillis() - Consts.MILIS_TO_EXPLODE + 300;        //po 300 ms wybuchnie bomba
    }
}
