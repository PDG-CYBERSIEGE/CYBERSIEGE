package pdg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import pdg.game.Entity.Robot;

public class RobotChoiceButton extends Actor {

    private Texture image;
    private boolean imageVisible = true;

    private Color borderColor;
    private float borderThickness;

    private final Texture whitePixel;

    public final Robot robot;
    private boolean robotLoaded;

    public RobotChoiceButton(Robot robot) {
      this.image = robot.getSprite();
      this.robot = robot;
      this.borderColor = Color.WHITE;
      this.borderThickness = 2f;
      this.whitePixel = createWhitePixel();
      setSize(image.getWidth(), image.getHeight());
    }

    private Texture createWhitePixel() {
      Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
      pixmap.setColor(Color.WHITE);
      pixmap.fill();
      Texture texture = new Texture(pixmap);
      pixmap.dispose();
      return texture;
    }

    public void setImageVisible(boolean visible) {
      this.imageVisible = visible;
    }

    public void setBorderColor(Color color) {
      this.borderColor = color;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
      Color prevColor = batch.getColor().cpy();

      // contour : 4 bandes (bas, haut, gauche, droite)
      batch.setColor(borderColor);
      batch.draw(whitePixel, getX(), getY(), getWidth(), borderThickness);
      batch.draw(whitePixel, getX(), getY() + getHeight() - borderThickness, getWidth(), borderThickness);
      batch.draw(whitePixel, getX(), getY(), borderThickness, getHeight());
      batch.draw(whitePixel, getX() + getWidth() - borderThickness, getY(), borderThickness, getHeight());

      // image (optionnelle), à l'intérieur du contour
      if (imageVisible) {
        batch.setColor(prevColor);
        batch.draw(image,
          getX() + borderThickness, getY() + borderThickness,
          getWidth() - 2 * borderThickness, getHeight() - 2 * borderThickness);
      }

      batch.setColor(prevColor);
    }

    public void dispose() {
      whitePixel.dispose();
    }

    public boolean isRobotLoaded() {
      return robotLoaded;
    }

    public void load() {
      robotLoaded = true;
    }

    public void unload() {
      robotLoaded = false;
    }
}
