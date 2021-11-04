package com.client;

import java.util.LinkedList;

public class ClientMessageQueue {
    
    private LinkedList<String> messageQueue;
    
    ClientMessageQueue() {
        messageQueue = new LinkedList<String>();
    }
    
    synchronized void add(String command) {  //TODO notify();
        messageQueue.add(command);
    }
    
    synchronized String pop() {
        return messageQueue.pop();
    }
    
    public synchronized boolean isEmpty() {
        return messageQueue.isEmpty();
    }
}