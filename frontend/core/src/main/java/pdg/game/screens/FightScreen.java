package pdg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pdg.game.Canon;
import pdg.game.DTO.BlockDTO;
import pdg.game.DTO.KingDTO;
import pdg.game.DTO.RobotDTO;
import pdg.game.DTO.TeamDTO;
import pdg.game.Entity.King;
import pdg.game.Entity.Robot;
import pdg.game.Entity.Team;
import pdg.game.GameContactListener;
import pdg.game.Main;
import pdg.game.blocks.BlockSprite;
import pdg.game.blocks.HeavyBlockSprite;
import pdg.game.blocks.LightBlockSprite;
import pdg.game.blocks.MediumBlockSprite;
import pdg.game.scene.Background;
import pdg.game.ui.Frame;
import pdg.game.ui.RobotChoiceButton;
import pdg.game.ui.Score;
import pdg.game.utils.StaticValues;

import java.util.ArrayList;
import java.util.Map;


public class FightScreen implements Screen {

  private static final float ARENA_WIDTH = 32f;
  private static final float ARENA_HEIGHT = 18f;
  private static final float BORDER_THICKNESS = 1f; // épaisseur en mètres, ajuste selon le rendu voulu

  private final Main game;

  // renderer for placeholder TODO a enlever quand on aura plus besoin de placeholder
  private final ShapeRenderer shapeRenderer;

  // ce qui gère la physique du jeu
  private World world;
  private Box2DDebugRenderer b2dr;
  // Passe à false avant de livrer en prod : le rendu debug Box2D coûte cher.
  private static final boolean DEBUG_RENDER_PHYSICS = true;

  //utile pour dessiner tout ce qui n'est pas un acteur comme les dot de direction par exemple
  private SpriteBatch batch;



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

  private final Team ownTeam;
  private final Team ennemyTeam;

  float initialX = 420;
  float initialY = 80;
  float offset = 50;

  //list of robotbutton
  private ArrayList<RobotChoiceButton> robotsBtn = new ArrayList<>();

  //endgame
  private boolean gameEnded = false;

  // camera settings
  private final Vector3 camStartPos = new Vector3();
  private final Vector3 camTargetPos = new Vector3();
  private float camStartZoom;
  private float camTargetZoom;
  private float camTransitionTime = 0f;
  private float camTransitionDuration = 0f;
  private boolean camTransitioning = false;

  //label pour la première phase
  private Label heavyCountLabel, mediumCountLabel, lightCountLabel;




  public FightScreen(Main game, TeamDTO onwTeamDTO, TeamDTO ennemyTeamDTO) {
    this.game = game;
    this.shapeRenderer = new ShapeRenderer();

    // initiating world
    world = new World(new Vector2(0, -9.81f), true);
    world.setContactListener(new GameContactListener());
    b2dr = new Box2DDebugRenderer();

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

    batch = new SpriteBatch();

    background = new Background();
    background.apply(itemStage);


    //creating teams
    this.ownTeam = new Team(onwTeamDTO, world, itemStage);
    for (RobotChoiceButton btn : this.ownTeam.setupChoiceButton()) {
      stage.addActor(btn);
      this.robotsBtn.add(btn);
    }

    this.ennemyTeam = new Team(ennemyTeamDTO, world, itemStage);


    createWorldBorders();
    }

