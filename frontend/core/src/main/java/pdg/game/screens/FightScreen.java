package pdg.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.World;import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pdg.game.Canon;import pdg.game.Main;
import pdg.game.blocks.Block;
import pdg.game.blocks.HeavyBlock;
import pdg.game.blocks.LightBlock;
import pdg.game.blocks.MediumBlock;
import pdg.game.scene.Background;
import pdg.game.ui.Frame;
import pdg.game.ui.Score;

public class FightScreen implements Screen {

  private final Main game;

  private final ShapeRenderer shapeRenderer;

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

  public FightScreen(Main game) {
    this.game = game;
    this.shapeRenderer = new ShapeRenderer();

    //creating canons
    Texture dot = createWhiteDotTexture(16);
    Canon canon = new Canon()
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


    // ui de comptage de points
    score = new Score("henri", "xxPaulgamerXX", 10); // TODO remplacer par le nom des joueurs lorsque on les aura par la websockets et qu on aura le retour des DTO
    score.setPosition(
      stage.getWidth() / 2f - score.getWidth() / 2f, stage.getHeight() - score.getHeight());
    stage.addActor(score);
    score.addScoreP1();

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
  }

  @Override
  public void render(float delta) {

    ScreenUtils.clear(0, 0, 0, 1); // clear l'écran pour ne rien garder de la derniere frame
    if (isVisible) { // désactive ecran si fenetre pas assez grand, pas obliger de garder.$
      // affiche le niveau, puis l'ui par dessus.
      itemStage.getViewport().apply();
      itemStage.act(delta);
      itemStage.draw();

      // placeholder : rectangle rouge plein, pour les tour  construire
      // coordonnées que itemStage (unités de blocs, 32x18)
      float x = 1f;
      float y = 2f;
      float width = 5f;
      float height = 10f;

      shapeRenderer.setProjectionMatrix(itemStage.getViewport().getCamera().combined);
      shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
      shapeRenderer.setColor(Color.RED);
      shapeRenderer.rect(x, y, width, height);
      shapeRenderer.rect(x + 25, y, width, height);


      // placeholder : vert, pour les tour de lancement
      shapeRenderer.setColor(Color.GREEN);
      shapeRenderer.rect(x + width + 1, y, 2f, 2f);
      shapeRenderer.rect(x + 25 - 3, y, 2f, 2f);
      shapeRenderer.end();




      stage.getViewport().apply();
      stage.act(delta);
      stage.draw();
    }
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
    itemStage.getViewport().update(width, height, true);
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void hide() {

  }

  @Override
  public void dispose() {

  }

  private Texture createWhiteDotTexture(int diameterPx) {
    Pixmap pixmap = new Pixmap(diameterPx, diameterPx, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.WHITE);
    pixmap.fillCircle(diameterPx / 2, diameterPx / 2, diameterPx / 2);
    Texture texture = new Texture(pixmap);
    pixmap.dispose();
    return texture;
  }
}
