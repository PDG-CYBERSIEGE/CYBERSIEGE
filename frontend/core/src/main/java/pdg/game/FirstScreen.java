package pdg.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;



/** First screen of the application. */
public class FirstScreen implements Screen {

    private Main game;
    private Stage stage;
    private Skin skin;

    public FirstScreen(final Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        
        Gdx.app.log("FirstScreen", "show() called");

        // =========================
        // STAGE
        // =========================

        stage = new Stage(new ScreenViewport());

        for (int i = 1; i <= 5; i++) {

            Texture backgroundTexture = new Texture(
                Gdx.files.internal("background/1/Day/" + i + ".png")
            );

            Image background = new Image(backgroundTexture);
            background.setFillParent(true);

            stage.addActor(background);
        }

        Gdx.input.setInputProcessor(stage);


        // =========================
        // SKIN
        // =========================

        Skin skin = new Skin(
            Gdx.files.internal("futuristic_ui/uiskin.json")
        );


        System.out.println("Skin loaded: " + skin);
        TextButton button1 = new TextButton("cancel", skin, "red");
        TextButton button2 = new TextButton("accept", skin, "green");
        Gdx.app.log("FirstScreen", "Buttons created: " + button1 + ", " + button2);
        button1.setSize(100, 30);
        button2.setSize(100, 30);
        Gdx.app.log("FirstScreen", "Buttons sized: " + button1.getWidth() + "x" + button1.getHeight() + ", " + button2.getWidth() + "x" + button2.getHeight());
        Frame frame = new Frame(button1, button2, 400, 300, "Connection");
        frame.getContent().add(new TextField("username", skin));
        stage.addActor(frame);
    }


    @Override
    public void render(float delta) {
        
        ScreenUtils.clear(
            0,
            0,
            0,
            1
        );

        stage.act(delta);
        stage.draw();
    }


    @Override
    public void resize(
        int width,
        int height
    ) {
        
        stage.getViewport().update(
            width,
            height,
            true
        );
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

        stage.dispose();
    }
}