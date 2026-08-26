package pdg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class HeavyBlock extends Block {

  private static final float MAX_HEALTH = 300;

  public HeavyBlock(int length) {
    super(MAX_HEALTH);

    if (length < 2) length = 2;

    Texture texture_left = new Texture(Gdx.files.internal("blocks/heavy_left.png"));
    Texture texture_middle = new Texture(Gdx.files.internal("blocks/heavy_middle.png"));
    Texture texture_right = new Texture(Gdx.files.internal("blocks/heavy_right.png"));

    setSize(length, 1);
    setTransform(true);
    setOrigin(getWidth() / 2f, getHeight() / 2f);

    for (int i = 0; i < length; i++) {
      if (i == 0) {
        add(new Image(texture_left));
      } else if (i == (length - 1)) {
        add(new Image(texture_right));
      } else {
        add(new Image(texture_middle));
      }
    }
  }
}
