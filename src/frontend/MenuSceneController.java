package frontend;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class MenuSceneController implements Initializable {

    @FXML
    public StackPane parentRoot;
    @FXML
    public AnchorPane MenuScene;
    @FXML
    public Button StartButton;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub

    }

    @FXML
    private void startButtonClicked(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getClassLoader().getResource("resources/fxml/MapChooseScene.fxml"));
            Scene scene = StartButton.getScene();
            root.translateYProperty().set(scene.getHeight());
            parentRoot.getChildren().add(root);

            Timeline timeline = new Timeline();
            KeyValue kValue = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), kValue);
            timeline.getKeyFrames().add(keyFrame);
            timeline.setOnFinished(e -> {
                parentRoot.getChildren().remove(MenuScene);
            });
            timeline.play();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
