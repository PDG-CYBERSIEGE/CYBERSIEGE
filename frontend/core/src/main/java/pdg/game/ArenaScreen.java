package pdg.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.*;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ArenaScreen implements Screen{

    private Game game;
    private static final long serialVersionUID = 1L;

    private OrthographicCamera gameCam;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private TiledMap map;
    private Viewport viewport;
    private Stage stage;

    private SpriteBatch batch;

    private ArrayList<Block> alliedTower=new ArrayList<>(),
        ennemyTower = new ArrayList<>(),// oui
        deadBlocks= new ArrayList<>(); // TODO checker l'utilité de ca
    private King allyKing;
    private King ennemyKing;
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

    }

    public ArenaScreen(Game game) {
        this.game = game;

        // Initialize camera and viewport
        gameCam = new OrthographicCamera();
        viewport = new FitViewport(960 / PPM, 608 / PPM, gameCam);
        world = new World(new Vector2(0, -9.81f), true);
        createRobot(4);
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
    public void render(float delta) {

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
