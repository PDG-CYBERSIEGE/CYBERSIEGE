package pdg.game.scene;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/** Manages the five layered images used for a randomly selected background. */
public class Background {

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
      images[i - 1].setFillParent(true);
    }
  }

  /** Adds all background layers to the stage in their rendering order. */
  public void apply(Stage stage) {

    for (int i = 0; i < 5; i++) {
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
