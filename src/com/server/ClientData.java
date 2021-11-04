package com.server;

import java.net.InetAddress;

public class ClientData {
    
    @Override
    public String toString() {
        return "ClientData [IPaddr=" + IPaddress + ", id=" + id + "]";
    }
    
    private InetAddress IPaddress;
    private int id;
    private int port;
    private boolean ready;
    private String nick;
    
    ClientData(InetAddress IP, int port, int id) {
        this.IPaddress = IP;
        this.id = id;
        this.port = port;
        ready = false;
        nick = "";
    }
    
    int getPort() {
        return port;
    }
    
    InetAddress getIPaddress() {
        return IPaddress;
    }
    
    public void setIPaddress(InetAddress IPaddress) {
        IPaddress = IPaddress;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    boolean isReady() {
        return ready;
    }
    
    void changeReadyStatus() {
        ready = !ready;
    }
    
    public void setNick(String nick) {
        this.nick = nick;
    }
    
    public String getNick() {
        return nick;
    }
}