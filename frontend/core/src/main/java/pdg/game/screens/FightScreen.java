package pdg.game.screens;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import pdg.game.DTO.TeamDTO;
import pdg.game.Entity.Team;
import pdg.game.GameContactListener;
import pdg.game.Main;
import pdg.game.blocks.HeavyBlockSprite;
import pdg.game.network.websocket.GameMessages;
import pdg.game.scene.Background;
import pdg.game.ui.Frame;
import pdg.game.ui.GravityButton;
import pdg.game.ui.RobotChoiceButton;
import pdg.game.ui.Score;
import pdg.game.ui.VerifyButton;
import pdg.game.utils.StaticValues;
import static pdg.game.utils.StaticValues.ALLYCANONSPAWN;
import static pdg.game.utils.StaticValues.ENNEMYCANONSPAWN;
import static pdg.game.utils.StaticValues.OWNBUILDINGZONE;
import static pdg.game.utils.StaticValues.RECTHEIGHT;
import static pdg.game.utils.StaticValues.RECTWIDTH;
import static pdg.game.utils.StaticValues.RECTX;
import static pdg.game.utils.StaticValues.RECTY;

public class FightScreen implements Screen {

  private static final float ARENA_WIDTH = 32f;
  private static final float ARENA_HEIGHT = 18f;
  private static final float BORDER_THICKNESS = 1f;

  private final Main game;

  // ce qui gère la physique du jeu
  private World world;
  private Box2DDebugRenderer b2dr;
  // Passe à false avant de livrer en prod : le rendu debug Box2D coûte cher.
  private static final boolean DEBUG_RENDER_PHYSICS = true;

  // utile pour dessiner tout ce qui n'est pas un acteur comme les dot de direction par exemple
  private SpriteBatch batch;

  /** Scene2D stage for the interface and game-world layers. */
  private Stage stage, itemStage;

  private Background background;
  private Skin skin;

  /** Score panel displayed at the top of the screen. */
  Score score;

  /** Whether both stages should currently be rendered. */
  boolean isVisible = true;

  private Team ownTeam;
  private Team ennemyTeam;

  private String username;
  private String ennemyUser;

  // list of robotbutton
  private ArrayList<RobotChoiceButton> robotsBtn = new ArrayList<>();

  // endgame
  private boolean gameEnded = false;

  // camera settings
  private final Vector3 camStartPos = new Vector3();
  private final Vector3 camTargetPos = new Vector3();
  private float camStartZoom;
  private float camTargetZoom;
  private float camTransitionTime = 0f;
  private float camTransitionDuration = 0f;
  private boolean camTransitioning = false;

  // label pour la première phase
  private Label heavyCountLabel, mediumCountLabel, lightCountLabel;

  private GravityButton gravityButton;
  private VerifyButton verifyButton;
  private Image buildZonePlaceholder;

  // physique
  private static final float PHYSICS_TIMESTEP = 1f / 60f; // 60 Hz, standard Box2D
  private float physicsAccumulator = 0f;


  // fin de partie
  private TextButton play_again;
  private TextButton main_menu;
  private Label endGameLabel;
  private Frame frame;
  int scoreP1 = 0;
  int scoreP2 = 0;


  public enum GamePhase {
    BUILD,
    COMBAT
  }

  public GamePhase currentPhase = GamePhase.BUILD;

  private boolean isValidated = false;

  public FightScreen(Main game, String ownPlayerName, String opponentPlayerName) {
    username = ownPlayerName;
    ennemyUser = opponentPlayerName;
    this.game = game;

    skin = new Skin(Gdx.files.internal("futuristic_ui/uiskin.json"));

    // initiating world
    world = new World(new Vector2(0, -9.81f), true);
    world.setContactListener(new GameContactListener(this));
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

    createGroundSprite();

    buildZonePlaceholder =
        createColoredRectangle(RECTX, RECTY, RECTWIDTH, RECTHEIGHT, new Color(1f, 0f, 0f, 0.4f));
    itemStage.addActor(buildZonePlaceholder);

    // ui de comptage de points
    score =
      new Score(
        username, ennemyUser,
        3);

    verifyButton = new VerifyButton("Verifier", skin);
    heavyCountLabel = new Label("", skin);
    mediumCountLabel = new Label("", skin);
    lightCountLabel = new Label("", skin);


    createWorldBorders();
  }

