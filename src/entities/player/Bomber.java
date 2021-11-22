package entities.player;

import Bomb.Bomb;
import Bomb.Flame;
import GameFrame.KeyboardInput;
import GameMain.BombermanGame;
import entities.Entity;
import entities.stillobjects.Brick;
import entities.stillobjects.Grass;
import entities.stillobjects.Wall;
import graphics.Sprite;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import sounds.Sound;

public class Bomber extends BomberCharacter {
  private KeyboardInput input;
  private int maxBom = 1;
  private int frameLen = 1;
  public boolean canPassBom = false;
  public boolean canPassFlame = false;
  private List<Bomb> bombList = new ArrayList<>();

  private Sound soundPlaceBomb = new Sound(Sound.PLACE_BOMB_SOUND);
  private Sound soundMoving = new Sound(Sound.MOVING_SOUND);

  private final int[] addToXToCheckCollision =
      {2, Sprite.SCALED_SIZE - 10, Sprite.SCALED_SIZE - 10, 2};
  private final int[] addToYToCheckCollision =
      {3, 3, Sprite.SCALED_SIZE - 6, Sprite.SCALED_SIZE - 6};
  private final int[] addToXToSetPrecision = {0, Sprite.SCALED_SIZE, Sprite.SCALED_SIZE - 20, 10};
  private final int[] addToYToCSetPrecision =
      {10, 10, Sprite.SCALED_SIZE + 1, Sprite.SCALED_SIZE + 1};


  /**
   * Create a bomber that react to user input.
   */
  public Bomber(int x, int y, KeyboardInput keyboardInput) {
    super(x, y, Sprite.playerDown.getFxImage());
    direction = 1;
    velocity = 2;
    input = keyboardInput;
  }

  @Override
  public void update() {
    animate();
    bombUpdate();

    input = BombermanGame.getCanvasGame().getInput();

    if (input.space) {
      if (bombList.size() < maxBom) {
        Entity e = BombermanGame.getCanvasGame().getEntityInCoodinate(getXUnit(), getYUnit());
        if (e == null) {
          bombList.add(new Bomb(getXUnit(), getYUnit(), frameLen, this));
          // if (!soundPlaceBomb.isRunning()) soundPlaceBomb.play();
        }
      }
    }
    if (input.up || input.right || input.left || input.down) {
      setMoving(true);
    } else {
      setMoving(false);
      switch (direction) {
        case 0:
          this.setImg(Sprite.playerUp.getFxImage());
          break;
        case 1:
          this.setImg(Sprite.playerDown.getFxImage());
          break;
        case 2:
          this.setImg(Sprite.playerLeft.getFxImage());
          break;
        case 3:
          this.setImg(Sprite.playerRight.getFxImage());
          break;
        default:
          break;
      }
    }
    if (isMoving()) {
      // setPrecision();
      if (!soundMoving.isRunning()) {
        soundMoving.play();
      }
      calculateMove();
    } else {
      soundMoving.stop();
    }
  }

  @Override
  public void render() {}

  @Override
  public void calculateMove() {
    input = BombermanGame.getCanvasGame().getInput();
    // setPrecision();
    if (input.up) {
      y -= velocity;
      // setPrecision(input);
      if (!canMove()) {
        y += velocity;
      }
      setDirection(0);
      this.setImg(Sprite.movingSprite(Sprite.playerUp, Sprite.playerUp1, Sprite.playerUp2,
          animation, timeTransfer).getFxImage());
    } else if (input.down) {
      y += velocity;

      if (!canMove()) {
        setPrecision(input);
        y -= velocity;
      }
      setDirection(1);
      this.setImg(Sprite.movingSprite(Sprite.playerDown, Sprite.playerDown1, Sprite.playerDown2,
          animation, timeTransfer).getFxImage());
    } else if (input.left) {
      x -= velocity;
      if (!canMove()) {
        setPrecision(input);
        x += velocity;
      }
      setDirection(2);
      this.setImg(Sprite.movingSprite(Sprite.playerLeft, Sprite.playerLeft1, Sprite.playerLeft2,
          animation, timeTransfer).getFxImage());
    } else {
      x += velocity;
      if (!canMove()) {
        setPrecision(input);
        x -= velocity;

      }
      setDirection(3);
      this.setImg(Sprite.movingSprite(Sprite.playerRight, Sprite.playerRight1, Sprite.playerRight2,
          animation, timeTransfer).getFxImage());
    }
  }

  @Override
  public boolean canMove() {
    for (int i = 0; i < 4; i++) { // check collision for 4 corners
      int newX = (getX() + addToXToCheckCollision[i]) / Sprite.SCALED_SIZE;
      int newY = (getY() + addToYToCheckCollision[i]) / Sprite.SCALED_SIZE;
      Entity e = BombermanGame.getCanvasGame().getEntityInCoodinate(newX, newY);

      if (e instanceof Wall || e instanceof Brick) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void setPrecision(KeyboardInput input) {
    for (int i = 0; i < 4; i++) {
      int newX = (getX() + addToXToCheckCollision[i]) / Sprite.SCALED_SIZE;
      int newY = (getY() + addToYToCheckCollision[i]) / Sprite.SCALED_SIZE;
      Entity collision = BombermanGame.getCanvasGame().getEntityInCoodinate(newX, newY);
      int preX = (getX() + addToXToSetPrecision[i]) / Sprite.SCALED_SIZE;
      int preY = (getY() + addToYToCSetPrecision[i]) / Sprite.SCALED_SIZE;
      Entity check = BombermanGame.getCanvasGame().getGrass(preX, preY);
      boolean isCollided = collision instanceof Wall || collision instanceof Brick;

      if (check instanceof Grass && isCollided && i == 0) {
        y = preY * Sprite.SCALED_SIZE;
      }

      if (check instanceof Grass && isCollided && i == 1) {
        y = preY * Sprite.SCALED_SIZE;
      }

      if (check instanceof Grass && isCollided && i == 2) {
        x = preX * Sprite.SCALED_SIZE + 4;
      }

      if (check instanceof Grass && isCollided && i == 3) {
        x = preX * Sprite.SCALED_SIZE;
      }
    }
  }

  public List<Bomb> getBombList() {
    return bombList;
  }
  //
  // public void setCanPassBom(boolean canPassBom) {
  // this.canPassBom = canPassBom;
  // }
  //
  // public boolean isCanPassBom() {
  // return canPassBom;
  // }

  public void bombRender(GraphicsContext gc) {
    for (Bomb b : bombList) {
      if (b.isExplored()) {
        b.frameRender(gc);
      } else {
        b.render(gc);
      }
    }
  }

  /**
   * Update bomb.
   */
  public void bombUpdate() {
    bombList.forEach(Bomb::update);
    for (Bomb b : bombList) {
      if (b.getImg() == null) {
        bombList.remove(b);
        break;
      } else {
        for (Flame fl : b.getFlameList()) {
          fl.update();
        }
      }
    }
  }
}
