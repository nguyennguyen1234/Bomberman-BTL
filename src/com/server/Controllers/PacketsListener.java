package com.server.Controllers;

import javafx.concurrent.Task;
import com.server.MessageHandler;
import com.server.MessageQueue;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PacketsListener extends Task {
    private static Logger log = Logger.getLogger(PacketsListener.class.getCanonicalName());
    private static final int PORT = 7115;
    private static final int BUFFER = 2048;
    private static final boolean debug = false;
    private ExecutorService executor = Executors.newFixedThreadPool(4);
    private GUIController controller;
    private DatagramSocket socket;
    private MessageQueue messages;
    private byte[] buf;

    public PacketsListener(GUIController controller) throws IOException, ClassNotFoundException {
        this.controller=controller;
        this.socket = new DatagramSocket(PORT);
        this.messages= new MessageQueue();
        buf = new byte[BUFFER];
        executor.submit(new MessageHandler(messages, controller, socket));
    }

    @Override
    protected Object call() throws Exception {
        if (debug)
            log.info("Serwer rozpoczal dzialanie");

        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                buf = new byte[BUFFER];
                messages.add(packet);
            } catch(Exception e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}