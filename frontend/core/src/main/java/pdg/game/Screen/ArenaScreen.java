package pdg.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import pdg.game.Canon;
import pdg.game.DTO.BlockDTO;
import pdg.game.DTO.KingDTO;
import pdg.game.DTO.RobotDTO;
import pdg.game.DTO.TeamDTO;
import pdg.game.Entity.Block;
import pdg.game.Entity.Entity;
import pdg.game.Entity.King;
import pdg.game.Entity.Robot;
import pdg.game.Entity.Team;
import pdg.game.GameContactListener;
import pdg.game.Main;
import pdg.game.utils.Asset;

/**
 * Ecran de la phase de bataille (2e phase d'une manche, cf. cahier des
 * charges) : affiche les deux bases, gère le canon du joueur local et fait
 * avancer la simulation physique jusqu'à ce qu'un roi tombe.
 *
 * Hypothèses faites pour compléter ce fichier (à vérifier / adapter) :
 *  - Block/King/Robot ont un constructeur (Body body, Texture sprite, int health)
 *    et Robot en a un en plus avec (..., int damage, float cooldown).
 *  - Robot expose isReady()/startCooldown() en plus de reduceCooldown() déjà
 *    utilisé plus bas (nécessaire pour Canon).
 *  - Asset expose getTexture(String path) (voir proposition Asset.java).
 *
 * NB: en l'absence de fichier .tmx, l'arène n'est plus chargée depuis une
 * tiled map : les 4 bords (sol/plafond/murs) sont créés en dur dans
 * createWorldBorders(). Remplacez cette méthode par un vrai chargement de
 * map le jour où vous aurez un .tmx.
 */
public class ArenaScreen implements Screen {

    public static final float PPM = 100f;

    // Dimensions de l'arène en pixels (avant division par PPM), doivent
    // rester cohérentes avec la taille du viewport ci-dessous.
    private static final float ARENA_WIDTH_PX = 960f;
    private static final float ARENA_HEIGHT_PX = 608f;
    private static final float BORDER_THICKNESS_PX = 20f;

    private final Main game;

    private boolean gameOver = false;
    private boolean youWon;

    private OrthographicCamera gameCam;
    private Viewport viewport;
    private Stage stage;

    private Asset assetManager;
    private SpriteBatch batch;

    private Team ownTeam;
    private Team ennemyTeam;

    private World world;
    private Box2DDebugRenderer b2dr;
    // Passe à false avant de livrer en prod : le rendu debug Box2D coûte cher.
    private static final boolean DEBUG_RENDER_PHYSICS = true;

    private Texture trajectoryDotTexture;

    private Canon canon;

    // Conservés le temps de construire les Team dans initializeBodies(),
    // une fois les assets chargés.
    private final TeamDTO ownTeamDTO;
    private final TeamDTO ennemyTeamDTO;

    public ArenaScreen(Main game, TeamDTO ownTeamDTO, TeamDTO ennemyTeamDTO) {
        this.game = game;
        this.ownTeamDTO = ownTeamDTO;
        this.ennemyTeamDTO = ennemyTeamDTO;

        gameCam = new OrthographicCamera();
        viewport = new FitViewport(ARENA_WIDTH_PX / PPM, ARENA_HEIGHT_PX / PPM, gameCam);

        // Le monde utilise des coordonnées positives (0..ARENA_WIDTH_PX/PPM,
        // 0..ARENA_HEIGHT_PX/PPM). La caméra étant créée centrée sur (0,0)
        // par défaut, il faut la recentrer sur l'arène, sinon seul le
        // quart inférieur-gauche du monde serait visible.
        gameCam.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);
        gameCam.update();

        world = new World(new Vector2(0, -9.81f), true);
        world.setContactListener(new GameContactListener());

        b2dr = new Box2DDebugRenderer();
        batch = new SpriteBatch();
        stage = new Stage(viewport);

        assetManager = new Asset();
        assetManager.loadSprites(); // synchrone, voir Asset.java

        trajectoryDotTexture = assetManager.getTexture("sprites/green.png");

        initializeBodies();

        // Le canon du joueur local tire les robots de sa propre équipe.
        // Position à ajuster selon votre disposition de map (ici: coin bas-gauche).
        //canon = new Canon(world, ownTeam, new Vector2(80, 80), trajectoryDotTexture);
    }

    /** Construit les corps Box2D + entités des deux équipes, et les bordures statiques de l'arène. */
    private void initializeBodies() {
      /*
        createWorldBorders();
        ownTeam = createTeam(ownTeamDTO);
        ennemyTeam = createTeam(ennemyTeamDTO);

       */
    }

    /**
     * Crée 4 corps statiques (sol, plafond, mur gauche, mur droit) qui
     * délimitent l'arène, en remplacement d'une vraie tiled map.
     */
    private void createWorldBorders() {
        // Sol : la surface supérieure est à y = 0
        createStaticBody(new Rectangle(0, -BORDER_THICKNESS_PX, ARENA_WIDTH_PX, BORDER_THICKNESS_PX));
        // Plafond
        createStaticBody(new Rectangle(0, ARENA_HEIGHT_PX, ARENA_WIDTH_PX, BORDER_THICKNESS_PX));
        // Mur gauche
        createStaticBody(new Rectangle(-BORDER_THICKNESS_PX, 0, BORDER_THICKNESS_PX, ARENA_HEIGHT_PX));
        // Mur droit
        createStaticBody(new Rectangle(ARENA_WIDTH_PX, 0, BORDER_THICKNESS_PX, ARENA_HEIGHT_PX));
    }
