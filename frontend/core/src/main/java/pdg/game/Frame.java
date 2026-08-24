package pdg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;

public class Frame extends Table {

  private Button button1, button2;
  private Window window;
  private Table content;

  public Frame() {
    this(new Button(), new Button(), 400, 300, "Frame Title");
  }

  public Frame(Button button1, Button button2, float width, float height, String title) {

    // =========================
    // SKIN
    // =========================
    Skin skin = new Skin(Gdx.files.internal("futuristic_ui/uiskin.json"));

    this.setSize(width, height);

    // =========================
    // frame window for the borders
    // =========================

    window = new Window(title, skin);

    window.getTitleLabel().setAlignment(Align.center);

    // =========================
    // Content (background) of the frame
    // background is a TiledDrawable, so it will repeat the texture to fill the area.
    // it is set as the background of a Table, which is added to the window.
    // =========================

    TiledDrawable backgroundTexture =
        new TiledDrawable(new TextureRegion(new Texture(Gdx.files.internal("Frame_56.png"))));

    content = new Table();
    Table window_content = new Table();

    window_content.setBackground(backgroundTexture);

    window.add(window_content).fill().expand();
    window_content.add(content).fill().expand().top();

    // =========================
    // BOUTONS
    // =========================

    // space on the left, right and between the buttons
    float pad = (width - button1.getWidth() - button2.getWidth()) / 3f;
    // Table to hold the buttons, so they are aligned horizontally
    Table buttonRow = new Table();
    buttonRow.add(button1).size(button1.getWidth(), button1.getHeight()).padRight(pad);
    buttonRow.add(button2).size(button2.getWidth(), button2.getHeight());

    window_content.row();
    window_content.add(buttonRow).expand().bottom().padBottom(10);

    this.add(window).fill().expand();

    // =========================
    // set internal variables
    // =========================

    this.button1 = button1;
    this.button2 = button2;
  }

  public Button getButton1() {
    return button1;
  }

  public Button getButton2() {
    return button2;
  }

  public Window getWindow() {
    return window;
  }

  public Table getContent() {
    return content;
  }
}
