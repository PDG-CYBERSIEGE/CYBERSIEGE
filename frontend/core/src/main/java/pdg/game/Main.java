package pdg.game;

import com.badlogic.gdx.Game;
import pdg.game.screens.FightScreen;
import pdg.game.screens.FirstScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
  @Override
  public void create() {
    setScreen(new FightScreen(this));
  }
}
