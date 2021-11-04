package com.server;

import com.server.Controllers.GUIController;
import com.server.Controllers.LogicController;
import com.server.Controllers.ServerConsts;
import com.server.Controllers.ServerLobbyController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MessagePageboy extends Task {
    private static Logger log = Logger.getLogger(MessagePageboy.class.getCanonicalName());
    private static final boolean debug = false;

    private DatagramPacket codedMessage;
    private JSONObject message;
    private String whatToDoMyLord;
    private ArrayList<ClientData> clients;
    private DatagramSocket socket;
    private LogicController logicController;
    private ServerLobbyController serverLobbyController;
    private GUIController serverMessageController;
    private Highscores highscores;

    public MessagePageboy(DatagramPacket codedMessage, JSONObject message, String whatToDoMyLord,
                          ArrayList<ClientData> clients, DatagramSocket socket, LogicController logicController,
                          ServerLobbyController serverLobbyController, GUIController serverMessageController, Highscores highscores){
        this.codedMessage = codedMessage;
        this.message = message;
        this.whatToDoMyLord = whatToDoMyLord;
        this.clients = clients;
        this.socket = socket;
        this.logicController = logicController;
        this.serverLobbyController = serverLobbyController;
        this.serverMessageController = serverMessageController;
        this.highscores = highscores;
    }

    @Override
    protected Object call() throws Exception {

        if(whatToDoMyLord.equals("join")){
            cmdJoin(codedMessage);
        }else if(whatToDoMyLord.equals("ready")){
            cmdReady(message);
        }else if(whatToDoMyLord.equals("updateSlots")){
            cmdUpdateSlots(message);
        }else if(whatToDoMyLord.equals("key")){
            synchronized (this){cmdKey(message); notify();}
        }else if (whatToDoMyLord.equals("quit")){
            cmdQuit(message);
        }
        return null;
    }

    private void cmdJoin(DatagramPacket codedMessage) throws InterruptedException {
        ClientData newClient = new ClientData(codedMessage.getAddress(), codedMessage.getPort(), 0);

        /* Jeśli są miejsca to dodaj gracza do gry, przydziel ID i wyślij odpowiedz OK */
        if (clients.size() < ServerConsts.MAX_NUMBER_OF_PLAYERS) {
            handleClient(newClient);
        } else {
            rejectClient(newClient);
        }
    }

    private synchronized void handleClient(ClientData newClient) {
        JSONObject answerToSend = new JSONObject();
        int clientId = clients.size();
        newClient.setId(clientId);
        clients.add(newClient);
        answerToSend.put("status", "OK");
        answerToSend.put("id", clientId);

        Broadcaster.msgToOne(newClient, answerToSend.toString(), socket);
        sendSlots(newClient);
        sendHighscores(newClient);

        Platform.runLater(() -> serverMessageController.sendMessage("Dolacza: " + newClient.getIPaddress()));
    }

    private void sendHighscores(ClientData newClient) {
        JSONArray arrayOfScores = highscores.getScoresInJSON();
        JSONObject messageToSend = new JSONObject();
        messageToSend.put("status", "hs");
        messageToSend.put("hscores", arrayOfScores);
        if (arrayOfScores.length() != 0){
            Broadcaster.msgToOne(newClient, messageToSend.toString(), socket);
        }
    }

    private void sendSlots(ClientData newClient) {
        serverLobbyController.sendSlotsToClient(newClient);
    }

    private void rejectClient(ClientData newClient) {
        JSONObject answerToSend = new JSONObject();
        answerToSend.put("status", "ACCESS_DENIED");
        Broadcaster.msgToOne(newClient, answerToSend.toString(), socket);
        Platform.runLater(() -> serverMessageController.sendMessage("Odrzucilem klienta: " + newClient.getIPaddress()));
    }

    private void cmdReady(JSONObject msg) throws InterruptedException {
        int clientId = msg.getInt("id");
        clients.get(clientId).changeReadyStatus();
        for (ClientData client : clients) {              //Jest tylu, ilu się połączyło
            if (!client.isReady()) {
                return;
            }
        }
        //Jesli wszyscy klienci sa gotowi, to zaczynamy gre
        startGame();
    }

    private void startGame() throws InterruptedException {
        JSONObject answerToStart = new JSONObject();
        answerToStart.put("status", "start");
        Broadcaster.broadcastMessage(clients, answerToStart.toString(), socket);
        JSONObject answerToPrint = new JSONObject();

        logicController.fillMap();
        logicController.createPlayers(clients);

        answerToPrint.put("cmd", "eMap");
        answerToPrint.put("fields", logicController.printEntireMap());

        JSONObject answerToScores = new JSONObject();
        answerToScores.put("cmd", "escores");
        JSONArray clientsArray = new JSONArray();

        for (ClientData client : clients){
            JSONObject temp = new JSONObject();
            temp.put("id", Integer.toString(client.getId()));
            temp.put("nick", client.getNick());
            clientsArray.put(temp);
        }
        answerToScores.put("plrs", clientsArray);
        System.out.println("WAZNA WIADOMOSC\t\t" + answerToScores.toString());

        Broadcaster.broadcastMessage(clients, answerToScores.toString(), socket);

        Broadcaster.broadcastMessage(clients, answerToPrint.toString(), socket);
        Platform.runLater(() -> serverMessageController.sendMessage("Start gry"));
        ExecutorService wholeMapSenderExecutor = Executors.newSingleThreadExecutor();
        wholeMapSenderExecutor.submit(new WholeMapSender(logicController, socket, clients));
    }

    private void cmdUpdateSlots(JSONObject msg) {
        int newIdSlot = msg.getInt("newSlotId");
        int oldIdSlot = msg.getInt("oldSlotId");
        int clientId = msg.getInt("clientId");
        String textOnSlot = msg.getString("text");

        if (debug)
            log.info("Setting up slots.");

        Platform.runLater(() -> serverLobbyController.changeSlot(newIdSlot, oldIdSlot, textOnSlot, clientId));
    }

    private void cmdKey(JSONObject msg) {
        int clientId = msg.getInt("id");
        if (logicController.getPlayer(clientId).isAlive()) {
            String key = msg.getString("but");
            JSONObject answerToSend = new JSONObject();
            JSONArray arrayToSend = new JSONArray();
            answerToSend.put("cmd", "move");
            if (key.equals("BOMB")) {
                keyBomb(clientId, answerToSend, arrayToSend);
            } else {                                                //key == UP || RIGT || DOWN || LEFT
                keyMakeMove(clientId, key, answerToSend, arrayToSend);
            }
        } else {
            Platform.runLater(() -> serverMessageController.sendMessage("Player is dead."));
        }
    }

    private void cmdQuit(JSONObject message) {
        int clientId = message.getInt("id");
        logicController.killPlayer(clientId);
    }

    private void keyBomb(int clientId, JSONObject answerToSend, JSONArray arrayToSend){
        logicController.dropBomb(clientId, arrayToSend);
        Platform.runLater(() -> serverMessageController.sendMessage(arrayToSend.toString()));
        answerToSend.put("fields", arrayToSend);
        Broadcaster.broadcastMessage(clients, answerToSend.toString(), socket);
    }

    private void keyMakeMove(int clientId, String key, JSONObject answerToSend, JSONArray arrayToSend) {
        if (logicController.incCoords(clientId, key, arrayToSend)) {
            Platform.runLater(() -> serverMessageController.sendMessage(arrayToSend.toString()));
            answerToSend.put("fields", arrayToSend);
            Broadcaster.broadcastMessage(clients, answerToSend.toString(), socket);
        } else {
            Platform.runLater(() -> serverMessageController.sendMessage("Unable to move."));
    }
}}