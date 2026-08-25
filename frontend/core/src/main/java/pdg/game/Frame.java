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

  private Button[] buttons;
  private Window window;
  private Table content;

  public Frame(Button... buttons) {
    this(400, 300, "Frame Title", buttons);
  }

  public Frame(float width, float height, String title, Button... buttons) {

    // =========================
    // TEST PARAMETERS
    // =========================

    if (width <= 0 || height <= 0 || title == null
        || buttons == null || buttons.length <= 0) {
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

    TiledDrawable backgroundTexture = new TiledDrawable(
        new TextureRegion(
            new Texture(Gdx.files.internal(
                "gui/frame_background/base_frame.png"
            ))
        )
    );

    content = new Table();
    Table windowContent = new Table();

    windowContent.setBackground(backgroundTexture);

    windowContent.add(content)
        .fill()
        .expand()
        .top();

    window.add(windowContent)
        .fill()
        .expand();

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
      buttonRow.add(button)
          .size(button.getWidth(), button.getHeight())
          .padRight(pad);
    }

    windowContent.row();

    windowContent.add(buttonRow)
        .expand()
        .bottom()
        .padBottom(10);

    add(window)
        .fill()
        .expand();

    // =========================
    // set internal variables
    // =========================

    this.buttons = buttons;
  }

  public Button getButton(int i) {
    return buttons[i];
  }

  public Window getWindow() {
    return window;
  }

  public Table getContent() {
    return content;
  }
}