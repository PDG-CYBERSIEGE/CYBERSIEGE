package pdg.game.Screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.*;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import pdg.game.Canon;
import pdg.game.DTO.TeamDTO;
import pdg.game.Entity.Entity;
import pdg.game.Entity.Robot;
import pdg.game.Entity.Team;
import pdg.game.Entity.Block;
import pdg.game.Game;
import pdg.game.utils.Asset;

import java.util.ArrayList;


public class ArenaScreen implements Screen{

    private pdg.game.Game game;
    private static final long serialVersionUID = 1L;

    private boolean gameOver = false;
    private boolean youWon;


    private OrthographicCamera gameCam;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private TiledMap map;
    private Viewport viewport;
    private Stage stage;

    private Asset assetManager;

    private SpriteBatch batch;

    private Team ownTeam;
    private Team ennemyTeam;
    private ArrayList<Block> deadBlocks= new ArrayList<>(); // TODO checker l'utilité de ca

    private ArrayList<Body> bodiesToDestroy = new ArrayList<>();


    public static final float PPM = 100f;
    private World world;
    private Box2DDebugRenderer b2dr;

    private Texture woodBlockTexture,pigBlockTexture;
    private Texture backBtnTexture,saveBtnTexture,reloadBtnTexture;

    private int mapWidth;
    private int mapHeight;

    private Canon canon;

    public void createRobot(int count){
        // create a robot using the DTO
    }

    public ArenaScreen(Game game, TeamDTO onwTeam, TeamDTO ennemyTeam) {
        this.game = game;

        // Initialize camera and viewport
        gameCam = new OrthographicCamera();
        viewport = new FitViewport(960 / PPM, 608 / PPM, gameCam);
        world = new World(new Vector2(0, -9.81f), true);

        // initialize the robots
        createRobot(4);

        // loading all sprites
        assetManager = new Asset();
        assetManager.loadSprites();

        //crée les équipes

        onwTeam.blocks()


    }

    private Team extractTeam(TeamDTO team){

    }

    private void initializeBodies() {
        // create kings and robots and blocks
    }

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
        fdef.friction = 10.0f;
        fdef.restitution = 0.2f;

        body.createFixture(fdef);
        shape.dispose();
    }

    private Body createDynamicBody(Rectangle rect, String sprite, int health) {

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
        /*
        TODO a rajouter a la suite de cette fonction
        Block block = new Block(sprite, health, body);
        body.setUserData(block);
        wood Wood = new wood(body);
                body.setUserData(Wood);
                Wood.setRectX(rect.getWidth());
                Wood.setRectY(rect.getHeight());
                blockBodies1.add(Wood);
                break;
         */
        shape.dispose();
        return body;
    }


    private void update(float delta) {
        world.step(1 / 60f, 6, 2);
        gameCam.update();
        tiledMapRenderer.setView(gameCam);
        teamUpdate(ownTeam, false);
        teamUpdate(ennemyTeam, true);
    }

    private void teamUpdate(Team team, boolean isEnnemy){
        if (team.king.isDead()) {
            destroy(team.king);
            gameOver = true;
            youWon = isEnnemy;
        }

        for (Block b : team.tower){
            if (b.isDead()){
                destroy(b);
            }
        }

        for (Robot r : team.robots) {
            r.reduceCooldown();
        }
    }

    private void destroy(Entity entity){
        entity.getSprite().dispose();
        entity.getBody().setActive(false);
        world.destroyBody(entity.getBody());
    }

    private void teamRender(Team team) {

        batch.draw(team.king.getSprite(), team.king.getBody().getPosition().x-30/ PPM, team.king.getBody().getPosition().y-30/ PPM, 60 / PPM, 60 / PPM);

        for (Block b : team.tower){
            batch.draw(b.getSprite(), b.getBody().getPosition().x-30/ PPM, b.getBody().getPosition().y-30/ PPM, 60 / PPM, 60 / PPM);

        }
    }

    @Override
    public void render(float delta) {
        update(delta);

//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameOver) {
            // TODO Set to the Win Screen or loseScreen
        }

        tiledMapRenderer.render();
        //slingshotGame.render(delta);
        b2dr.render(world, gameCam.combined); // TODO c'est du débug a enlevé pour la prod

        batch.setProjectionMatrix(gameCam.combined);
        batch.begin();

        teamRender(ownTeam);
        teamRender(ennemyTeam);

        batch.draw(backBtnTexture,10 / PPM, 550 / PPM,48/PPM,48/PPM);
        batch.draw(saveBtnTexture,70 / PPM, 550 / PPM,105/PPM,48/PPM);
        batch.draw(reloadBtnTexture,185 / PPM, 550 / PPM,48/PPM,48/PPM);

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

    }

    @Override
    public void dispose() {
        map.dispose();
        tiledMapRenderer.dispose();
        batch.dispose();
        world.dispose();
        b2dr.dispose();
        woodBlockTexture.dispose();
        backBtnTexture.dispose();
    }
}
