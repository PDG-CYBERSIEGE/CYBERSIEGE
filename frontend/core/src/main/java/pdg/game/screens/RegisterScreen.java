package pdg.game.screens;

import java.util.function.Consumer;

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

import pdg.game.network.AuthClient;
import pdg.game.network.ResponseListener;
import pdg.game.scene.Background;
import pdg.game.ui.Frame;

/**
 * RegisterScreen - Handles new user registration.
 *
 * <p>This screen displays a registration form where new users can create an account by providing
 * username, email, password, and password confirmation. It validates user input and communicates
 * with the server for account creation.
 *
 * <p>Features: - Username, email, and password input fields with placeholder text - Password
 * confirmation validation - Error message display for failed registrations - Server-side
 * registration via AuthClient - Callback mechanism to notify parent screen of registration
 * success/failure
 */
public class RegisterScreen implements Screen {
  // Logging tag
  private static final String TAG = "RegisterScreen";

  // Background and stage for rendering
  private Background background;
  private Stage stage;

  // UI resources
  private Skin skin;

  // Authentication client for server communication
  private AuthClient authClient;

  // Callback for screen transitions
  private Consumer<Boolean> callback;

  // Input fields
  private TextField username;
  private TextField mail;
  private TextField password;
  private TextField confirmPassword;

  // Error message label
  private Label errorMessage;

  // Password visibility toggle buttons
  private ImageButton showPassword;
  private ImageButton showConfirmPassword;

  // UI styling constants
  private static final Color BASE_COLOR = Color.GRAY;
  private static final Color EDIT_COLOR = Color.WHITE;
  private static final String BASE_TEXT = "type here";

