package com.client;

import javafx.concurrent.Task;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Logger;

public class ClientReceiver extends Task {
    private static Logger log = Logger.getLogger(ClientReceiver.class.getCanonicalName());
    private static final boolean debug = false;
    
    private ClientMessageQueue messages;
    private DatagramSocket serverSocket;
    private DatagramPacket data;
    private byte[] receivedData;
    
    ClientReceiver(ClientMessageQueue messages, DatagramSocket socket) {
        this.messages = messages;
        this.serverSocket = socket;
    }
    
    @Override
    public Object call() {
        while (true) {
            receivedData = new byte[1024];
            data = new DatagramPacket(receivedData, receivedData.length);
            try {
                serverSocket.receive(data);
                if (debug)
                    log.info("Received data pack (adding to queue): " + new String(data.getData()));
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            synchronized (messages) {
                messages.add(new String((data.getData())));
                messages.notify();
            }
        }
    }
}
