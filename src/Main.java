
import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import entities.Bomber;
import entities.Entity;
import entities.Grass;
import entities.Wall;
import graphics.Sprite;

public class Main extends Application {

  public static final int WIDTH = 20;
  public static final int HEIGHT = 15;

  private GraphicsContext gc;
  private Canvas canvas;
  private List<Entity> entities = new ArrayList<>();
  private List<Entity> stillObjects = new ArrayList<>();


  public static void main(String[] args) {
    Application.launch(Main.class);
  }

  @Override
  public void start(Stage stage) {
    // Tao Canvas
    canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH * 1.0, Sprite.SCALED_SIZE * HEIGHT * 1.0);
    gc = canvas.getGraphicsContext2D();

    // Tao root container
    Group root = new Group();
    root.getChildren().add(canvas);

    // Tao scene
    Scene scene = new Scene(root);

    // Them scene vao stage
    stage.setScene(scene);
    stage.show();

    AnimationTimer timer = new AnimationTimer() {
      @Override
      public void handle(long l) {
        render();
        update();
      }
    };
    timer.start();

    createMap();

    Entity bomberman = new Bomber(1, 1, Sprite.playerRight.getFxImage());
    entities.add(bomberman);
  }

  public void createMap() {
    for (int i = 0; i < WIDTH; i++) {
      for (int j = 0; j < HEIGHT; j++) {
        Entity object;
        if (j == 0 || j == HEIGHT - 1 || i == 0 || i == WIDTH - 1) {
          object = new Wall(i, j, Sprite.wall.getFxImage());
        } else {
          object = new Grass(i, j, Sprite.grass.getFxImage());
        }
        stillObjects.add(object);
      }
    }
  }

  public void update() {
    entities.forEach(Entity::update);
  }

  public void render() {
    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    stillObjects.forEach(g -> g.render(gc));
    entities.forEach(g -> g.render(gc));
  }
}