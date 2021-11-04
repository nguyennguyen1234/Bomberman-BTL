package com.client.gui.interfaceControllers;

import com.client.exceptions.GameSlotOccupiedException;
import com.client.exceptions.PlayersColorNullException;
import com.client.exceptions.PlayersNameNullException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.json.JSONObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class LobbyController extends MainStageController {
    private static Logger log = Logger.getLogger(LobbyController.class.getCanonicalName());
    private static final int playersAmount = 3;

    // IPv4 address pattern
    private static final Pattern ADDRESPATTERN =
            Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})$");
    private static final Pattern PORTPATTERN = Pattern.compile("^(\\d{1,5})$");

    private static boolean isIPAddressValid = true, isPortValid = true;
    private static String serverAddress = "127.0.0.1";
    private static String serverPort = "7115";
    private static PlayerSlot playersSlots[] = new PlayerSlot[playersAmount];

    private boolean colorPickerAdded;
    private boolean ready;
    private PlayerSlot selectedSlot;
    private String playersName, playersColor;

    @FXML
    private TextField IPAddressField;
    @FXML
    private TextField PortField;
    @FXML
    private Pane player1Color;
    @FXML
    private Pane player2Color;
    @FXML
    private Pane player3Color;
    @FXML
    private Label player1NameLabel;
    @FXML
    private Label player2NameLabel;
    @FXML
    private Label player3NameLabel;
    @FXML
    private ColorPicker colorPicker;

    private static boolean validateIPv4(final String ip) {
        return ADDRESPATTERN.matcher(ip).matches();
    }

    private static boolean validatePort(final String port) {
        return PORTPATTERN.matcher(port).matches();
    }

    @FXML
    public void initialize() {
        playersName = "Elo";
        playersColor = null;
        ready = false;
        thisPlayer.setPlayersName("Test");

        playersSlots[0] = new PlayerSlot(player1NameLabel, player1Color, true);
        playersSlots[1] = new PlayerSlot(player2NameLabel, player2Color, true);
        playersSlots[2] = new PlayerSlot(player3NameLabel, player3Color, true);

        colorPickerAdded = false;
        colorPicker = new ColorPicker();
        colorPicker.setStyle("-fx-color-label-visible: false;");
    }

    @FXML
    void addressEntered() {
        serverAddress = IPAddressField.getText();
        if (debug)
            log.info("User entered " + serverAddress + " server's address.");

        if (validateIPv4(serverAddress)) {
            IPAddressField.setStyle("-fx-text-fill: green;");
            isIPAddressValid = true;
        } else {
            IPAddressField.setStyle("-fx-text-fill: red;");
            isIPAddressValid = false;
        }
    }

    @FXML
    void portEntered() {
        serverPort = PortField.getText();
        if (debug)
            log.info("User entered " + serverPort + " server's port.");

        if (validatePort(serverPort)) {
            PortField.setStyle("-fx-text-fill: green;");
            isPortValid = true;
        } else {
            PortField.setStyle("-fx-text-fill: red;");
            isPortValid = false;
        }
    }

    @FXML
    void connectToServer() {
        if (debug)
            log.info("Trying to connect to server: " + serverAddress);

        if (isIPAddressValid && isPortValid) {
            if (debug)
                log.info("Connecting to server!");

            try {
                thisPlayer.isReadyToJoin();
                thisPlayer.wannaJoin(serverAddress, serverPort);
            } catch (UnknownHostException | PlayersNameNullException
                    | PlayersColorNullException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
                String alertMessage =
                        "There was a problem, with connecting to a server. Exception caught: "
                                + e.getMessage();
                showAlert(Alert.AlertType.ERROR, "Failed to connect to server.", alertMessage);
            }
        } else {
            log.warning("Couldn't connect to server: " + serverAddress);
            String alertMessage;
            if (serverAddress != null)
                alertMessage = "Address " + serverAddress
                        + ",that you entered, is invalid! Please enter valid IPv4 address.";
            else
                alertMessage = "Please enter server IP address!";

            showAlert(Alert.AlertType.ERROR, "Invalid IP address!", alertMessage);
        }

    }

    @FXML
    void changeColor(Event event) {
        Pane eventPane = (Pane) event.getTarget();
        if (selectedSlot == null) {
            showAlert(Alert.AlertType.INFORMATION, "It's not your slot!",
                    "Please select slot first, then change your color!");
            return;
        }
        if (!selectedSlot.equalsColorPane(eventPane)) {
            showAlert(Alert.AlertType.INFORMATION, "It's not your slot!",
                    "Please change your colour, not somebody else's!");
            return;
        }

        colorPicker.relocate(eventPane.getLayoutX(), eventPane.getLayoutY());
        colorPicker.setOnAction((ActionEvent newEvent) -> {
            playersColor = colorPicker.getValue().toString().substring(2, 10);
            thisPlayer.setPlayersColor(playersColor);
            eventPane.setStyle("-fx-background-color:" + "#" + playersColor);
            root.getChildren().remove(colorPicker);
            colorPickerAdded = false;
        });

        if (!colorPickerAdded) {
            ((Pane) eventPane.getParent()).getChildren().add(colorPicker);
            colorPickerAdded = true;
        }
    }

    @FXML
    void selectGameSlot(Event event) {
        if (!thisPlayer.isNameSet()) {
            showAlert(Alert.AlertType.ERROR, "No name!", "Firstly please enter your name.");
            return;
        }
        Label eventLabel;
        Object object = event.getTarget();
        if (object instanceof Label) {
            eventLabel = (Label) object;

        } else {
            log.warning("Unknown instance of class that caused event!");
            return;
        }

        for (int i = 0; i < playersAmount; i++) {
            if (playersSlots[i].equalsNameLabel(eventLabel)) {
                try {
                    selectSingleSlot(i);
                } catch (GameSlotOccupiedException e) {
                    if (debug)
                        log.info("Selected slot is already taken!");

                    String alertMessage =
                            "Slot, that you selected, is already taken by other player!";
                    showAlert(Alert.AlertType.ERROR, "Slot already taken!", alertMessage);
                }
                return;
            }
        }
    }

    @FXML
    void setPlayersName(Event event) {
        TextField textField = (TextField) event.getTarget();
        playersName = textField.getText();
        thisPlayer.setPlayersName(playersName);

        if (debug)
            log.info("Player's name set to: " + playersName);
    }

    @FXML
    void readyClicked() {
        ready = !ready;
        JSONObject messageToSend = new JSONObject();
        messageToSend.put("cmd", "ready");
        messageToSend.put("id", thisPlayer.getID());
        Platform.runLater(() -> thisPlayer.send(messageToSend.toString()));
    }

    private void selectSingleSlot(int slotNumber) throws GameSlotOccupiedException {
        Platform.runLater(
                () -> thisPlayer.sendSlot(thisPlayer.getSlotId(), slotNumber, playersName));

        /*
         * if (playersSlots[slotNumber].isEmpty()) { if (selectedSlot != null)
         * selectedSlot.freeSlot(); selectedSlot = playersSlots[slotNumber];
         * selectedSlot.takeSlot(playersName);
         * playersSlots[slotNumber].changeColor(thisPlayer.getPlayersColor());
         * log.info("Selected slot number " + slotNumber);
         */

        /*
         * } else if (playersSlots[slotNumber] == selectedSlot) { if (debug)
         * log.info("Unselecting player's slot."); playersSlots[slotNumber].freeSlot(); selectedSlot
         * = null; } else { throw new GameSlotOccupiedException(); }
         */
    }

    public void setPlayersSlot(int idSlot, String nameOfPlayer) {
        playersSlots[idSlot].setPlayersName(nameOfPlayer);
    }
}
