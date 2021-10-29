import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
  /**
   * Click Run to run the application.
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Start Application.
   */
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Bomberman");
    primaryStage.setResizable(false);
    Image icon = new Image("./resources/icon/newlogo.png");
    primaryStage.getIcons().add(icon);
    try {
      Parent root =
          FXMLLoader.load(getClass().getClassLoader().getResource("resources/fxml/MenuScene.fxml"));
      Scene scene = new Scene(root);
      scene.getStylesheets()
          .add(getClass().getResource("resources/fxml/MenuCss.css").toExternalForm());
      primaryStage.setScene(scene);
      primaryStage.show();
      primaryStage.setOnCloseRequest(arg0 -> {
        Platform.exit();
        System.exit(0);
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
