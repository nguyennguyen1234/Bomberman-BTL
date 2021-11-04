package com.client.exceptions;

public class PlayersNameNullException extends Exception {
    public PlayersNameNullException(String msg) {
        super(msg);
    }
    
    public PlayersNameNullException(int playersId) {
        super("Player's " + playersId + " name is null.");
    }
}
