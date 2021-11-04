package com.client.gui.interfaceControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GameController extends MainStageController {
    private static Logger log = Logger.getLogger(GameController.class.getCanonicalName());
    private final static int playersAmount = 4;
    private static boolean debug = true;
    
    private final Map<Integer, String> scoresMap = new HashMap<>();
    
    private Label scoreLabels[] = new Label[4];
    
    @FXML
    private Pane gameMapPane;
    @FXML
    private Pane gameScoresPane;
    @FXML
    private Label score1Label;
    @FXML
    private Label score2Label;
    @FXML
    private Label score3Label;
    @FXML
    private Label score4Label;
    
    @FXML
    public void initialize() {
        scoreLabels[0] = score1Label;
        scoreLabels[1] = score2Label;
        scoreLabels[2] = score3Label;
        scoreLabels[3] = score4Label;
        
        for (int i = 0; i < playersAmount; i++)
            scoreLabels[i].setText("Empty :(");
    }
    
    public Pane getGameMapPane() {
        return gameMapPane;
    }
    
    public Pane getGameScoresPane() {
        return gameScoresPane;
    }
    
    public void initializeScoreLabel(int playersNumber, String playersName) {
        if (debug)
            log.info("Initialization of player's score. Player: " + playersNumber + " " + playersName);
        
        scoresMap.put(playersNumber, playersName);
        scoreLabels[playersNumber].setText(playersName + " - 0");
        log.info(scoreLabels[playersNumber].getText());
    }
    
    public void setScoreLabel(int playersNumber, int newScore) {
        if (debug)
            log.info("Player number " + playersNumber + " has new score: " + newScore);
        
        scoreLabels[playersNumber].setText(scoresMap.get(playersNumber) + " - " + newScore);
    }
}
