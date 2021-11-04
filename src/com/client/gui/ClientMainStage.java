package com.client.gui;

import com.client.Client;
import com.client.gui.interfaceControllers.GameController;
import com.client.gui.interfaceControllers.HighscoresController;
import com.client.gui.interfaceControllers.LobbyController;
import com.client.gui.interfaceControllers.MainStageController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class ClientMainStage extends Application {
    protected static boolean debug = false;
    protected static Logger log = Logger.getLogger(ClientMainStage.class.getCanonicalName());
    protected static Stage primaryStage;
    protected static Pane root;
    protected static Scene scene;
    protected static Client thisPlayer;
    
    protected FXMLLoader loader;

    // Child controllers
    @FXML
    public static MainStageController mainStageController = new MainStageController();
    @FXML
    public static GameController gameController = new GameController();
    @FXML
    public static LobbyController lobbyController = new LobbyController();
    @FXML
    public static HighscoresController highscoresController = new HighscoresController();
    @FXML
    public static Pane primaryPane;

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientMainStage.primaryStage = primaryStage;
        loader = new FXMLLoader(getClass().getResource("interfaceControllers/fxmlFiles/MainStage.fxml"));
        mainStageController = new MainStageController();
        loader.setController(mainStageController);
        root = loader.load();
        primaryStage.setTitle("Bomberman");
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        thisPlayer = new Client(this, lobbyController);
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Pane getRootElem() {
        return root;
    }
}