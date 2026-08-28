package pdg.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.function.Consumer;

public class ConnectionScreen implements Screen {
  MainMenuScreen mainMenu;
  Stage backgroundStage;
  Stage stage;
  boolean isConnected = false;
  TextButton button;
  Consumer<Boolean> callback;

  public ConnectionScreen(Stage backgroundStage, Consumer<Boolean> callback) {

    this.backgroundStage = backgroundStage;
    this.callback = callback;

    stage = new Stage(new FitViewport(1920, 1080));
  }

  public void show() {

    callback.accept(true);
  }

  public void render(float delta) {

    ScreenUtils.clear(0, 0, 0, 1);

    stage.getViewport().apply();
    stage.act(delta);
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {}
}
