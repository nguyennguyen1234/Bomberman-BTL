package com.client.gui.interfaceControllers;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

class PlayerSlot {
    private Label playerNameLabel;
    private Pane playersColorPane;
    private boolean isEmpty;
    
    PlayerSlot(Label playerNameLabel, Pane playersColorPane, boolean isEmpty) {
        this.playerNameLabel = playerNameLabel;
        this.playersColorPane = playersColorPane;
        this.isEmpty = isEmpty;
    }
    
    boolean isEmpty() {
        return isEmpty;
    }
    
    void takeSlot(String playersName) {
        isEmpty = false;
        playerNameLabel.setText(playersName);
    }
    
    void freeSlot() {
        isEmpty = true;
        playerNameLabel.setText("Free slot");
        playersColorPane.setStyle("-fx-background-color: red;");
    }
    
    void setPlayersName(String name) {
        playerNameLabel.setText(name);
    }
    
    boolean equalsColorPane(Pane input) {
        return this.playersColorPane == input;
    }
    
    boolean equalsNameLabel(Label input) {
        return this.playerNameLabel == input;
    }
}
