package pdg.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import pdg.game.network.websocket.GameWebSocket;
import pdg.game.scene.Background;
import pdg.game.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

  private final GameWebSocket gameWebSocket;

  public Main(GameWebSocket gameWebSocket) {
    this.gameWebSocket = gameWebSocket;
  }

  @Override
  public void create() {
    Background background = new Background();
    Stage backgroundStage = new Stage(new FitViewport(1920, 1080));
    background.apply(backgroundStage);
    Gdx.app.setLogLevel(Application.LOG_DEBUG);
    setScreen(new MainMenuScreen(this, backgroundStage, gameWebSocket));
  }
}
