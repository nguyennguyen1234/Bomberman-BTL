package com.server;

import com.client.gui.interfaceControllers.HighscoresController;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Szczepan on 27.04.2017.
 */
public class Highscores {
    private static Logger log = Logger.getLogger(HighscoresController.class.getCanonicalName());
    private final String highscoresDataName = "highscores.dat";
    private final int topScoresAmount = 6;
    private SingleScore[] bestScores;

    public Highscores() throws IOException, ClassNotFoundException {
        bestScores = new SingleScore[topScoresAmount];
        for (int i =0; i < topScoresAmount; i++){
            bestScores[i] = new SingleScore();
        }
        readScores();
        bestScores[0].set("test", 1234);
    }

    private void readScores(){
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(highscoresDataName))) {
            bestScores = (SingleScore[]) input.readObject();
        }
        catch (IOException e) {
            log.warning("Highscores file not found!");
            saveScores();
        }
        catch (ClassNotFoundException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void saveScores(){
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(highscoresDataName))) {
            output.writeObject(bestScores);
            log.info("Highscores file saved to file!");
        }
        catch (IOException e1) {
            log.log(Level.SEVERE, e1.getMessage(), e1);
        }
    }

    public JSONArray getScoresInJSON(){
        JSONArray returnMessage = new JSONArray();

            for (SingleScore score : bestScores){
                if (score == null) {
                    break;
                }
                JSONObject singleScore = new JSONObject();
                singleScore.put("name", score.getName());
                singleScore.put("score", Integer.toString(score.getScore()));
                returnMessage.put(singleScore);
            }
            return returnMessage;
    }

    public void addNewHighscore(SingleScore newScore) {
        SingleScore copy;
        for (int i = 0; i < topScoresAmount; i++) {
            if (bestScores[i].compareTo(newScore)) {                // newScore bigger
                if (i != topScoresAmount - 1) {
                    for (int j = topScoresAmount - 2; j >= i; j--)
                        bestScores[j + 1] = bestScores[j];
                }
                bestScores[i] = newScore;
                return;
            }
        }
    }
}
