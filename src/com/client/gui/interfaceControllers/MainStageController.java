package com.client.gui.interfaceControllers;

import com.client.gui.ClientMainStage;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;

import java.io.IOException;
import java.util.logging.Level;

public class MainStageController extends ClientMainStage {
    
    @FXML
    public void startNewGame() {
        if (debug)
            log.info("Starting a new game.");
        
        try {
            loader = new FXMLLoader(getClass().getResource("fxmlFiles/Game.fxml"));
            loader.setController(gameController);
            root = loader.load();
            loadStage();
            
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        
    }
    
    @FXML
    void openLobby() {
        if (debug)
            log.info("Opening lobby scene.");
        
        try {
            loader = new FXMLLoader(getClass().getResource("fxmlFiles/Lobby.fxml"));
            loader.setController(lobbyController);
            root = loader.load();
            loadStage();
            
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    @FXML
    void openHighscores() {
        if (debug)
            log.info("Opening higscores.");
        
        try {
            loader = new FXMLLoader(getClass().getResource("fxmlFiles/Highscores.fxml"));
            loader.setController(highscoresController);
            root = loader.load();
            loadStage();
            
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    @FXML
    void exitGame() {
        log.info("Closing application.");
        
        this.primaryStage.close();
    }
    
    @FXML
    void backToMenu() {
        if (debug)
            log.info("Going back to main menu.");
        
        try {
            thisPlayer.sendQuitGameMessage();
            loader = new FXMLLoader(getClass().getResource("fxmlFiles/MainStage.fxml"));
            loader.setController(mainStageController);
            root = loader.load();
            loadStage();
            
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    @FXML
    void mouseEnteredButton(Event event) {
        if (debug)
            log.info("Mouse enetered a button.");
        
        Button eventButton = (Button) event.getTarget();
        eventButton.setEffect(new Glow(0.5));
    }
    
    @FXML
    void mouseExitedButton(Event event) {
        if (debug)
            log.info("Mouse exited a button.");
        
        Button eventButton = (Button) event.getTarget();
        eventButton.setEffect(new Glow(0.0));
    }
    
    void showAlert(Alert.AlertType alertType, String headerText, String message) {
        Alert alert = new Alert(alertType, message);
        alert.setTitle("");
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
    
    private void loadStage() {
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
