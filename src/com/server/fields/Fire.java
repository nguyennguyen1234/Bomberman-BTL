package com.server.fields;


public class Fire extends Field {
    private long startTime;
    private Bonus fieldUnderFireField;
    private Player ownerOfFire;                   //TODO jesli bedziemy robic punktacje

    public Fire(int x, int y, boolean destroyable, Player owner) {
        super(x, y, destroyable);
        this.empty = true;
        this.startTime = System.currentTimeMillis();
        this.name = "Fire";
        this.ownerOfFire = owner;
        this.fieldUnderFireField = null;
    }

    public long getStartTime() {
        return startTime;
    }

    public Bonus getFieldUnderFireField(){
        return fieldUnderFireField;
    }

    public void setUnderField(Bonus field) {
        this.fieldUnderFireField = field;
    }

    public Player getOwnerOfFire() {
        return ownerOfFire;
    }

    public void setOwnerOfFire(Player ownerOfFire) {
        this.ownerOfFire = ownerOfFire;
    }
}