/*
    /** Construit une Team (roi + blocs de tour + file de robots) à partir de son DTO.
    private Team createTeam(TeamDTO teamDTO) {
        Team team = new Team();

        KingDTO kingDTO = teamDTO.king();
        Rectangle kingRect = new Rectangle(kingDTO.x(), kingDTO.y(), 60, 60);
        Body kingBody = createDynamicBody(kingRect);
        Texture kingTexture = assetManager.getTexture(kingDTO.sprite());
        King king = new King(kingTexture, kingDTO.health(), kingBody, kingDTO.mass());
        kingBody.setUserData(king);
        team.king = king;

        for (BlockDTO blockDTO : teamDTO.blocks()) {
            Rectangle rect = new Rectangle(blockDTO.x(), blockDTO.y(), 60, 60);
            Body body = createDynamicBody(rect);
            Texture texture = assetManager.getTexture(blockDTO.sprite());
            Block block = new Block(texture, blockDTO.health(), body, blockDTO.mass());
            body.setUserData(block);
            team.tower.add(block);
        }

        // File des robots pas encore tirés : positionnés hors champ, c'est
        // Canon qui les replace sur le pas de tir au fur et à mesure.
        for (RobotDTO robotDTO : teamDTO.robots()) {
            Rectangle rect = new Rectangle(-100, -100, 40, 40);
            Body body = createDynamicBody(rect);
            Texture texture = assetManager.getTexture(robotDTO.sprite());
            Robot robot = new Robot(texture, robotDTO.health(), body, robotDTO.mass(), robotDTO.cooldown());
            body.setUserData(robot);
            team.robots.add(robot);
        }

        return team;
    }
    */

    private void createStaticBody(Rectangle rect) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((rect.getX() + rect.getWidth() / 2) / PPM, (rect.getY() + rect.getHeight() / 2) / PPM);

        Body body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.getWidth() / 2 / PPM, rect.getHeight() / 2 / PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.friction = 0.5f;
        fdef.restitution = 0.2f;

        body.createFixture(fdef);
        shape.dispose();
    }

    /**
     * Crée un corps dynamique pour une entité (roi, bloc ou robot).
     */
    private Body createDynamicBody(Rectangle rect) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set((rect.getX() + rect.getWidth() / 2) / PPM, (rect.getY() + rect.getHeight() / 2) / PPM);

        Body body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.getWidth() / 2 / PPM, rect.getHeight() / 2 / PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 0.5f;
        fdef.friction = 0.2f;
        fdef.restitution = 0f;

        body.createFixture(fdef);
        shape.dispose();
        return body;
    }


    private void update(float delta) {
        world.step(1 / 60f, 6, 2);
        gameCam.update();
        canon.update(delta);
        teamUpdate(ownTeam, false, delta);
        teamUpdate(ennemyTeam, true, delta);
    }

    private void teamUpdate(Team team, boolean isEnnemy, float delta) {
        if (team.king.isDead()) {
            destroy(team.king);
            gameOver = true;
            youWon = isEnnemy;
        }

        for (Block b : team.tower) {
            if (b.isDead()) {
                destroy(b);
            }
        }

        for (Robot r : team.robots) {
            r.reduceCooldown();
        }
    }

    /**
     * Détruit une entité. Sûr à appeler ici car on est TOUJOURS après
     * world.step() (donc en dehors d'un callback de collision) : Box2D
     * interdit de détruire un Body pendant un step, mais teamUpdate() n'est
     * appelé qu'une fois le step terminé.
     *
     * Ne dispose PAS le sprite : les textures sont gérées une seule fois
     * par l'Asset manager (voir dispose() ci-dessous). Les disposer ici
     * casserait le rendu de toutes les autres entités partageant la même
     * texture (ex: tous les blocs "wood").
     */
    private void destroy(Entity entity) {
        entity.getBody().setActive(false);
        world.destroyBody(entity.getBody());
    }

    private void teamRender(Team team) {
        batch.draw(team.king.getSprite(),
            team.king.getBody().getPosition().x - 30 / PPM,
            team.king.getBody().getPosition().y - 30 / PPM,
            60 / PPM, 60 / PPM);

        for (Block b : team.tower) {
            batch.draw(b.getSprite(),
                b.getBody().getPosition().x - 30 / PPM,
                b.getBody().getPosition().y - 30 / PPM,
                60 / PPM, 60 / PPM);
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        if (gameOver) {
            // TODO: brancher vers votre écran de victoire/défaite, ex:
            // game.setScreen(youWon ? new VictoryScreen(game) : new DefeatScreen(game));
            // return;
        }

        // Pas de tiled map à rendre : on efface l'écran nous-mêmes, sinon
        // chaque image laisserait une trainée (rien d'autre ne clear le buffer).
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (DEBUG_RENDER_PHYSICS) {
            b2dr.render(world, gameCam.combined);
        }

        batch.setProjectionMatrix(gameCam.combined);
        batch.begin();

        teamRender(ownTeam);
        teamRender(ennemyTeam);
        canon.draw(batch);

        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
    public void show() {
        // Le canon gère le drag de visée, le Stage gèrera les boutons
        // (back/save/reload) si vous les migrez un jour vers des Actor
        // Scene2d plutôt que des batch.draw() manuels.
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, canon.getInputProcessor()));
    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        b2dr.dispose();
        stage.dispose();
        canon.dispose();
        // Toutes les textures (blocs, king, robots, dot, boutons) sont
        // libérées ici en une fois, plutôt qu'individuellement à la mort
        // de chaque entité (voir remarque dans destroy()).
        assetManager.dispose();
    }
}
