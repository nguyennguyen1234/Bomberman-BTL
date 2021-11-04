package com.client.exceptions;

public class PlayersColorNullException extends Exception {
    public PlayersColorNullException(String msg) {
        super(msg);
    }
    
    public PlayersColorNullException(int playersId) {
        super("Player's " + playersId + " color is null.");
    }
}
