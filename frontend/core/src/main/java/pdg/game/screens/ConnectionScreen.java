package pdg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.function.Consumer;
import pdg.game.Main;
import pdg.game.network.AuthClient;
import pdg.game.network.HttpClient;
import pdg.game.network.ResponseListener;
import pdg.game.scene.Background;
import pdg.game.ui.Frame;

/**
 * ConnectionScreen - Handles user login functionality.
 *
 * <p>This screen displays a login interface where users can enter their username and password to
 * authenticate with the server. It also provides navigation to the registration screen for new
 * users.
 *
 * <p>Features: - Username and password input fields with placeholder text - Login validation and
 * authentication via AuthClient - Navigation to RegisterScreen for new user registration - Callback
 * mechanism to notify parent screen of authentication success/failure
 */
public class ConnectionScreen implements Screen {
  // Logging tag
  private static final String TAG = "ConnectionScreen";

  // Game reference for screen management
  private Main game;

  // Background and stage for rendering
  private Background background;
  private Stage stage;

  // UI resources
  private Skin skin;

  // HTTP client for server communication
  private HttpClient httpClient;

  // Authentication client for server communication
  private AuthClient authClient;

  // Register screen reference (lazy-loaded)
  private Screen registerScreen;

  // Callback for screen transitions
  private Consumer<Boolean> callback;

  // Input fields
  private TextField username;
  private TextField password;

  private Label errorMessage;

  private ImageButton showPassword;

  // UI styling constants
  private static final Color BASE_COLOR = Color.GRAY;
  private static final Color EDIT_COLOR = Color.WHITE;
  private static final String BASE_TEXT = "type here";

