package pdg.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Game extends com.badlogic.gdx.Game {
  private SpriteBatch batch;

  private Texture image;

  @Override
  public void create() {
    batch = new SpriteBatch();

    // set mainScreen

    // test avec le screen de jeu
  }

  @Override
  public void render() {
    super.render();
  }

  @Override
  public void dispose() {
    batch.dispose();
  }
}
