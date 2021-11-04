package com.server.Controllers;

import com.server.Broadcaster;
import com.server.ClientData;
import com.server.Consts;
import com.server.fields.Bomb;
import com.server.fields.Bonus;
import com.server.fields.Field;
import com.server.fields.Fire;
import com.server.fields.NormalBlock;
import com.server.fields.Player;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class LogicController {
    private static Logger log = Logger.getLogger(LogicController.class.getCanonicalName());
    private static final boolean debug = false;
    
    private ArrayList<ClientData> clients;
    private DatagramSocket socket;
    private Field[][] mapFields;
    private LinkedList<Player> players;
    private ArrayList<Fire> fires;
    private ExecutorService bombExecutors = Executors.newFixedThreadPool(Consts.MAX_N_BOMBS * 3);   //max liczba bomb na mapie * liczba graczy
    private ExecutorService firesExecutor = Executors.newSingleThreadExecutor();
    private final Map<String, Integer> fieldImages = fillHashMap();
    private final Map<Integer, Point> fieldForPlayers = fillFieldForPlayers();
    private static int breakLoopFires = 0;
    
    private static Map<String, Integer> fillHashMap() {
        Map<String, Integer> tempMap = new HashMap<>();
        tempMap.put("Player1", 31);
        tempMap.put("Player2", 32);
        tempMap.put("Player3", 33);
        tempMap.put("Player4", 34);
        tempMap.put("DefaultBlock", 0);
        tempMap.put("DestroyableBlock", 1);
        tempMap.put("Fire", 2);
        tempMap.put("UnDestroyableBlock", 4);
        tempMap.put("Bomb", 5);
        tempMap.put("Bonushaste", 6);
        tempMap.put("Bonusincrange", 7);
        tempMap.put("Bonusincbombs", 8);
        return tempMap;
    }
    
    private static Map<Integer, Point> fillFieldForPlayers() {
        Map<Integer, Point> tempMap = new HashMap<>();
        tempMap.put(0, new Point(0, 0));
        tempMap.put(1, new Point(Consts.DIMENSION - 1, Consts.DIMENSION - 1));
        tempMap.put(2, new Point(0, Consts.DIMENSION - 1));
        tempMap.put(3, new Point(Consts.DIMENSION - 1, 0));
        return tempMap;
    }
    
    public LogicController(DatagramSocket socket, ArrayList<ClientData> clients) {
        this.players = new LinkedList<>();
        this.fires = new ArrayList<>();
        this.socket = socket;
        this.clients = clients;
        firesLoop();
    }
    
    private Field getMapField(int x, int y) {
        return this.mapFields[y][x];
    }
    
    private void setMapField(int x, int y, Field field) {
        this.mapFields[y][x] = field;
    }
    
    public Player getPlayer(int id) {
        return players.get(id);
    }
    
    private ArrayList<Fire> getFires() {
        return this.fires;
    }
    
    public void fillMap() {
        this.mapFields = new Field[Consts.DIMENSION][Consts.DIMENSION];
        for (int i = 1; i < Consts.DIMENSION; i += 2) {
            for (int j = 1; j < Consts.DIMENSION; j += 2) {
                this.mapFields[i][j] = new NormalBlock(j, i, false, false);       //Blok nie do rozbicia
            }
        }
        
        Random generator = new Random();
        for (int i = 0; i < Consts.DIMENSION; i++) {
            for (int j = 0; j < Consts.DIMENSION; j++) {
                if (!((i % 2 == 1) && (j % 2 == 1)) && (generator.nextDouble() > Consts.NORMAL_BLOCK_PROB))
                    this.mapFields[i][j] = new NormalBlock(j, i, true, false);     //Blok do rozbicia
            }
        }
        for (int i = 0; i < Consts.DIMENSION; i++) {
            for (int j = 0; j < Consts.DIMENSION; j++) {
                if (mapFields[i][j] == null) {
                    this.mapFields[i][j] = new NormalBlock(j, i, false, true);          //Bloki puste
                }
            }
        }
        
        mapFields[0][0] = new NormalBlock(0, 0, false, true);             //pola puste dla graczy
        mapFields[0][1] = new NormalBlock(1, 0, false, true);
        mapFields[1][0] = new NormalBlock(0, 1, false, true);
        mapFields[0][Consts.DIMENSION - 1] = new NormalBlock(Consts.DIMENSION - 1, 0, false, true);
        mapFields[0][Consts.DIMENSION - 2] = new NormalBlock(Consts.DIMENSION - 2, 0, false, true);
        mapFields[1][Consts.DIMENSION - 1] = new NormalBlock(Consts.DIMENSION - 1, 1, false, true);
        mapFields[Consts.DIMENSION - 1][0] = new NormalBlock(0, Consts.DIMENSION - 1, false, true);
        mapFields[Consts.DIMENSION - 2][0] = new NormalBlock(0, Consts.DIMENSION - 2, false, true);
        mapFields[Consts.DIMENSION - 1][1] = new NormalBlock(1, Consts.DIMENSION - 1, false, true);
        mapFields[Consts.DIMENSION - 1][Consts.DIMENSION - 1] = new NormalBlock(Consts.DIMENSION - 1, Consts.DIMENSION - 1, false, true);
        mapFields[Consts.DIMENSION - 2][Consts.DIMENSION - 1] = new NormalBlock(Consts.DIMENSION - 1, Consts.DIMENSION - 2, false, true);
        mapFields[Consts.DIMENSION - 1][Consts.DIMENSION - 2] = new NormalBlock(Consts.DIMENSION - 2, Consts.DIMENSION - 1, false, true);
    }
    
    public void createPlayers(ArrayList<ClientData> clients) {
//        Random generator = new Random();
//        int randomPoint = generator.nextInt(clients.size());
//        int direction;
//        if (generator.nextBoolean()) {
//            direction = 1;
//        } else {
//            direction = -1;
//        }
        
        for (ClientData client : clients) {
            Point coords = fieldForPlayers.get(client.getId());
            createPlayer(coords.x, coords.y, client.getId(), client.getNick());
            //randomPoint = (randomPoint + direction) % clients.size();
        }
    }
    
    private void createPlayer(int x, int y, int id, String nick) {
        this.mapFields[y][x] = new Player(x, y, true, nick, id);
        players.add((Player) this.mapFields[y][x]);
        
        if (debug)
            log.info("Player's ID: " + ((Player) this.mapFields[y][x]).getId());
    }
    
    private void destroyField(int x, int y, Field newField, JSONArray answer) {
        if (this.mapFields[y][x] instanceof Player) {
            deletePlayerFromMap((Player) this.mapFields[y][x]);
        }
        this.mapFields[y][x] = newField;
        sendFieldOfMap(x, y, answer);
    }
    
    private void deletePlayerFromMap(Player player) {
        if (debug)
            log.info("Killing player: " + player.getNick());
        
        getPlayer(player.getId()).kill();
    }
    
    private boolean canMove(int x, int y) {
        if (x < 0 || y < 0 || x > Consts.DIMENSION - 1 || y > Consts.DIMENSION - 1) {
            return false;
        } else if (this.mapFields[y][x].isEmpty()) {
            return true;
        }
        return false;
    }
    
    private void addBomb(Bomb bomb) {
        bombExecutors.submit(() -> {
            while (true) {
                if ((System.currentTimeMillis() - bomb.getStartTime()) > Consts.MILIS_TO_EXPLODE) {
                    Platform.runLater(() -> explode(bomb));
                    break;
                }
            }
            return;
        });
    }
    
    private void addFire(Fire fire) {
        breakLoopFires = 1;
        this.fires.add(fire);
    }
    
    public String printEntireMap() {
        String mapp = "";
        for (int i = 0; i < Consts.DIMENSION; i++) {
            for (int j = 0; j < Consts.DIMENSION; j++) {
                Integer blockNumber = fieldImages.get(mapFields[j][i].getName());
                mapp += Integer.toString(blockNumber);
            }
        }
        return mapp;
    }
    
    private void sendFieldOfMap(int x, int y, JSONArray answer) {
        JSONObject temp = new JSONObject();
        temp.put("field", Integer.toString(fieldImages.get(getMapField(x, y).getName())));
        temp.put("y", Integer.toString(y));
        temp.put("x", Integer.toString(x));
        answer.put(temp);
    }
    
    private void sendNamedFieldOfMap(int x, int y, String Name, JSONArray answer) {
        JSONObject temp = new JSONObject();
        temp.put("field", Integer.toString(fieldImages.get(Name)));
        temp.put("y", Integer.toString(y));
        temp.put("x", Integer.toString(x));
        answer.put(temp);
    }
    
    public boolean incCoords(int finalID, String direction, JSONArray answer) {
        int diffX = 0;
        int diffY = 0;
        switch (direction) {
            case "UP":
                diffY = -1;
                break;
            case "LEFT":
                diffX = -1;
                break;
            case "DOWN":
                diffY = 1;
                break;
            default:                 //RIGHT
                diffX = 1;
                break;
        }
        
        int newX = players.get(finalID).getX() + diffX;
        int newY = players.get(finalID).getY() + diffY;
        
        if (canMove(newX, newY)) {
            int playerX = players.get(finalID).getX();
            int playerY = players.get(finalID).getY();
            if (!(getMapField(playerX, playerY) instanceof Bomb)) {                       //gracz schodzi ze zwyklego pola
                setMapField(playerX, playerY, new NormalBlock(playerX, playerY, false, true));
            } else {
                sendNamedFieldOfMap(playerX, playerY, "DefaultBlock", answer);           //gracz schodzi z bomby
            }
            //Nowe pole
            if (getMapField(newX, newY) instanceof Fire) {                              //wszedl w ogien
                sendNamedFieldOfMap(playerX, playerY, "DefaultBlock", answer);
                if (((Fire) getMapField(newX, newY)).getOwnerOfFire() != players.get(finalID)) {
                    ((Fire) getMapField(newX, newY)).getOwnerOfFire().incScore(Consts.SCORE_KILL_PLAYER);
                }
                deletePlayerFromMap(players.get(finalID));
                return true;
            } else if (getMapField(newX, newY) instanceof Bonus) {                      //wszedl na bonus
                ((Bonus) getMapField(newX, newY)).takeBonus(players.get(finalID));
                players.get(finalID).incScore(Consts.SCORE_TAKE_BONUS);
                if (getMapField(newX, newY).getName().equals("Bonushaste")) {
                    JSONObject msg = new JSONObject();
                    msg.put("cmd", "incspeed");
                    Broadcaster.msgToOne(clients.get(finalID), msg.toString(), socket);
                }
                sendNamedFieldOfMap(newX, newY, "DefaultBlock", answer);
            }
            sendFieldOfMap(playerX, playerY, answer);
            players.get(finalID).move(diffX, diffY);
            setMapField(players.get(finalID).getX(), players.get(finalID).getY(), players.get(finalID));
            sendFieldOfMap(players.get(finalID).getX(), players.get(finalID).getY(), answer);
            return true;
        }
        return false;
    }
    
    //BOMB
    
    public void dropBomb(int playerIndex, JSONArray answer) {
        Player player = players.get(playerIndex);
        int x = player.getX();
        int y = player.getY();
        if (player.getNBombs() > 0 && (!(getMapField(x, y) instanceof Bomb)) && player.isAlive()) {
            setMapField(x, y, new Bomb(x, y, true, player));
            addBomb((Bomb) getMapField(x, y));
            player.decNBombs();
            sendNamedFieldOfMap(x, y, "Bomb", answer);
            sendNamedFieldOfMap(x, y, player.getName(), answer);
        }
    }
    
    private void explode(Bomb bomb) {
        JSONObject answerToSend = new JSONObject();
        answerToSend.put("cmd", "move");
        
        JSONArray arrayOfFields = new JSONArray();
        if (bomb.getX() == bomb.getOwnerOfBomb().getX() && bomb.getY() == bomb.getOwnerOfBomb().getY()) {
            deletePlayerFromMap(bomb.getOwnerOfBomb());
        }
        Fire newFire = new Fire(bomb.getX(), bomb.getY(), true, bomb.getOwnerOfBomb());
        destroyField(bomb.getX(), bomb.getY(), newFire, arrayOfFields);
        addFire(newFire);
        
        boolean firstUp = false;
        boolean firstRight = false;
        boolean firstDown = false;
        boolean firstLeft = false;
        for (int i = 1; i < bomb.getRange() + 1; i++) {
            if (bomb.getY() - i >= 0 && !firstUp) {                                         //up
                firstUp = checkFieldToBurn(bomb.getX(), bomb.getY() - i, arrayOfFields, bomb);
            }
            if (bomb.getX() + i < Consts.DIMENSION && !firstRight) {                        //right
                firstRight = checkFieldToBurn(bomb.getX() + i, bomb.getY(), arrayOfFields, bomb);
            }
            if (bomb.getY() + i < Consts.DIMENSION && !firstDown) {                         //down
                firstDown = checkFieldToBurn(bomb.getX(), bomb.getY() + i, arrayOfFields, bomb);
            }
            if (bomb.getX() - i >= 0 && !firstLeft) {                                       //left
                firstLeft = checkFieldToBurn(bomb.getX() - i, bomb.getY(), arrayOfFields, bomb);
            }
        }
        bomb.getOwnerOfBomb().incNBombs();
        answerToSend.put("fields", arrayOfFields);
        Broadcaster.broadcastMessage(clients, answerToSend.toString(), socket);
        System.out.println("Wybuch:\t\t" + answerToSend.toString());
    }
    
    private boolean checkFieldToBurn(int xToCheck, int yToCheck, JSONArray answer, Bomb bomb) {
        if (getMapField(xToCheck, yToCheck) instanceof Bomb) {
            ((Bomb) getMapField(xToCheck, yToCheck)).decTime();
            editScores(bomb.getOwnerOfBomb(), Consts.SCORE_DESTROY_BLOCK);
            return true;
        }
        if (!getMapField(xToCheck, yToCheck).isDestroyable()                                //niezniszczalny kloc
                && !getMapField(xToCheck, yToCheck).isEmpty()) {
            return true;
        } else {
            Fire newFire = new Fire(xToCheck, yToCheck, false, bomb.getOwnerOfBomb());
            if (getMapField(xToCheck, yToCheck).isDestroyable()) {                          //do zniszczenia
                newFire.setUnderField(getMapField(xToCheck, yToCheck).getFieldUnderDestryableField());
                if (getMapField(xToCheck, yToCheck) instanceof Player) {
                    if (bomb.getOwnerOfBomb() != getMapField(xToCheck, yToCheck)) {
                        editScores(bomb.getOwnerOfBomb(), Consts.SCORE_KILL_PLAYER);
                    }
                } else {
                    editScores(bomb.getOwnerOfBomb(), Consts.SCORE_DESTROY_BLOCK);
                }
                destroyField(xToCheck, yToCheck, newFire, answer);
                addFire(newFire);
                return true;
            } else {
/*                if (map.getMapField(xToCheck, yToCheck) instanceof Fire) {            //TODO jesli ogien ma przechodzic przez ogien (nowsce watki?)
                    ((Fire) map.getMapField(xToCheck, yToCheck)).removeFire();
                }*/
                setMapField(xToCheck, yToCheck, newFire);
                addFire(newFire);
                sendNamedFieldOfMap(xToCheck, yToCheck, "Fire", answer);
                return false;
            }
        }
    }
    
    //FIRE
    
    private void removeFire(Fire fire, JSONArray fieldsArray) {
        if (fire.getFieldUnderFireField() == null) {
            destroyField(fire.getX(), fire.getY(), new NormalBlock(fire.getX(), fire.getY(), false, true), fieldsArray);
        } else {
            sendNamedFieldOfMap(fire.getX(), fire.getY(), "DefaultBlock", fieldsArray);
            setMapField(fire.getX(), fire.getY(), fire.getFieldUnderFireField());
            sendFieldOfMap(fire.getX(), fire.getY(), fieldsArray);
        }
    }
    
    private void firesLoop() {
        firesExecutor.execute(() -> {
            while (true) {
                Iterator it = getFires().iterator();
                JSONObject answerToSend = new JSONObject();
                answerToSend.put("cmd", "move");
                JSONArray fieldsArray = new JSONArray();
                while (it.hasNext()) {
                    if (breakLoopFires == 1) {
                        try {
                            Thread.sleep(10);                           //TODO da sie to rozwiazac inaczej?
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        breakLoopFires = 0;
                        break;
                    }
                    Fire tempFire = (Fire) it.next();
                    if (System.currentTimeMillis() - tempFire.getStartTime() > Consts.FIRE_MILIS) {
                        removeFire(tempFire, fieldsArray);
                        it.remove();
                    }
                }
                if (!fieldsArray.isNull(0)) {
                    answerToSend.put("fields", fieldsArray);
                    Broadcaster.broadcastMessage(clients, answerToSend.toString(), socket);
                }
            }
        });
    }
    
    //Scores
    private void editScores(Player player, int score) {
        player.incScore(score);
        JSONObject answerToSend = new JSONObject();
        answerToSend.put("cmd", "scores");
        answerToSend.put("player", player.getId());
        answerToSend.put("score", Integer.toString(player.getScore()));
        
        Broadcaster.broadcastMessage(clients, answerToSend.toString(), socket);
    }

    public void killPlayer(int clientId) {
        getPlayer(clientId).kill();
    }
}