  @Override
  public void show() {

    Gdx.app.log("FirstScreen", "show() called");

    // =========================
    // STAGE
    // =========================

    InputMultiplexer multiplexer = new InputMultiplexer();
    multiplexer.addProcessor(stage);
    multiplexer.addProcessor(itemStage);
    multiplexer.addProcessor(ownTeam.canon.getInputProcessor());
    multiplexer.addProcessor(ownTeam.king.getInputProcessor());
    Gdx.input.setInputProcessor(multiplexer);

    // =========================
    // SKIN
    // =========================

    skin = new Skin(Gdx.files.internal("futuristic_ui/uiskin.json"));
    Image t = new Image(skin.getDrawable("frame2"));


    // ui de comptage de points
    score = new Score("player1", "player2", 3); // TODO remplacer par le nom des joueurs lorsque on les aura par la websockets et qu on aura le retour des DTO
    score.setPosition(
      stage.getWidth() / 2f - score.getWidth() / 2f, stage.getHeight() - score.getHeight());
    stage.addActor(score);
    score.addScoreP1();

    // Création de chaque type de block
    BlockSprite blockSprite1 = new MediumBlockSprite(5);
    blockSprite1.setPosition(10, 0);
    blockSprite1.resize(2);
    // block1.setRotation(45);
    itemStage.addActor(blockSprite1);
    BlockSprite blockSprite2 = new LightBlockSprite(3);
    blockSprite2.setPosition(10, 2);
    blockSprite2.resize(2);
    itemStage.addActor(blockSprite2);

    /*
    BlockSprite blockSprite3 = new HeavyBlockSprite(4);
    blockSprite3.setPosition(10, 4);
    blockSprite3.resize(2);
    itemStage.addActor(blockSprite3);


     */
    // image du robot
    Texture texture = new Texture("throwables/base.png");
    Image image = new Image(texture);
    image.setPosition(2, 1);
    image.setSize(1, 1);
    itemStage.addActor(image);

    // zoom pour la 1er phase
    Rectangle ownBuildZone = new Rectangle(0f, 1f, 6f, 11f);
    focusCameraOn(ownBuildZone, 1f, 0f);

    //label pour le compte de block de la première phase
    heavyCountLabel = new Label("", skin);
    heavyCountLabel.setPosition(50, 300);
    stage.addActor(heavyCountLabel);

    mediumCountLabel = new Label("", skin);
    mediumCountLabel.setPosition(50, 250);
    stage.addActor(mediumCountLabel);

    lightCountLabel = new Label("", skin);
    lightCountLabel.setPosition(50, 200);
    stage.addActor(lightCountLabel);

    //bouton pour la première phase


    // init phase 1
    initialisePhase1();

  }

  @Override
  public void render(float delta) {
    ScreenUtils.clear(0, 0, 0, 1); // clear l'écran pour ne rien garder de la derniere frame
    if (isVisible) { // désactive ecran si fenetre pas assez grand, pas obliger de garder.$

      checkEnd(); //faut regarder comment on traite le nouveau round.

      world.step(delta, 6, 2); // valeurs standard : 6 vélocity iterations, 2 position iterations

      updateCameraTransition(delta);

      ownTeam.updateBlocks(world);
      ennemyTeam.updateBlocks(world);

      Map<String, Integer> counts = ownTeam.countBlocksAtSpawn();
      heavyCountLabel.setText("Heavy: " + counts.getOrDefault(StaticValues.HEAVY, 0));
      mediumCountLabel.setText("Medium: " + counts.getOrDefault(StaticValues.MEDIUM, 0));
      lightCountLabel.setText("Light: " + counts.getOrDefault(StaticValues.LIGHT, 0));

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
      shapeRenderer.rect(0, y, 2f, 2f);
      shapeRenderer.rect(-7, y, 4f, height);
      shapeRenderer.rect(-3, 3, 1f, 1f);
      shapeRenderer.rect(-3, 4, 1f, 1f);

      shapeRenderer.end();

      batch.setProjectionMatrix(itemStage.getViewport().getCamera().combined);
      batch.begin();
      ownTeam.draw(batch);
      ennemyTeam.draw(batch);
      batch.end();

      if (DEBUG_RENDER_PHYSICS) {
        b2dr.render(world, itemStage.getViewport().getCamera().combined);
      }

      for (RobotChoiceButton btn : robotsBtn) {
        btn.robot.reduceCooldown();
        if (btn.isRobotLoaded() || !btn.robot.isReady()){
          btn.setImageVisible(false);
          btn.setBorderColor(Color.RED);
        } else {
          btn.setImageVisible(true);
          btn.setBorderColor(Color.WHITE);
        }
      }

      stage.getViewport().apply();
      stage.act(delta);
      stage.draw();
    }
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
    itemStage.getViewport().update(width, height, false);
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
    batch.dispose();
  }

