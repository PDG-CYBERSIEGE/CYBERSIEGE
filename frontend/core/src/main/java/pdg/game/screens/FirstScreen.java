package pdg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import pdg.game.Main;
import pdg.game.blocks.Block;
import pdg.game.blocks.HeavyBlock;
import pdg.game.blocks.LightBlock;
import pdg.game.blocks.MediumBlock;
import pdg.game.ui.Frame;
import pdg.game.ui.Score;

/** First screen of the application. */
public class FirstScreen implements Screen {

  private Main game;
  private Stage stage, itemStage;
  private Skin skin;
  Frame frame;
  Score score;
  boolean isVisible = true;
  private float timer = 0f;

  public FirstScreen(final Main game) {
    this.game = game;
  }

  @Override
  public void show() {

    Gdx.app.log("FirstScreen", "show() called");

    // =========================
    // STAGE
    // =========================

    stage = new Stage(new FitViewport(1920, 1080));
    itemStage = new Stage(new FitViewport(32, 18));

    for (int i = 1; i <= 5; i++) {

      Texture backgroundTexture = new Texture(Gdx.files.internal("background/1/Day/" + i + ".png"));

      Image background = new Image(backgroundTexture);
      background.setFillParent(true);

      itemStage.addActor(background);
    }

    Gdx.input.setInputProcessor(stage);

    // =========================
    // SKIN
    // =========================

    skin = new Skin(Gdx.files.internal("futuristic_ui/uiskin.json"));

    TextButton button1 = new TextButton("cancel", skin, "red");
    TextButton button2 = new TextButton("accept", skin, "green");
    button1.setSize(100, 30);
    button2.setSize(100, 30);
    frame = new Frame(400, 300, "Connection", button1, button2);
    frame.getContent().add(new TextField("username", skin));
    frame.setPosition(
        stage.getWidth() / 2f - frame.getWidth() / 2f,
        stage.getHeight() / 2f - frame.getHeight() / 2f);
    stage.addActor(frame);

    score = new Score("henri", "xxPaulgamerXX", 10);
    score.setPosition(
        stage.getWidth() / 2f - score.getWidth() / 2f, stage.getHeight() - score.getHeight());
    stage.addActor(score);
    score.addScoreP1();

    Block block1 = new MediumBlock(5);
    block1.setPosition(10, 2);
    block1.resize(2);
    // block1.setRotation(45);
    itemStage.addActor(block1);

    Block block2 = new LightBlock(3);
    block2.setPosition(10, 2);
    block2.resize(2);
    itemStage.addActor(block2);

    Block block3 = new HeavyBlock(4);
    block3.setPosition(10, 4);
    block3.resize(2);
    itemStage.addActor(block3);

    // Actor child = block1.getChild(0);
    /*child.remove();
    stage.addActor(child);
    child.setSize(100, 100);
    child.setPosition(19, 2);*/

    Texture texture = new Texture("throwables/base.png");

    // Image Scene2D
    Image image = new Image(texture);

    // Position et taille dans le monde 8 x 5
    image.setPosition(2, 1);
    image.setSize(1, 1);

    // Ajout au Stage
    itemStage.addActor(image);
  }

  @Override
  public void render(float delta) {

    timer += delta;
    if (timer >= 1f) {
      score.addScoreP1();
      score.addScoreP2();
      timer = 0;
    }
    ScreenUtils.clear(0, 0, 0, 1);
    if (isVisible) {
      itemStage.getViewport().apply();
      itemStage.act(delta);
      itemStage.draw();
      stage.getViewport().apply();
      stage.act(delta);
      stage.draw();
    }
  }

  @Override
  public void resize(int width, int height) {

    stage.getViewport().update(width, height, true);
    itemStage.getViewport().update(width, height, true);

    if (frame.getWidth() > width || frame.getHeight() > height) {
      isVisible = false;
    } else {
      isVisible = true;
    }
    frame.setPosition(
        stage.getWidth() / 2f - frame.getWidth() / 2f,
        stage.getHeight() / 2f - frame.getHeight() / 2f);

    score.setPosition(
        stage.getWidth() / 2f - score.getWidth() / 2f, stage.getHeight() - score.getHeight());
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {

    stage.dispose();
  }
}
