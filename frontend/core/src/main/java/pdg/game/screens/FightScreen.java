package pdg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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

import java.util.ArrayList;


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

  private final Team ownTeam = new Team();
  private final Team ennemyTeam = new Team();

  float initialX = 420;
  float initialY = 80;
  float offset = 50;

  //list of robotbutton
  private ArrayList<RobotChoiceButton> robotsBtn = new ArrayList<>();

  //endgame
  private boolean gameEnded = false;

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
    createOwnTeam(onwTeamDTO);
    createEnnemyTeam(ennemyTeamDTO);

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
    multiplexer.addProcessor(ownTeam.canon.getInputProcessor());
    Gdx.input.setInputProcessor(multiplexer);

    // =========================
    // SKIN
    // =========================

    skin = new Skin(Gdx.files.internal("futuristic_ui/uiskin.json"));
    Image t = new Image(skin.getDrawable("frame2"));


    // ui de comptage de points
    score = new Score("henri", "", 3); // TODO remplacer par le nom des joueurs lorsque on les aura par la websockets et qu on aura le retour des DTO
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

  }

  @Override
  public void render(float delta) {
    ScreenUtils.clear(0, 0, 0, 1); // clear l'écran pour ne rien garder de la derniere frame
    if (isVisible) { // désactive ecran si fenetre pas assez grand, pas obliger de garder.$

      checkEnd(); //faut regarder comment on traite le nouveau round.

      world.step(delta, 6, 2); // valeurs standard : 6 vélocity iterations, 2 position iterations

      ownTeam.updateBlocks(world);
      ennemyTeam.updateBlocks(world);

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


      /*
      // placeholder : vert, pour les tour de lancement
      shapeRenderer.setColor(Color.GREEN);
      shapeRenderer.rect(x + width + 1, y, 2f, 2f);
      shapeRenderer.rect(x + 25 - 3, y, 2f, 2f);

       */
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
    batch.dispose();
  }

  private Texture createWhiteDotTexture(int diameterPx) {
    Pixmap pixmap = new Pixmap(diameterPx, diameterPx, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.WHITE);
    pixmap.fillCircle(diameterPx / 2, diameterPx / 2, diameterPx / 2);
    Texture texture = new Texture(pixmap);
    pixmap.dispose();
    texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    return texture;
  }

  private void setupChoiceButton (Robot robot) {
    // bouton pour choisir le robot a envoyer
    RobotChoiceButton btn = new RobotChoiceButton(robot);

    btn.setPosition(initialX, initialY);

    initialX += offset;

    stage.addActor(btn);

    btn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        ownTeam.canon.loadNextRobot(btn);
      }
    });

    robotsBtn.add(btn);

  }

  private void createOwnTeam (TeamDTO teamDTO) {
    ownTeam.canon = new Canon(world, itemStage, new Vector2(7f, 3f), new Texture("launcher/Generator.png"), createWhiteDotTexture(16));
    createKing(ownTeam, teamDTO);
    createBlocks(ownTeam, teamDTO);


      for (RobotDTO robotDTO : teamDTO.robots()) {
        setupChoiceButton(createRobot(ownTeam, robotDTO));
      }

  }

  private void createEnnemyTeam(TeamDTO teamDTO) {
    ennemyTeam.canon = new Canon(world, itemStage, new Vector2(24f, 3f), new Texture("launcher/Generator.png"), createWhiteDotTexture(16));
    createKing(ennemyTeam, teamDTO);
    createBlocks(ennemyTeam, teamDTO);

    for (RobotDTO robotDTO : teamDTO.robots()) {
      createRobot(ennemyTeam, robotDTO);
    }
  }

  private void createKing(Team team, TeamDTO teamDTO){
    KingDTO kingDTO = teamDTO.king();
    Rectangle kingRect = new Rectangle(kingDTO.x(), kingDTO.y(), 1, 1);
    Body kingBody = createDynamicBody(kingRect, kingDTO.mass());
    Texture kingTexture = new Texture(kingDTO.sprite());
    King king = new King(kingTexture, kingDTO.health(), kingBody, kingDTO.mass(), 1, 1);
    kingBody.setUserData(king);
    team.king = king;
  }

  private void createBlocks(Team team, TeamDTO teamDTO) {
    for (BlockDTO blockDTO : teamDTO.blocks()) {
      Rectangle rect = new Rectangle(blockDTO.x(), blockDTO.y(), blockDTO.length(), 1);
      Body body = createDynamicBody(rect, blockDTO.mass());
      pdg.game.Entity.Block block = new pdg.game.Entity.Block(null, blockDTO.health(), body, blockDTO.mass(), 1, 1, blockDTO.type(), blockDTO.length());
      body.setUserData(block);
      team.tower.add(block);
      if (block.getBlockSprite() != null) {
        itemStage.addActor(block.getBlockSprite());
      }
    }
  }

  private Robot createRobot(Team team, RobotDTO robotDTO) {
    Rectangle rect = new Rectangle(-100, -100, 1, 1);
    Body body = createDynamicBody(rect, robotDTO.mass());
    Texture texture = new Texture(robotDTO.sprite());
    Robot robot = new Robot(texture, robotDTO.health(), body, robotDTO.mass(), robotDTO.cooldown(), 1, 1);
    body.setUserData(robot);
    team.robots.add(robot);
    return robot;
  }

  private Body createDynamicBody(Rectangle rect, int mass) {
    BodyDef bdef = new BodyDef();
    bdef.type = BodyDef.BodyType.DynamicBody;
    bdef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);

    Body body = world.createBody(bdef);

    PolygonShape shape = new PolygonShape();
    shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);

    FixtureDef fdef = new FixtureDef();
    fdef.shape = shape;
    fdef.density = mass/1000f;
    fdef.friction = 0.2f;
    fdef.restitution = 0f;

    body.createFixture(fdef);
    shape.dispose();
    return body;
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
}
