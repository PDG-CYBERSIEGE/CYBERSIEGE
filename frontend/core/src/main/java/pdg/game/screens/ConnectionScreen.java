package pdg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.function.Consumer;
import pdg.game.network.AuthClient;
import pdg.game.network.HttpClient;
import pdg.game.network.ResponseListener;
import pdg.game.scene.Background;
import pdg.game.ui.Frame;

public class ConnectionScreen implements Screen {
  MainMenuScreen mainMenu;
  Background background;
  Stage stage;
  boolean isConnected = false;
  TextButton button;
  Consumer<Boolean> callback;
  private AuthClient authClient;

  public ConnectionScreen(Background background, Consumer<Boolean> callback) {

    this.background = background;
    this.callback = callback;

    HttpClient httpClient = new HttpClient("http://localhost:8080");
    authClient = new AuthClient(httpClient);

    stage = new Stage(new FitViewport(1920, 1080));
    background.apply(stage);

    Skin skin = new Skin(Gdx.files.internal("futuristic_ui/uiskin.json"));

    Label usernameLabel = new Label("username:", skin);
    TextField username = new TextField("username", skin);
    username.addListener(
        new FocusListener() {
          @Override
          public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
            if (focused && username.getText().equals("username")) {
              username.setText("");
            }
          }
        });

    Label passwordLabel = new Label("password::", skin);
    TextField password = new TextField("password", skin);
    password.setPasswordMode(true);
    password.setPasswordCharacter('o');
    password.addListener(
        new FocusListener() {
          @Override
          public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
            if (focused && password.getText().equals("password")) {
              password.setText("");
            }
          }
        });
    TextButton cancel = new TextButton("cancel", skin, "red");
    cancel.setSize(200, 75);

    cancel.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {

            callback.accept(false);
          }
        });
    TextButton connect = new TextButton("connect", skin, "green");
    connect.setSize(200, 75);

    connect.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {

            String usernameValue = username.getText();
            String passwordValue = password.getText();

            authClient.login(
                usernameValue,
                passwordValue,
                new ResponseListener() {

                  @Override
                  public void success(String token) {
                    System.out.println("Connexion réussie !");
                    System.out.println("Token : " + token);

                    authClient.setToken(token);

                    callback.accept(true);
                  }

                  @Override
                  public void failure(int status, String result) {
                    System.out.println("Connexion refusée : " + status + " - " + result);
                  }

                  @Override
                  public void error(String message) {
                    System.out.println("Erreur réseau : " + message);
                  }
                });
          }
        });

    Frame frame = new Frame(600, 600, "Connection", cancel, connect);
    frame.setPosition(
        stage.getWidth() / 2f - frame.getWidth() / 2f,
        stage.getHeight() / 2f - frame.getHeight() / 2f);
    frame.getContent().add(usernameLabel);
    frame.getContent().row();
    frame.getContent().add(username);
    frame.getContent().row();
    frame.getContent().add(passwordLabel);
    frame.getContent().row();
    frame.getContent().add(password);

    stage.addActor(frame);
  }

  public void show() {
    Gdx.input.setInputProcessor(stage);
    // callback.accept(true);
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
