package com.server;

import java.net.DatagramPacket;
import java.util.LinkedList;

public class MessageQueue {
    private LinkedList<DatagramPacket> messageQueue;
    
    public MessageQueue() {
        messageQueue = new LinkedList<DatagramPacket>();
    }
    
    public synchronized void add(DatagramPacket message) {
        messageQueue.add(message);
        notify(); // TODO all?
    }
    
    synchronized DatagramPacket pop() throws InterruptedException {
        
        while (messageQueue.isEmpty()) {
            wait(1000);
            if (messageQueue.isEmpty()) {
                return (null);
            }
        }
        return messageQueue.pop();
    }
}