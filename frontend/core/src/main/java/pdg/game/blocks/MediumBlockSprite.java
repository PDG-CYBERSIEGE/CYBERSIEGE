package pdg.game.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Medium block. It is stronger than a light block and is composed of three textures: left, middle
 * and right, to form a homogeneous bar.
 */
public class MediumBlockSprite extends BlockSprite {

  /**
   * Constructor for a medium block.
   *
   * @param length number of block segments
   */
  public MediumBlockSprite(int length) {

    if (length < 2) length = 2;

    Texture texture_left = new Texture(Gdx.files.internal("blocks/medium_left.png"));
    Texture texture_middle = new Texture(Gdx.files.internal("blocks/medium_middle.png"));
    Texture texture_right = new Texture(Gdx.files.internal("blocks/medium_right.png"));

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
