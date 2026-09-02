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
import pdg.game.network.http.AuthClient;
import pdg.game.network.http.HttpClient;
import pdg.game.network.http.ResponseListener;

/**
 * Main menu screen that displays the title, connection button, and authenticated user information.
 */
public class MainMenuScreen implements Screen {

  private static final String TAG = "MainMenuScreen";

  final Main game;
  Stage backgroundStage;
  Stage stage;
  boolean isConnected = false;
  TextButton button;
  Label title, usernameLabel;
  Skin skin;

  HttpClient httpClient;
  AuthClient authClient;

  /**
   * Creates the main menu screen with background, title, and action button.
   *
   * @param game the main game instance for screen transitions
   * @param backgroundStage the stage containing the background that persists across screens
   */
  public MainMenuScreen(final Main game, Stage backgroundStage) {
    this.game = game;
    this.backgroundStage = backgroundStage;

    // Initialize HTTP and authentication clients
    httpClient = new HttpClient("http://localhost:8080"); // for local testing
    authClient = new AuthClient(httpClient);

    stage = new Stage(new FitViewport(1920, 1080));
    skin = new Skin(Gdx.files.internal("futuristic_ui/uiskin.json"));

    // Create and configure the connection/play button
    button = new TextButton("Connect", skin, "red_large");
    button.setSize(450, 150);
    button.getLabel().setFontScale(0.5f);
    button.setPosition((stage.getWidth() - button.getWidth()) / 2f, 200);
    button.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            if (!isConnected) {
              // Navigate to connection screen
              game.setScreen(
                  new ConnectionScreen(
                      game,
                      backgroundStage,
                      authClient,
                      success -> {
                        isConnected = success;
                        game.setScreen(MainMenuScreen.this);
                      }));
            } else {
              // TODO: Transition to gameplay screen when implemented
              // use a consumer callback like for connectionscreen to update isConnected, token
              // could expire and we need to handle that
            }
          }
        });

    // Create and configure the title label
    title = new Label("CYBERSIEGE", skin, "big_title_thick");
    title.pack();
    title.setSize(title.getWidth() + 50, title.getHeight() + 50);
    title.setAlignment(Align.center);
    title.setPosition((stage.getWidth() - title.getWidth()) / 2f, 500);

    // Create username label (populated after authentication)
    usernameLabel = new Label("", skin);

    stage.addActor(title);
    stage.addActor(usernameLabel);
    stage.addActor(button);
  }

  /**
   * Called when the screen becomes active. Updates button state based on connection status and
   * fetches the username if connected.
   */
  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);

    if (isConnected) {
      button.getLabel().setText("Play");
      button.setStyle(skin.get("green_large", TextButton.TextButtonStyle.class));
      setUsername();
    } else {
      button.getLabel().setText("Connect");
      button.setStyle(skin.get("red_large", TextButton.TextButtonStyle.class));
    }
  }

  /**
   * Renders the screen each frame, updating and drawing both background and UI stages.
   *
   * @param delta time in seconds since last frame
   */
  @Override
  public void render(float delta) {
    ScreenUtils.clear(0, 0, 0, 1);

    backgroundStage.getViewport().apply();
    backgroundStage.act(delta);
    backgroundStage.draw();

    stage.getViewport().apply();
    stage.act(delta);
    stage.draw();
  }

  /**
   * Called when the screen is resized. Updates viewports to match new dimensions.
   *
   * @param width new screen width in pixels
   * @param height new screen height in pixels
   */
  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
    backgroundStage.getViewport().update(width, height, true);
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  /**
   * Fetches and displays the authenticated user's username. Called when the connection is
   * successful.
   */
  private void setUsername() {
    authClient.getUsername(
        new ResponseListener() {
          @Override
          public void success(String username) {
            Gdx.app.log(TAG, "Retrieved username: " + username);
            usernameLabel.setText("connected as " + username);
            usernameLabel.pack();
            usernameLabel.setPosition(
                stage.getWidth() - usernameLabel.getWidth() - 20,
                stage.getHeight() - usernameLabel.getHeight() - 20);
          }

          @Override
          public void failure(int status, String result) {
            Gdx.app.error(TAG, "Failed to retrieve username with status " + status + ": " + result);
          }

          @Override
          public void error(String message) {
            Gdx.app.error(TAG, "Network error while retrieving username: " + message);
          }
        });
  }

  @Override
  public void dispose() {
    if (stage != null) {
      stage.dispose();
    }
    if (skin != null) {
      skin.dispose();
    }
    if (backgroundStage != null) {
      backgroundStage.dispose();
    }
  }
}
