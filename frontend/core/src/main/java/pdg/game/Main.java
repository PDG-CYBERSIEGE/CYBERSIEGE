package pdg.game;

import com.badlogic.gdx.Game;
import pdg.game.scene.Background;
import pdg.game.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
  @Override
  public void create() {
    Background background = new Background();
    setScreen(new MainMenuScreen(this, background));
  }
}
