package com.server;

public class Slot {
    private String textOnSlot;
    private boolean empty;

    public Slot(String text){
        textOnSlot = text;
        empty = true;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public String getTextOnSlot(){
        return textOnSlot;
    }

    public void setTextOnSlot(String textOnSlot){
        this.textOnSlot = textOnSlot;
    }
}