  /**
   * Constructor for RegisterScreen.
   *
   * <p>Initializes the registration screen with UI components including username, email, password,
   * and password confirmation fields, along with register and cancel buttons.
   *
   * @param background The background to display behind the UI
   * @param authClient The authentication client for server communication
   * @param callback Callback to handle screen transition results
   */
  public RegisterScreen(Background background, AuthClient authClient, Consumer<Boolean> callback) {
    this.background = background;
    this.callback = callback;
    this.authClient = authClient;

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
    username.addListener(createInputFocusListener(username));

    // Create and configure email input field
    Label mailLabel = new Label("mail:", skin);
    mail = new TextField(BASE_TEXT, skin);
    mail.setStyle(
        new TextField.TextFieldStyle(mail.getStyle()) {
          {
            fontColor = BASE_COLOR;
          }
        });
    mail.addListener(createInputFocusListener(mail));

    // Create and configure password input field
    showPassword = new ImageButton(skin, "eye");
    showPassword.setVisible(false);

    Label passwordLabel = new Label("password:", skin);
    password = new TextField(BASE_TEXT, skin);
    password.setStyle(
        new TextField.TextFieldStyle(password.getStyle()) {
          {
            fontColor = BASE_COLOR;
          }
        });

    // set listeners
    showPassword.addListener(createPasswordRevealListener(password));
    password.addListener(createInputFocusListener(password, showPassword));

    // Create and configure password confirmation field
    showConfirmPassword = new ImageButton(skin, "eye");
    showConfirmPassword.setVisible(false);

    Label confirmPasswordLabel = new Label("confirm password:", skin);
    confirmPassword = new TextField(BASE_TEXT, skin);
    confirmPassword.setStyle(
        new TextField.TextFieldStyle(confirmPassword.getStyle()) {
          {
            fontColor = BASE_COLOR;
          }
        });

    // set listeners
    showConfirmPassword.addListener(createPasswordRevealListener(confirmPassword));
    confirmPassword.addListener(createInputFocusListener(confirmPassword, showConfirmPassword));

    // Create cancel button
    TextButton cancel = new TextButton("cancel", skin, "red");
    cancel.setSize(200, 75);
    cancel.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            callback.accept(false);
          }
        });

    // Create registration button
    TextButton create = new TextButton("create", skin, "green");
    create.setSize(200, 75);
    create.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            handleRegistration();
          }
        });

    // Build UI frame with labels and input fields
    Frame frame = new Frame(600, 600, "register", cancel, create);
    frame.setPosition(
        stage.getWidth() / 2f - frame.getWidth() / 2f,
        stage.getHeight() / 2f - frame.getHeight() / 2f);

    frame.getContent().add(usernameLabel).align(Align.left).expandX().padLeft(10).padRight(60);
    frame.getContent().add().size(40, 40).padRight(10);
    frame.getContent().row();

    frame.getContent().add(username).align(Align.left).fillX().expandX().pad(10);
    frame.getContent().add().size(40, 40).padRight(10);
    frame.getContent().row();

    frame.getContent().add(mailLabel).align(Align.left).expandX().padLeft(10).padRight(60);
    frame.getContent().add().size(40, 40).padRight(10);
    frame.getContent().row();

    frame.getContent().add(mail).align(Align.left).fillX().expandX().pad(10);
    frame.getContent().add().size(40, 40).padRight(10);
    frame.getContent().row();

    frame.getContent().add(passwordLabel).align(Align.left).expandX().padLeft(10).padRight(60);
    frame.getContent().add().size(40, 40).padRight(10);
    frame.getContent().row();

    frame.getContent().add(password).align(Align.left).fillX().expandX().pad(10);
    frame.getContent().add(showPassword).size(40, 40).padRight(10);
    frame.getContent().row();

    frame
        .getContent()
        .add(confirmPasswordLabel)
        .align(Align.left)
        .expandX()
        .padLeft(10)
        .padRight(60);
    frame.getContent().add().size(40, 40).padRight(10);
    frame.getContent().row();

    frame.getContent().add(confirmPassword).align(Align.left).fillX().expandX().pad(10);
    frame.getContent().add(showConfirmPassword).size(40, 40).padRight(10);
    frame.getContent().row();

    frame.getContent().add(errorMessage).align(Align.left).fillX().expandX().colspan(2).pad(10);
    frame.getContent().row();
    errorMessage.setWrap(true);

    stage.addActor(frame);
  }

  /**
   * Creates a focus listener for input fields that clears placeholder text when focused. Configures
   * password field masking and controls visibility toggle button.
   *
   * @param field The input field to listen to
   * @param isPasswordField Whether this field should be masked as a password
   * @param revealButton Optional button to control password visibility
   * @return A FocusListener configured for the field
   */
  private FocusListener createInputFocusListener(TextField field) {
    return createInputFocusListener(field, null);
  }

  private FocusListener createInputFocusListener(TextField field, Actor revealButton) {
    return new FocusListener() {
      @Override
      public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
        if (focused && field.getStyle().fontColor == BASE_COLOR) {
          field.setText("");
          field.getStyle().fontColor = EDIT_COLOR;

          // Configure password field with masking
          if (revealButton != null) {
            field.setPasswordMode(true);
            field.setPasswordCharacter('•');
            revealButton.setVisible(true);
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

  /**
   * Handles the registration process by validating input and sending registration request.
   * Validates that passwords match before submitting to server.
   */
  private void handleRegistration() {
    String mailValue = mail.getText();
    String usernameValue = username.getText();
    String passwordValue = password.getText();
    String confirmPasswordValue = confirmPassword.getText();

    if (mailValue.equals(BASE_TEXT)
        || mailValue.isEmpty()
        || usernameValue.equals(BASE_TEXT)
        || usernameValue.isEmpty()
        || passwordValue.equals(BASE_TEXT)
        || passwordValue.isEmpty()
        || confirmPasswordValue.equals(BASE_TEXT)
        || confirmPasswordValue.isEmpty()) {
      errorMessage.setText("All fields are required");
      return;
    }

    // Clear previous error message
    errorMessage.setText("");

    // Validate that passwords match
    if (!passwordValue.equals(confirmPasswordValue)) {
      errorMessage.setText("Passwords do not match");
      return;
    }

    // Send registration request to server
    authClient.register(
        mailValue,
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
            Gdx.app.error(TAG, "Registration failed with status " + status + ": " + result);
            errorMessage.setText(result);
          }

          @Override
          public void error(String message) {
            Gdx.app.error(TAG, "Registration network error: " + message);
            errorMessage.setText("Network error");
          }
        });
  }

  /**
   * Called when the screen becomes active. Resets all input fields and buttons to initial state.
   */
  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);
    stage.setKeyboardFocus(null);
    resetInputFields();
    showPassword.setVisible(false);
    showConfirmPassword.setVisible(false);
    errorMessage.setText("");
  }

  /** Resets all input fields to their placeholder state with base styling. */
  private void resetInputFields() {
    username.getStyle().fontColor = BASE_COLOR;
    username.setText(BASE_TEXT);
    mail.getStyle().fontColor = BASE_COLOR;
    mail.setText(BASE_TEXT);
    password.getStyle().fontColor = BASE_COLOR;
    password.setText(BASE_TEXT);
    confirmPassword.getStyle().fontColor = BASE_COLOR;
    confirmPassword.setText(BASE_TEXT);
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
    if (stage != null) {
      stage.dispose();
    }
    if (skin != null) {
      skin.dispose();
    }
  }
}
