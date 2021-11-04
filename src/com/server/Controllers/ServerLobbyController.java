package com.server.Controllers;

import com.server.Broadcaster;
import com.server.ClientData;
import com.server.Slot;
import org.json.JSONObject;

import java.net.DatagramSocket;
import java.util.ArrayList;

public class ServerLobbyController {
    private static final int playersAmount = 3;
    
    private ArrayList<ClientData> clients;
    private DatagramSocket socket;
    private ArrayList<Slot> slots;

    public ServerLobbyController(DatagramSocket socket, ArrayList<ClientData> clients){
        this.socket = socket;
        this.clients = clients;
        slots = new ArrayList<>();
        
        for (int i = 0; i < playersAmount; i++)
            slots.add(new Slot("Free slot"));
    }

    public synchronized void changeSlot(int newIdSlot, int oldIdSlot, String textOnSlot, int clientId) {
        if (slots.get(newIdSlot).isEmpty()){
            if (oldIdSlot != -1){
                slots.get(oldIdSlot).setEmpty(true);
                slots.get(oldIdSlot).setTextOnSlot("Free Slot");
                sendUpdatedSlotToEveryone(oldIdSlot, slots.get(oldIdSlot).getTextOnSlot());
            }
            slots.get(newIdSlot).setEmpty(false);
            slots.get(newIdSlot).setTextOnSlot(textOnSlot);
            clients.get(clientId).setNick(textOnSlot);
            sendUpdatedSlotToEveryone(newIdSlot, slots.get(newIdSlot).getTextOnSlot());
            sendUpdatedSlotToOne(clientId, newIdSlot);
        }
    }

    private void sendUpdatedSlotToOne(int clientId, int newIdSlot){
        JSONObject messageToSend = new JSONObject();
        messageToSend.put("status", "changeSlot");
        messageToSend.put("slotId", Integer.toString(newIdSlot));
        Broadcaster.msgToOne(clients.get(clientId), messageToSend.toString(), socket);
    }

    private void sendUpdatedSlotToEveryone(int idSlot, String textOnSlot){
        JSONObject messageToSend = new JSONObject();
        messageToSend.put("status", "updateSlots");
        messageToSend.put("slotId", Integer.toString(idSlot));
        messageToSend.put("text", textOnSlot);
        Broadcaster.broadcastMessage(clients, messageToSend.toString(), socket);
    }

    public void sendSlotsToClient(ClientData newClient) {
        for (int i = 0; i < playersAmount; i++){
            JSONObject messageToSend = new JSONObject();
            messageToSend.put("status", "updateSlots");
            messageToSend.put("slotId", Integer.toString(i));
            messageToSend.put("text", slots.get(i).getTextOnSlot());
            Broadcaster.msgToOne(clients.get(newClient.getId()), messageToSend.toString(), socket);
        }
    }
}
