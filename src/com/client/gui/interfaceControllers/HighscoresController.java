package com.client.gui.interfaceControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.json.JSONArray;
import org.json.JSONObject;

public class HighscoresController extends MainStageController {

    private final int topScoresAmount = 6;
    public  String[] texts;

    private Label topScoresLabels[] = new Label[topScoresAmount];

    @FXML
    private Label top1LabelScore;
    @FXML
    private Label top2LabelScore;
    @FXML
    private Label top3LabelScore;
    @FXML
    private Label top4LabelScore;
    @FXML
    private Label top5LabelScore;
    @FXML
    private Label top6LabelScore;

    public HighscoresController(){
        texts = new String[topScoresAmount];
    }

    @FXML
    public void initialize() {
        topScoresLabels[0] = top1LabelScore;
        topScoresLabels[1] = top2LabelScore;
        topScoresLabels[2] = top3LabelScore;
        topScoresLabels[3] = top4LabelScore;
        topScoresLabels[4] = top5LabelScore;
        topScoresLabels[5] = top6LabelScore;
        for (int i =0; i < topScoresAmount; i++){
            topScoresLabels[i].setText(texts[i]);
        }
    }


    @FXML
    void mouseClickedLabel() {

    }

    @FXML
    void mouseEnteredLabel() {

    }

    @FXML
    void mouseExitedLabel() {

    }

    public void setScores(JSONArray scores) {

        for (int i = 0; i < scores.length(); i++) {
            JSONObject temp = scores.getJSONObject(i);
            String playerNick = temp.getString("name");
            int score = temp.getInt("score");

//            System.out.println("W hajskors bedzie:  " + score + "\t\t" + playerNick);
            texts[i]=playerNick + "   " + score;
        }
    }


/*    private void readHighscores() {
        for (int i = 0; i < topScoresAmount; i++)
            topScoresLabels[i].setText(bestScores[i].get());
    }*/
}
