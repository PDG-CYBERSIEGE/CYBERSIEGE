package pdg.game.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Manages the five layered images used for a randomly selected background. */
public class Background {

  private static final float SCALE_FACTOR = 1.7f; // 1.0 = taille exacte du stage, >1 = plus grand
  private static final String TAG = "Background";
  private static final int NUM_LAYERS = 5;

  String path;
  Random random;
  Image[] images = new Image[NUM_LAYERS];
  List<Texture> textures = new ArrayList<>();

  /** Loads a random background and its five image layers. */
  public Background() {

    random = new Random();
    loadRandomBackground();
    Gdx.app.log(TAG, "Background initialized with path: " + path);
  }

  /** Loads a random background configuration and creates image layers. */
  private void loadRandomBackground() {
    // Background 0 has no day/night variant; the other backgrounds do.
    int randomBackground = random.nextInt(10);
    path = "background/" + randomBackground + "/";

    if (randomBackground != 0) {
      randomBackground = random.nextInt(2);
      path += randomBackground == 1 ? "Day/" : "Night/";
    }
    // Load each layer separately so Scene2D can render them in order.
    for (int i = 1; i <= NUM_LAYERS; i++) {
      Texture backgroundTexture = new Texture(Gdx.files.internal(path + i + ".png"));
      textures.add(backgroundTexture);
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
    Gdx.app.log(TAG, "Changing background");
    // Dispose old textures to prevent memory leaks
    disposeTextures();
    textures.clear();
    loadRandomBackground();
    Gdx.app.log(TAG, "Background changed to path: " + path);
  }

  /** Disposes all loaded textures. */
  public void dispose() {
    Gdx.app.log(TAG, "Disposing Background resources");
    disposeTextures();
  }

  /** Helper method to dispose all textures in the textures list. */
  private void disposeTextures() {
    for (Texture texture : textures) {
      if (texture != null) {
        texture.dispose();
      }
    }
  }
}
