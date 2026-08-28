package pdg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;

/** Reusable window frame containing custom content and action buttons. */
public class Frame extends Table {

  /** Buttons displayed at the bottom of the frame. */
  private Button[] buttons;

  /** Window providing the frame border and title. */
  private Window window;

  /** Table that callers can populate with custom controls. */
  private Table content;

  /** Creates a default 400 by 300 frame. */
  public Frame(Button... buttons) {
    this(400, 300, "Frame Title", buttons);
  }

  /**
   * Creates a frame with the requested size, title and buttons.
   *
   * @param width frame width
   * @param height frame height
   * @param title title displayed in the window border
   * @param buttons buttons displayed at the bottom
   */
  public Frame(float width, float height, String title, Button... buttons) {

    // =========================
    // TEST PARAMETERS
    // =========================

    if (width <= 0 || height <= 0 || title == null || buttons == null || buttons.length <= 0) {
      return;
    }

    // =========================
    // SKIN
    // =========================

    Skin skin = new Skin(Gdx.files.internal("futuristic_ui/uiskin.json"));

    setSize(width, height);

    // =========================
    // frame window for the borders
    // =========================

    window = new Window(title, skin);
    window.getTitleLabel().setAlignment(Align.center);
    window.setMovable(false);

    // =========================
    // Content (background) of the frame
    // background is a TiledDrawable, so it will repeat the texture to fill the area.
    // it is set as the background of a Table, which is added to the window.
    // =========================

    TiledDrawable backgroundTexture =
        new TiledDrawable(
            new TextureRegion(
                new Texture(Gdx.files.internal("futuristic_ui/frame_background/base_frame.png"))));
    content = new Table();
    Table windowContent = new Table();

    windowContent.setBackground(backgroundTexture);
    windowContent.add(content).fill().expand().top().left().pad(2);
    window.add(windowContent).fill().expand();
    // =========================
    // BOUTONS
    // =========================

    float pad = width;

    for (Button button : buttons) {
      pad -= button.getWidth();
    }

    pad /= 3f;

    Table buttonRow = new Table();
    buttonRow.padLeft(pad);

    for (Button button : buttons) {
      buttonRow.add(button).size(button.getWidth(), button.getHeight()).padRight(pad);
    }

    windowContent.row();
    windowContent.add(buttonRow).expand().bottom().padBottom(15);

    add(window).fill().expand();

    // =========================
    // set internal variables
    // =========================

    this.buttons = buttons;
  }

  /** Returns a button by its position in the button row. */
  public Button getButton(int i) {
    return buttons[i];
  }

  /** Returns the table where custom controls can be added. */
  public Table getContent() {
    return content;
  }
}
