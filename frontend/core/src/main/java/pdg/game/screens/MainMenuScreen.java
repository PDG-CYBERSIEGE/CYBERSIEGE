package pdg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pdg.game.Main;
import pdg.game.scene.Background;

/** Main menu screen that displays the title and connection or play action. */
public class MainMenuScreen implements Screen {
  private static final String TAG = "MainMenuScreen";

  Main game;
  Background background;
  Stage stage;
  boolean isConnected = false;
  TextButton button;
  Skin skin;
  ConnectionScreen connectionScreen;

  /** Creates the main menu and adds its background, title, and action button. */
  public MainMenuScreen(final Main game, Background background) {
    this.game = game;
    this.background = background;
    Gdx.app.log(TAG, "Initializing MainMenuScreen");

    stage = new Stage(new FitViewport(1920, 1080));

    skin = new Skin(Gdx.files.internal("futuristic_ui/uiskin.json"));
    Gdx.app.log(TAG, "UI skin loaded successfully");

    button = new TextButton("Connect", skin, "red");
    button.setSize(450, 150);
    button.getLabel().setFontScale(0.5f);
    button.setPosition((stage.getWidth() - button.getWidth()) / 2f, 200);
    button.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {

            // Open the connection screen until the player has connected.
            if (!isConnected) {
              if (connectionScreen == null) {
                connectionScreen =
                    new ConnectionScreen(
                        game,
                        background,
                        success -> {
                          isConnected = success;

                          game.setScreen(MainMenuScreen.this);
                        });
              }
              game.setScreen(connectionScreen);
            } else {
              // The play-screen transition will be added here.
              // setScreen(new LoadingScreen(this, background));
            }
          }
        });

    Label title = new Label("CYBERSIEGE", skin, "big_title");
    title.pack();
    title.setSize(title.getWidth() + 50, title.getHeight() + 50);
    title.setAlignment(Align.center);
    title.setPosition((stage.getWidth() - title.getWidth()) / 2f, 500);

    background.apply(stage);
    stage.addActor(title);
    stage.addActor(button);
  }

  /** Activates input and refreshes the button appearance for the connection state. */
  @Override
  public void show() {

    Gdx.input.setInputProcessor(stage);

    if (isConnected) {
      button.getLabel().setText("Play");
      button.setStyle(skin.get("green", TextButton.TextButtonStyle.class));
    } else {
      button.getLabel().setText("Connect");
      button.setStyle(skin.get("red", TextButton.TextButtonStyle.class));
    }
  }

  /** Updates and draws the menu using the fixed 1920x1080 viewport. */
  @Override
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
  public void dispose() {
    Gdx.app.log(TAG, "Disposing MainMenuScreen resources");
    if (connectionScreen != null) {
      connectionScreen.dispose();
      Gdx.app.log(TAG, "Disposed ConnectionScreen");
    }
    if (stage != null) {
      stage.dispose();
    }
    if (skin != null) {
      skin.dispose();
    }
  }
}
