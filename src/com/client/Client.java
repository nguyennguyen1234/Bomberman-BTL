package com.client;

import com.client.exceptions.PlayersColorNullException;
import com.client.exceptions.PlayersNameNullException;
import com.client.gui.ClientConsts;
import com.client.gui.ClientMainStage;
import com.client.gui.interfaceControllers.LobbyController;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private static Random generator = new Random();
    
    private ExecutorService executor = Executors.newFixedThreadPool(3);
    private DatagramSocket socket;
    private InetAddress serverIP;
    private ClientMessageQueue messages;
    private int serverPort;
    private int myId;
    private ClientMainStage mainStage;
    private int slotId;
    
    private String playersName;
    private String playersColor;
    private int playersTimeBetweenMoves;
    
    public Client(ClientMainStage mainStage, LobbyController lobbyController) throws IOException, InterruptedException {
        this.socket = new DatagramSocket();
        this.messages = new ClientMessageQueue();
        this.mainStage = mainStage;
        executor.submit(new ClientReceiver(messages, socket));
        executor.submit(new ClientMessageHandler(messages, this, lobbyController));
        slotId = -1;
        myId = 0;
        playersColor = Integer.toHexString(generator.nextInt(16581375 + 1));
        playersTimeBetweenMoves = ClientConsts.TIME_BETWEEN_MOVES;
    }
    
    void startGame() throws IOException, InterruptedException {
        ClientMainStage.mainStageController.startNewGame();
        ClientMap map = new ClientMap(mainStage);
        executor.submit(new GameMessageHandler(messages, map, this));
        ClientListener playerListener = new ClientListener(mainStage, this);
        playerListener.listen();    //TODO Listen w nowym watku?
    }

    public void send(String message) {
        System.out.println(message);
        DatagramPacket data = new DatagramPacket(message.getBytes(), message.length(), serverIP, serverPort);
        try {
            System.out.println(data.getData());
            socket.send(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSlot(int oldSlotId, int newSlotId, String textOnLabel) {
        JSONObject msg = new JSONObject();
        msg.put("cmd", "updateSlots");
        msg.put("text", textOnLabel);
        msg.put("newSlotId", Integer.toString(newSlotId));
        msg.put("oldSlotId", Integer.toString(oldSlotId));
        msg.put("clientId", Integer.toString(myId));
        System.out.println("Wysylam nowy slot");
        send(msg.toString());
    }

    public void sendQuitGameMessage()
    {
/*        JSONObject msg = new JSONObject();
        msg.put("cmd", "quit");
        msg.put("id",Integer.toString(myId));
        send(msg.toString());*/
    }


    void sendKey(String which) {
        JSONObject msg = new JSONObject();
        msg.put("cmd", "key");
        msg.put("but", which);
        msg.put("id", myId);
        send(msg.toString());
    }

    public void wannaJoin(String serverIP, String serverPort) throws UnknownHostException {
        this.serverIP = InetAddress.getByName(serverIP);
        this.serverPort = Integer.parseInt(serverPort);
        JSONObject msg = new JSONObject();
        msg.put("cmd", "join");
        send(msg.toString());
    }

    void setMyId(int id) {
        this.myId = id;
    }

    public int getID() {
        return myId;
    }

    void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setPlayersColor(String playersColor) {
        this.playersColor = playersColor;
    }
    
    public String getPlayersColor() {
        return this.playersColor;
    }

    int getPlayersTimeBetweenMoves() {
        return playersTimeBetweenMoves;
    }

    void setPlayersTimeBetweenMoves(int playersTimeBetweenMoves) {
        this.playersTimeBetweenMoves = playersTimeBetweenMoves;
    }
    
    public void setPlayersName(String playersName) {
        this.playersName = playersName;
    }
    
    public void isReadyToJoin() throws PlayersNameNullException, PlayersColorNullException {
        if (playersName == null)
            throw new PlayersNameNullException(myId);
        
        if (playersColor == null)
            throw new PlayersColorNullException(myId);
    }
    
    public boolean isNameSet() {
        return playersName != null;
    }
    
    void newGameScore(int playersId, String playersName) {
        Platform.runLater(() -> ClientMainStage.gameController.initializeScoreLabel(playersId, playersName));
    }
    
    void setGameScore(int playersId, int newScore) {
        Platform.runLater(() -> ClientMainStage.gameController.setScoreLabel(playersId, newScore));
    }

    public void updateHighScores(JSONArray highScores) {
        Platform.runLater(() -> mainStage.highscoresController.setScores(highScores));
    }
}