  @Override
  public void show() {

    Gdx.app.log("FirstScreen", "show() called");

    // =========================
    // STAGE
    // =========================


    // =========================
    // SKIN
    // =========================

    // on aura le retour des DTO
    score.setPosition(
        stage.getWidth() / 2f - score.getWidth() / 2f, stage.getHeight() - score.getHeight());
    stage.addActor(score);

    // label pour le compte de block de la première phase
    heavyCountLabel.setPosition(50, 805);
    stage.addActor(heavyCountLabel);

    mediumCountLabel.setPosition(50, 635);
    stage.addActor(mediumCountLabel);

    lightCountLabel.setPosition(50, 470);
    stage.addActor(lightCountLabel);

    verifyButton.setPosition(500, 370);
    verifyButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            if (verifyButton.isActivable()) {
              verify();
            }
          }
        });
    stage.addActor(verifyButton);



  }

  @Override
  public void render(float delta) {
    ScreenUtils.clear(0, 0, 0, 1); // clear l'écran pour ne rien garder de la derniere frame
    if (isVisible && ownTeam != null && ennemyTeam != null) { // désactive ecran si fenetre pas assez grand, pas obliger de garder.$

      checkEnd(); // faut regarder comment on traite le nouveau round.

      stepPhysics(delta);
      updateCameraTransition(delta);

      // placeholder : rectangle rouge plein, pour les tour  construire
      // coordonnées que itemStage (unités de blocs, 32x18)

      Map<String, Integer> counts = ownTeam.countBlocksAtSpawn();
      heavyCountLabel.setText("Heavy: " + counts.getOrDefault(StaticValues.HEAVY, 0));
      mediumCountLabel.setText("Medium: " + counts.getOrDefault(StaticValues.MEDIUM, 0));
      lightCountLabel.setText("Light: " + counts.getOrDefault(StaticValues.LIGHT, 0));

      buildZonePlaceholder.setVisible(currentPhase == GamePhase.BUILD);

      // affiche le niveau, puis l'ui par dessus.
      itemStage.getViewport().apply();
      itemStage.act(delta);
      itemStage.draw();

      ownTeam.updateBlocks(world);
      ennemyTeam.updateBlocks(world);

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
        if (btn.isRobotLoaded() || !btn.robot.isReady()) {
          btn.setImageVisible(false);
          btn.setBorderColor(Color.RED);
        } else {
          btn.setImageVisible(true);
          btn.setBorderColor(Color.WHITE);
        }
      }

      verifyButton.update(ownTeam);

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
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {
    itemStage.dispose();
    stage.dispose();
    world.dispose();
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

  private void checkEnd() {
    if (gameEnded) {

      if( frame != null) return;

      play_again = new TextButton("play again", skin, "yellow");
      play_again.setSize(150, 75);
      play_again.addListener(
          new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

              if(play_again.isDisabled()) return;

              play_again.setDisabled(true);
              main_menu.setDisabled(true);

              game.getMainMenuScreen().LoadOnDisconnect(false);
              game.getMainMenuScreen().reconnectOnDisconnect(true);

              game.getMainMenuScreen().getGameWebSocket().disconnect();

              endGameLabel.setText("Searching...");
            }
          });


      main_menu = new TextButton("main menu", skin, "green");
      main_menu.setSize(150, 75);
      main_menu.addListener(
          new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

              if(main_menu.isDisabled()) return;

              play_again.setDisabled(true);
              main_menu.setDisabled(true);
              game.getMainMenuScreen().getGameWebSocket().disconnect();
              game.setScreen(game.getMainMenuScreen());
            }
          });

      endGameLabel = new Label("you won!", skin, "title");

      frame = new Frame(600, 600, "Connection", play_again, main_menu);
      frame.setPosition(
      stage.getWidth() / 2f - frame.getWidth() / 2f,
      stage.getHeight() / 2f - frame.getHeight() / 2f);

      frame.getContent().add(endGameLabel).padBottom(100).center().fillX().row();

      stage.addActor(frame);
      frame.setPosition(
          stage.getWidth() / 2f - frame.getWidth() / 2f,
          stage.getHeight() / 2f - frame.getHeight() / 2f);

      return;
    }
    if (ownTeam.king.isDead()) {
      score.addScoreP2();
      scoreP2++;
      initialisePhase1();
      return;
    }
    if (ennemyTeam.king.isDead()) {
      score.addScoreP1();
      scoreP1++;
      initialisePhase1();
    }
  }

  /**
   * Démarre une transition fluide de la caméra vers une zone donnée.
   *
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
        0);
    cam.zoom = camStartZoom + (camTargetZoom - camStartZoom) * t;
    cam.update();

    if (t >= 1f) {
      camTransitioning = false;
    }
  }

  private void initialisePhase1() {
    currentPhase = GamePhase.BUILD;
    isValidated = false;
    Gdx.app.log("Screen", "debut initiate");

    InputMultiplexer multiplexer = new InputMultiplexer();
    multiplexer.addProcessor(stage);
    multiplexer.addProcessor(itemStage);
    multiplexer.addProcessor(ownTeam.canon.getInputProcessor());
    multiplexer.addProcessor(ownTeam.king.getInputProcessor());
    Gdx.input.setInputProcessor(multiplexer);

    if (gravityButton != null) {
      gravityButton.remove();
    }
    gravityButton = new GravityButton("OFF", skin, ownTeam);
    gravityButton.setPosition(500, 270);
    stage.addActor(gravityButton);

    ownTeam.resetGravity();
    ennemyTeam.resetGravity();


    if (scoreP1 == 3 || scoreP2 == 3) {
      gameEnded = true;
    }
    ownTeam.setup();
    focusCameraOn(OWNBUILDINGZONE, 2f, 1f);
    applyPhaseVisibility();
    ownTeam.restoreHealth();
    ennemyTeam.restoreHealth();
    Gdx.app.log("Screen", "fin initiate");

  }

  private void transitionToPhase2() {
    resetCameraToFullView(1.5f); // transition fluide sur 1.5 seconde
    // reset to no gravity
    ownTeam.changeGravity();

    // setup both towers
    ennemyTeam.changeGravity();
    ownTeam.changeGravity();

    // restore potential health issues
    ennemyTeam.restoreHealth();
    ownTeam.restoreHealth();

    currentPhase = GamePhase.COMBAT;
    applyPhaseVisibility();
  }

  private void stepPhysics(float delta) {
    physicsAccumulator += delta;
    while (physicsAccumulator >= PHYSICS_TIMESTEP) {
      world.step(PHYSICS_TIMESTEP, 6, 2);
      physicsAccumulator -= PHYSICS_TIMESTEP;
    }
  }

  private void applyPhaseVisibility() {
    boolean isBuildPhase = currentPhase == GamePhase.BUILD;

    gravityButton.setVisible(isBuildPhase);
    verifyButton.setVisible(isBuildPhase);
    heavyCountLabel.setVisible(isBuildPhase);
    mediumCountLabel.setVisible(isBuildPhase);
    lightCountLabel.setVisible(isBuildPhase);

    for (RobotChoiceButton btn : robotsBtn) {
      btn.setVisible(!isBuildPhase);
    }
  }

  private void createGroundSprite() {
    int length = Math.round(ARENA_WIDTH + 8); // un segment par unité de largeur (32 segments)
    HeavyBlockSprite groundSprite = new HeavyBlockSprite(length);
    groundSprite.resize(2); // recalcule la taille à length x 1, cohérent avec le nombre de segments
    groundSprite.setPosition(-8, 0); // aligné avec le body statique du sol
    itemStage.addActor(groundSprite);
  }

  private Image createColoredRectangle(float x, float y, float width, float height, Color color) {
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(color);
    pixmap.fill();
    Texture texture = new Texture(pixmap);
    pixmap.dispose();

    Image image = new Image(texture);
    image.setPosition(x, y);
    image.setSize(width, height);
    return image;
  }

  public void receiveStart(GameMessages.Start start) {

  }

  public void receiveAvailableComponent(GameMessages.AvailableComponents availableComponents) {
    if (currentPhase == GamePhase.BUILD){
      ownTeam = new Team(username, game, availableComponents, world, itemStage, ALLYCANONSPAWN, ARENA_WIDTH);
      for (RobotChoiceButton btn : this.ownTeam.setupChoiceButton()) {
        stage.addActor(btn);
        this.robotsBtn.add(btn);
      }
      Gdx.app.log("Screen", "fin initiate");
      ennemyTeam = new Team(ennemyUser, game, availableComponents, world, itemStage, ENNEMYCANONSPAWN, ARENA_WIDTH);

    } else {
      currentPhase = GamePhase.BUILD;
      ownTeam.resetFromAvailableComponents(availableComponents);
      ennemyTeam.resetFromAvailableComponents(availableComponents);
    }

    initialisePhase1();
  }

  public void receiveBuildValidate(GameMessages.BuildValidate buildValidate) {
    if (buildValidate.valid){
      gravityButton.validated();
      isValidated = true;
    }
  }

  public void receiveTeamState(GameMessages.Team team) {
     if (currentPhase == GamePhase.BUILD){
       if (Objects.equals(team.team.name(), ownTeam.user)){
         ownTeam.setupToDate(team.team, false);
       } else {
         ennemyTeam.setupToDate(team.team, true);
       }

       if (ownTeam.isReceived() && ennemyTeam.isReceived()) {
         transitionToPhase2();
       }

     } else {
       if (Objects.equals(team.team.name(), ownTeam.user)){
         ownTeam.checkchanges(team.team, false);
       } else {
         ennemyTeam.checkchanges(team.team, true);
       }
     }
  }

  public void receiveEnemyFire(GameMessages.Fire fire) {
    ennemyTeam.canon.sendRobot(ennemyTeam.getRobot(fire.robot), fire.power, fire.angle);
  }

  private void verify(){
    if (!isValidated){
      game.getMainMenuScreen().getGameWebSocket().send(GameMessages.buildValidate(ownTeam.getDTO()));
      isValidated = true;
    }
  }
}
