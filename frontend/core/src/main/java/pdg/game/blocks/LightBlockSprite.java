package pdg.game.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Light block. It represents a smaller and less robust element. Its texture is repeated several
 * times depending on the requested length.
 */
public class LightBlockSprite extends BlockSprite {
  /**
   * Constructor for a light block.
   *
   * @param length number of block segments
   */
  public LightBlockSprite(int length) {
    if (length < 2) length = 2;

    Texture texture = new Texture(Gdx.files.internal("blocks/light.png"));
    setSize(length, 1);
    setTransform(true);
    setOrigin(getWidth() / 2f, getHeight() / 2f);

    for (int i = 0; i < length * 2; i++) {
      add(new Image(texture));
    }
  }
}
