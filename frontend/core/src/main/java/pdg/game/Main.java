package pdg.game;

import com.badlogic.gdx.Game;
import pdg.game.network.websocket.GameWebSocket;
import pdg.game.screens.FirstScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

  private final GameWebSocket gameWebSocket;

  public Main(GameWebSocket gameWebSocket) {
    this.gameWebSocket = gameWebSocket;
  }

  @Override
  public void create() {
    setScreen(new FirstScreen(this));
  }
}
