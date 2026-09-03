package pdg.game.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import java.util.Random;

/** Manages the five layered images used for a randomly selected background. */
public class Background {

  private static final float SCALE_FACTOR = 1.7f; // 1.0 = taille exacte du stage, >1 = plus grand

  String path;
  Random random;
  Image[] images = new Image[5];

  /** Loads a random background and its five image layers. */
  public Background() {

    random = new Random();

    // Background 0 has no day/night variant; the other backgrounds do.
    int randomBackground = random.nextInt(10);
    path = "background/" + randomBackground + "/";

    if (randomBackground != 0) {
      randomBackground = random.nextInt(2);
      path += randomBackground == 1 ? "Day/" : "Night/";
    }
    // Load each layer separately so Scene2D can render them in order.
    for (int i = 1; i <= 5; i++) {
      Texture backgroundTexture = new Texture(Gdx.files.internal(path + i + ".png"));
      images[i - 1] = new Image(backgroundTexture);
    }
  }

  /** Adds all background layers to the stage in their rendering order, agrandis et centrés. */
  public void apply(Stage stage) {
    float width = stage.getWidth() * SCALE_FACTOR;
    float height = stage.getHeight() * SCALE_FACTOR;
    float x = (stage.getWidth() - width) / 2f;
    float y = (stage.getHeight() - height) / 2f;

    for (int i = 0; i < 5; i++) {
      images[i].setSize(width, height);
      images[i].setPosition(x, y);
      stage.addActor(images[i]);
    }
  }

  /** Replaces the current layers with a newly selected random background. */
  public void change() {

    int randomBackground = random.nextInt(10);
    path = "background/" + randomBackground + "/";

    if (randomBackground != 0) {
      randomBackground = random.nextInt(2);
      path += randomBackground == 1 ? "Day/" : "Night/";
    }
    // Update existing actors so callers do not need to reapply the background.
    for (int i = 1; i <= 5; i++) {
      Texture backgroundTexture = new Texture(Gdx.files.internal(path + i + ".png"));
      images[i - 1].setDrawable(new Image(backgroundTexture).getDrawable());
    }
  }
}
