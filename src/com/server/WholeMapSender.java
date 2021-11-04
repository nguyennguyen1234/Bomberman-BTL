package com.server;

import com.server.Controllers.LogicController;
import javafx.concurrent.Task;
import org.json.JSONObject;

import java.net.DatagramSocket;
import java.util.ArrayList;

/**
 * Created by Uzytkownik on 27.04.2017.
 */
public class WholeMapSender extends Task {

    private LogicController logicController;
    private DatagramSocket socket;
    private ArrayList<ClientData> clients;

    WholeMapSender(LogicController logicController, DatagramSocket socket, ArrayList<ClientData> clients){
        this.logicController = logicController;
        this.socket = socket;
        this.clients = clients;
    }

    Boolean gameOn = true; //TODO jezeli gra nadal trwa
    int iterator =0;
    @Override
    protected Object call() throws Exception {
        while(gameOn){
            Thread.sleep(3000);
            iterator++;
            JSONObject msg = new JSONObject();
            msg.put("cmd", "eMap");
            msg.put("fields", logicController.printEntireMap());
            System.out.println("WYSYLKA CALEJ MAPY " + iterator);
            Broadcaster.broadcastMessage(clients, msg.toString(), socket);
        }
        return null;
    }
}