  private void createStaticBody(Rectangle rect) {
    BodyDef bdef = new BodyDef();
    bdef.type = BodyDef.BodyType.StaticBody;
    bdef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);

    Body body = world.createBody(bdef);

    PolygonShape shape = new PolygonShape();
    shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);

    FixtureDef fdef = new FixtureDef();
    fdef.shape = shape;
    fdef.density = 1f;
    fdef.friction = 0.5f;
    fdef.restitution = 0.2f;

    body.createFixture(fdef);
    shape.dispose();
  }

  private void createWorldBorders() {
    // Sol : la surface supérieure est à y = 0
    createStaticBody(new Rectangle(0, 2, ARENA_WIDTH, 0));
    // Plafond
    createStaticBody(new Rectangle(0, ARENA_HEIGHT, ARENA_WIDTH, BORDER_THICKNESS));
    // Mur gauche
    createStaticBody(new Rectangle(-BORDER_THICKNESS, 0, BORDER_THICKNESS, ARENA_HEIGHT));
    // Mur droit
    createStaticBody(new Rectangle(ARENA_WIDTH, 0, BORDER_THICKNESS, ARENA_HEIGHT));
  }

  private void checkEnd (){
    if (ownTeam.king.isDead()){
      world.destroyBody(ownTeam.king.getBody());
      score.addScoreP2();
      return;
    }
    if (ennemyTeam.king.isDead()){
      world.destroyBody(ennemyTeam.king.getBody());
      score.addScoreP1();
      return;
    }

  }
  /**
   * Démarre une transition fluide de la caméra vers une zone donnée.
   * @param area zone à cadrer (en unités itemStage, mètres)
   * @param padding marge autour de la zone, en mètres
   * @param duration durée de la transition, en secondes
   */
  private void focusCameraOn(Rectangle area, float padding, float duration) {
    OrthographicCamera cam = (OrthographicCamera) itemStage.getViewport().getCamera();

    camStartPos.set(cam.position);
    camStartZoom = cam.zoom;

    float targetWidth = area.width + padding * 2f;
    float targetHeight = area.height + padding * 2f;

    // zoom < 1 = zoomé (on voit moins de monde), zoom > 1 = dézoomé
    float zoomX = targetWidth / itemStage.getViewport().getWorldWidth();
    float zoomY = targetHeight / itemStage.getViewport().getWorldHeight();
    camTargetZoom = Math.max(zoomX, zoomY);

    camTargetPos.set(area.x + area.width / 2f, area.y + area.height / 2f, 0);

    camTransitionTime = 0f;
    camTransitionDuration = duration;
    camTransitioning = true;
  }

  /** Revient sur la vue complète de l'arène (transition vers la phase 2). */
  private void resetCameraToFullView(float duration) {
    focusCameraOn(new Rectangle(0, 0, ARENA_WIDTH, ARENA_HEIGHT), 0f, duration);
  }

  /** A appeler chaque frame, avant itemStage.act()/draw(). */
  private void updateCameraTransition(float delta) {
    if (!camTransitioning) return;

    camTransitionTime += delta;
    float t = Math.min(camTransitionTime / camTransitionDuration, 1f);
    t = Interpolation.smooth.apply(t); // easing pour un mouvement moins mécanique

    OrthographicCamera cam = (OrthographicCamera) itemStage.getViewport().getCamera();
    cam.position.set(
      camStartPos.x + (camTargetPos.x - camStartPos.x) * t,
      camStartPos.y + (camTargetPos.y - camStartPos.y) * t,
      0
    );
    cam.zoom = camStartZoom + (camTargetZoom - camStartZoom) * t;
    cam.update();

    if (t >= 1f) {
      camTransitioning = false;
    }
  }

  private void initialisePhase1(){
    ownTeam.setupBlocks();
  }

  private void transitionToPhase2() {
    resetCameraToFullView(1.5f); // transition fluide sur 1.5 seconde
    ownTeam.changeGravity();
    ennemyTeam.changeGravity();
  }

}
