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
import java.util.ArrayList;
import pdg.game.DTO.BlockDTO;
import pdg.game.DTO.KingDTO;
import pdg.game.DTO.RobotDTO;
import pdg.game.DTO.TeamDTO;
import pdg.game.Main;
import pdg.game.network.http.AuthClient;
import pdg.game.network.http.HttpClient;
import pdg.game.network.http.ResponseListener;
import pdg.game.network.websocket.GameListener;
import pdg.game.network.websocket.GameMessages;
import pdg.game.network.websocket.GameWebSocket;

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
  String username;
  Skin skin;

  HttpClient httpClient;
  AuthClient authClient;
  GameWebSocket gameWebSocket;
  boolean isConnecting = false;

  /**
   * Creates the main menu screen with background, title, and action button.
   *
   * @param game the main game instance for screen transitions
   * @param backgroundStage the stage containing the background that persists across screens
   */
  public MainMenuScreen(final Main game, Stage backgroundStage, GameWebSocket gameWebSocket) {
    this.game = game;
    this.backgroundStage = backgroundStage;
    this.gameWebSocket = gameWebSocket;

    // Initialize HTTP and authentication clients
    httpClient = new HttpClient("http://localhost:8080"); // for local testing
    authClient = new AuthClient(httpClient);

    stage = new Stage(new FitViewport(1920, 1080));
    skin = new Skin(Gdx.files.internal("futuristic_ui/uiskin.json"));

    // Create and configure the connection/play button
    button = new TextButton("Connect", skin, "red_large");
    button.setSize(550, 150);
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
              connectToMatch();
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

    button.setDisabled(false);
    if (isConnected) {
      button.getLabel().setText("Play");
      button.setStyle(skin.get("green_large", TextButton.TextButtonStyle.class));
      setUsername();
    } else {
      button.getLabel().setText("Connect");
      button.setStyle(skin.get("red_large", TextButton.TextButtonStyle.class));
    }

    isConnecting = false;
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
          public void success(String _username) {
            username = _username;
            Gdx.app.log(TAG, "Retrieved username: " + _username);
            usernameLabel.setText("connected as " + _username);
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

  private void connectToMatch() {
    if (isConnecting || button.isDisabled()) {
        return;
    }

    button.getLabel().setText("Connecting...");
    button.setDisabled(true);

    String token = authClient.getToken();
    if (token == null || token.isEmpty()) {
      Gdx.app.error(TAG, "Cannot join a match without an authentication token");
      return;
    }

    Gdx.app.log(TAG, "Attempting to connect to matchmaking with token: " + token);
    isConnecting = true;
    gameWebSocket.connect(
        "http://localhost:8080",
        token,
        new GameListener() {
          @Override
          public void onConnected() {
            Gdx.app.log(TAG, "Connected to matchmaking");
            gameWebSocket.send(GameMessages.buildValidate(createPlacedTeam()));
          }

          @Override
          public void onMessage(String message) {
            Gdx.app.log(TAG, "Match message: " + message);
          }

          @Override
          public void onDisconnected() {
            isConnecting = false;
            isConnected = false;
            button.getLabel().setText("Connect");
            button.setStyle(skin.get("red_large", TextButton.TextButtonStyle.class));
            button.setDisabled(false);
          }

          @Override
          public void onError(String message) {
            isConnecting = false;
            isConnected = false;
            Gdx.app.error(TAG, message);
            button.getLabel().setText("Connect");
            button.setStyle(skin.get("red_large", TextButton.TextButtonStyle.class));
            button.setDisabled(false);
          }
        });
  }

  private TeamDTO createPlacedTeam() {
    ArrayList<BlockDTO> blocks = new ArrayList<>();
    blocks.add(new BlockDTO("block", 100, 10, true, 5, 1, 3));
    blocks.add(new BlockDTO("block", 100, 10, true, 5, 2, 3));

    ArrayList<RobotDTO> robots = new ArrayList<>();
    robots.add(new RobotDTO("basic", 100, 10, 0));

    return new TeamDTO(username, blocks, robots, new KingDTO("king", 6, 3, 100, 20));
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
