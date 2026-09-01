package pdg.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import pdg.game.network.http.AuthClient;
import pdg.game.network.http.HttpClient;
import pdg.game.network.http.ResponseListener;
import pdg.game.network.websocket.GameListener;
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
