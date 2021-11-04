package com.client;

import com.client.gui.interfaceControllers.LobbyController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

public class ClientMessageHandler extends Task {
    private static Logger log = Logger.getLogger(ClientMessageHandler.class.getCanonicalName());
    private static final boolean debug = false;

    private ClientMessageQueue messageQueue;
    private Client client;
    private boolean stay;
    private LobbyController lobbyController;

    ClientMessageHandler(ClientMessageQueue messageQueue, Client client, LobbyController lobbyController) {
        this.messageQueue = messageQueue;
        this.client = client;
        stay = true;
        this.lobbyController = lobbyController;
    }

    @Override
    protected Object call() throws Exception {

        while (stay) {
            String message = null;
            while (message == null) {
                if (!messageQueue.isEmpty()) {
                    message = messageQueue.pop(); //TODO "jezeli gra nadal trwa", pobierane z Game.
                }
            }

            if (debug)
                log.info("Received message: " + message);

            JSONObject msg = new JSONObject(message);
            String status = msg.getString("status");

            if (status != null) {
                if (status.equals("OK")) {
                    client.setMyId(msg.getInt("id"));
                    System.out.println("Moj Id: " + client.getID() + "\tCzekam na rozpoczecie gry"); //TODO Ready
                } else if (status.equals("start")) {
                    Platform.runLater(() -> {
                        try {
                            client.startGame();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                     });
                    stay = false;
                } else if (status.equals("changeSlot")){
                    statusChangeSlot(msg);
                } else if (status.equals("updateSlots")){
                    statusUpdateSlots(msg);
                } else if (status.equals("hs")){
                    statusHs(msg);
                }
                else {
                    System.out.println("Nie udalo sie polaczyc z serverem");
                }
            }
        }
        return null;
    }

    private void statusHs(JSONObject msg) {
        JSONArray highScores = msg.getJSONArray("hscores");
        client.updateHighScores(highScores);
    }

    private void statusChangeSlot(JSONObject msg) {
        int newSlot = msg.getInt("slotId");
        Platform.runLater(() -> client.setSlotId(newSlot));
    }

    private void statusUpdateSlots(JSONObject msg) {
        int slotId = msg.getInt("slotId");
        String text = msg.getString("text");
        Platform.runLater(() -> lobbyController.setPlayersSlot(slotId, text));
    }
}
