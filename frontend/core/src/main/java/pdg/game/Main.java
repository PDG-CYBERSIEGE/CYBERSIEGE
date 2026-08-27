package pdg.game;

import static java.lang.Thread.sleep;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import pdg.game.network.AuthClient;
import pdg.game.network.HttpClient;
import pdg.game.network.ResponseListener;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
  private SpriteBatch batch;
  private Texture image;

  @Override
  public void create() {
    batch = new SpriteBatch();
    image = new Texture("libgdx.png");

    HttpClient httpClient = new HttpClient("http://localhost:8080");
    AuthClient authClient = new AuthClient(httpClient);

    authClient.register(
        "user@test.com",
        "usr",
        "psw12345",
        new ResponseListener() {
          @Override
          public void success(String result) {
            System.out.println("Success, res = " + result);
          }

          @Override
          public void failure(int statusCode, String msg) {
            System.out.println("Fail, status = " + statusCode + " : " + msg);
          }

          @Override
          public void error(String msg) {
            System.out.println("Error : " + msg);
          }
        });

    try {
      sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    authClient.login(
        "usr",
        "psw12345",
        new ResponseListener() {
          @Override
          public void success(String result) {
            System.out.println("Success, res = " + result);
            authClient.setToken(result);
          }

          @Override
          public void failure(int statusCode, String msg) {
            System.out.println("Fail, status = " + statusCode + " : " + msg);
          }

          @Override
          public void error(String msg) {
            System.out.println("Error : " + msg);
          }
        });
  }

  @Override
  public void render() {
    ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
    batch.begin();
    batch.draw(image, 140, 210);
    batch.end();
  }

  @Override
  public void dispose() {
    batch.dispose();
    image.dispose();
  }
}