  /**
   * Constructor for ConnectionScreen.
   *
   * <p>Initializes the login screen with UI components including username/password fields and
   * login/register/cancel buttons.
   *
   * @param game The main game instance for screen transitions
   * @param background The background to display behind the UI
   * @param callback Callback to handle screen transition results
   */
  public ConnectionScreen(final Main game, Background background, Consumer<Boolean> callback) {
    this.game = game;
    this.background = background;
    this.callback = callback;

    // Initialize HTTP and authentication clients
    // httpClient = new HttpClient("http://localhost:8080"); // for local testing
    httpClient = new HttpClient("http://labo-iot5.iict-heig-vd.ch:8080"); // for release

    authClient = new AuthClient(httpClient);

    // Setup stage with viewport and background
    stage = new Stage(new FitViewport(1920, 1080));
    background.apply(stage);

    // Load UI skin
    skin = new Skin(Gdx.files.internal("futuristic_ui/uiskin.json"));

    // Create error message label
    errorMessage = new Label("", skin);
    errorMessage.setColor(Color.RED);
    errorMessage.setWrap(true);

    // Create and configure username input field
    Label usernameLabel = new Label("username:", skin);
    username = new TextField(BASE_TEXT, skin);
    username.setStyle(
        new TextField.TextFieldStyle(username.getStyle()) {
          {
            fontColor = BASE_COLOR;
          }
        });
    username.addListener(createInputFocusListener(username, false));

    // Create and configure password input field
    Label passwordLabel = new Label("password:", skin);
    password = new TextField(BASE_TEXT, skin);
    password.setStyle(
        new TextField.TextFieldStyle(password.getStyle()) {
          {
            fontColor = BASE_COLOR;
          }
        });

    showPassword = new ImageButton(skin, "eye");
    showPassword.setVisible(false);

    password.addListener(createInputFocusListener(password, true));
    showPassword.addListener(createPasswordRevealListener(password));

    // Create cancel button
    TextButton cancel = new TextButton("cancel", skin, "red");
    cancel.setSize(150, 75);
    cancel.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            callback.accept(false);
          }
        });

    // Create register button (navigates to RegisterScreen)
    TextButton register = new TextButton("register", skin, "red");
    register.setSize(150, 75);
    register.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            navigateToRegisterScreen();
          }
        });

    // Create login button
    TextButton connect = new TextButton("connect", skin, "green");
    connect.setSize(150, 75);
    connect.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            handleLogin();
          }
        });

    // Build UI frame with labels and input fields
    Frame frame = new Frame(600, 600, "Connection", cancel, register, connect);
    frame.setPosition(
        stage.getWidth() / 2f - frame.getWidth() / 2f,
        stage.getHeight() / 2f - frame.getHeight() / 2f);

    frame.getContent().add(usernameLabel).align(Align.left).expandX().padLeft(10).padRight(60);
    frame.getContent().add().size(40, 40).padRight(10);
    frame.getContent().row();

    frame.getContent().add(username).align(Align.left).fillX().expandX().pad(10);
    frame.getContent().add().size(40, 40).padRight(10);
    frame.getContent().row();

    frame.getContent().add(passwordLabel).align(Align.left).expandX().padLeft(10).padRight(60);
    frame.getContent().add().size(40, 40).padRight(10);
    frame.getContent().row();

    frame.getContent().add(password).align(Align.left).fillX().expandX().pad(10);
    frame.getContent().add(showPassword).size(40, 40).padRight(10);
    frame.getContent().row();

    frame.getContent().add(errorMessage).align(Align.left).fillX().expandX().colspan(2).pad(10);
    frame.getContent().row();
    errorMessage.setWrap(true);

    stage.addActor(frame);
  }

  /**
   * Creates a focus listener for input fields that clears placeholder text when focused. Handles
   * password field initialization with masking and character display.
   *
   * @param field The input field to listen to
   * @param isPasswordField Whether this field should be masked as a password
   * @return A FocusListener configured for the field
   */
  private FocusListener createInputFocusListener(TextField field, boolean isPasswordField) {
    return new FocusListener() {
      @Override
      public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
        if (focused && field.getStyle().fontColor == BASE_COLOR) {
          field.setText("");
          field.getStyle().fontColor = EDIT_COLOR;

          // Configure password field with masking
          if (isPasswordField) {
            field.setPasswordMode(true);
            field.setPasswordCharacter('•');
            showPassword.setVisible(true);
          }
        }
      }
    };
  }

  /**
   * Creates a click listener for password visibility toggle button. Shows password on touch down,
   * hides on touch up.
   *
   * @param passwordField The password field to control
   * @return A ClickListener for password visibility toggle
   */
  private ClickListener createPasswordRevealListener(TextField passwordField) {
    return new ClickListener() {
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        passwordField.setPasswordMode(false);
        return true;
      }

      @Override
      public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        passwordField.setPasswordMode(true);
      }
    };
  }

  /** Navigates to the registration screen. Lazily initializes RegisterScreen on first access. */
  private void navigateToRegisterScreen() {
    if (registerScreen == null) {
      registerScreen = new RegisterScreen(background, authClient, callback);
    }
    game.setScreen(registerScreen);
  }

  /**
   * Handles the login process. Validates input fields and sends authentication request to server.
   * Displays error messages on failure and navigates to main game on success.
   */
  private void handleLogin() {
    String usernameValue = username.getText();
    String passwordValue = password.getText();

    if (usernameValue.equals(BASE_TEXT)
        || usernameValue.isEmpty()
        || passwordValue.equals(BASE_TEXT)
        || passwordValue.isEmpty()) {
      errorMessage.setText("All fields are required");
      return;
    }
    // Clear any previous error messages
    errorMessage.setText("");

    // Send login request to authentication service
    authClient.login(
        usernameValue,
        passwordValue,
        new ResponseListener() {
          @Override
          public void success(String token) {
            authClient.setToken(token);
            Gdx.app.postRunnable(() -> callback.accept(true));
          }

          @Override
          public void failure(int status, String result) {
            Gdx.app.error(TAG, "Login failed with status " + status + ": " + result);
            errorMessage.setText(result);
          }

          @Override
          public void error(String message) {
            Gdx.app.error(TAG, "Login network error: " + message);
            errorMessage.setText("Network error");
          }
        });
  }

  /**
   * Called when the screen becomes active. Resets UI elements to initial state and configures input
   * processor.
   */
  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);
    stage.setKeyboardFocus(null);
    resetInputFields();
    showPassword.setVisible(false);
    errorMessage.setText("");
  }

  /** Resets all input fields to their placeholder state. */
  private void resetInputFields() {
    username.getStyle().fontColor = BASE_COLOR;
    username.setText(BASE_TEXT);
    password.getStyle().fontColor = BASE_COLOR;
    password.setText(BASE_TEXT);
  }

  /**
   * Renders the screen each frame. Clears screen and updates stage with delta time.
   *
   * @param delta Time in seconds since last frame
   */
  @Override
  public void render(float delta) {
    // Clear screen with black color
    ScreenUtils.clear(0, 0, 0, 1);

    // Update and render stage
    stage.getViewport().apply();
    stage.act(delta);
    stage.draw();
  }

  /**
   * Called when screen is resized. Updates viewport to match new dimensions.
   *
   * @param width New screen width in pixels
   * @param height New screen height in pixels
   */
  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  /** Called when application is paused. */
  @Override
  public void pause() {}

  /** Called when application is resumed. */
  @Override
  public void resume() {}

  /** Called when screen is hidden or replaced. */
  @Override
  public void hide() {}

  /**
   * Called when screen is disposed. Cleans up resources including stage, skin, and input processor.
   */
  @Override
  public void dispose() {
    if (registerScreen != null) {
      registerScreen.dispose();
    }
    if (stage != null) {
      stage.dispose();
    }
    if (skin != null) {
      skin.dispose();
    }
  }
}
