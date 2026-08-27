package pdg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
import pdg.game.scene.Background;
import pdg.game.ui.Frame;
import pdg.game.ui.Score;

/** First screen of the application. */
public class FirstScreen implements Screen {

  private Main game;

  /** Scene2D stage for the interface and game-world layers. */
  private Stage stage, itemStage;

  private Background background;
  private Skin skin;

  /** Connection dialog displayed in the centre of the screen. */
  Frame frame;

  /** Score panel displayed at the top of the screen. */
  Score score;

  /** Whether both stages should currently be rendered. */
  boolean isVisible = true;

  /** Accumulator used to update the demo score once per second. */
  private float timer = 0f;

  /** Rotating loading image displayed on the interface stage. */
  Image loading = new Image();

  /** Creates the first screen for the supplied game instance. */
  public FirstScreen(final Main game) {
    this.game = game;
  }

  @Override
  public void show() {

    Gdx.app.log("FirstScreen", "show() called");

    // =========================
    // STAGE
    // =========================

    stage =
        new Stage(
            new FitViewport(1920, 1080)); // stage pour l'ui, dimention de l'écran pour etre précis
    itemStage =
        new Stage(
            new FitViewport(
                32,
                18)); // stage pour construire le niveau, avec les dimention en nombre de block (si
    // leur taille est pas changé) garder dimentions de l'écran (multiple de 16:9)
    // fitViewPort pour que les coordonnées soient indépendant sde la taille de la fenêtre, et pour
    // pas étirer l'affichage.

    background = new Background();
    background.apply(itemStage);

    Gdx.input.setInputProcessor(stage);

    // =========================
    // SKIN
    // =========================

    skin = new Skin(Gdx.files.internal("futuristic_ui/uiskin.json"));
    Image t = new Image(skin.getDrawable("frame2"));
    // ui central avec boutons
    TextButton button1 = new TextButton("cancel", skin, "red");
    TextButton button2 = new TextButton("accept", skin, "green");
    button1.setSize(100, 30);
    button2.setSize(100, 30);
    frame = new Frame(400, 300, "Connection", button1, button2);
    frame.getContent().add(new TextField("username", skin)).expand().top().left().padLeft(10);
    frame.getContent().row();
    frame.setPosition(
        stage.getWidth() / 2f - frame.getWidth() / 2f,
        stage.getHeight() / 2f - frame.getHeight() / 2f);
    stage.addActor(frame);

    // ui de comptage de points
    score = new Score("henri", "xxPaulgamerXX", 10);
    score.setPosition(
        stage.getWidth() / 2f - score.getWidth() / 2f, stage.getHeight() - score.getHeight());
    stage.addActor(score);
    score.addScoreP1();

    // Affichage du nom du jeu
    Label title = new Label("CYBERSIEGE", skin, "big_title");
    title.setPosition(10, 750);
    stage.addActor(title);

    // Création de chaque type de block
    Block block1 = new MediumBlock(5);
    block1.setPosition(10, 0);
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

    // image du robot
    Texture texture = new Texture("throwables/base.png");
    Image image = new Image(texture);
    image.setPosition(2, 1);
    image.setSize(1, 1);
    itemStage.addActor(image);

    // loading wheel
    loading = new Image(new Texture("futuristic_ui/loading/wheel.png"));
    loading.setSize(200, 200);
    loading.setPosition(stage.getWidth() - 300, stage.getHeight() - 600);
    loading.setOrigin(loading.getWidth() / 2f, loading.getHeight() / 2f);
    stage.addActor(loading);
  }

  @Override
  public void render(float delta) {

    // uptade rotation de waiting wheel
    timer += delta;
    float rotation = loading.getRotation();
    rotation += delta * 360;
    if (rotation >= 360) rotation -= 360; // 1 tour par seconde
    loading.setRotation(rotation);

    // augmenter le score des joueurs toutes les secondes
    if (timer >= 1f) {
      score.addScoreP1();
      score.addScoreP2();
      background.change();
      timer = 0;
    }
    ScreenUtils.clear(0, 0, 0, 1); // clear l'écran pour ne rien garder de la derniere frame
    if (isVisible) { // désactive ecran si fenetre pas assez grand, pas obliger de garder.$
      // affiche le niveau, puis l'ui par dessus.
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
    // pas besoin de gérer les objets car les stages ont une taille fixe.
    stage.getViewport().update(width, height, true);
    itemStage.getViewport().update(width, height, true);

    if (frame.getWidth() > width || frame.getHeight() > height) {
      isVisible = false;
    } else {
      isVisible = true;
    }
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
