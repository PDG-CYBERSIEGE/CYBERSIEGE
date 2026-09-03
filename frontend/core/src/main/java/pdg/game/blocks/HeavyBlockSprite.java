package pdg.game.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/** Block with high health, built from left, middle and right textures. */
public class HeavyBlockSprite extends BlockSprite {

  /**
   * Creates a heavy block with the requested number of segments.
   *
   * @param length number of visual segments
   */
  public HeavyBlockSprite(int length) {
    // A block always needs at least two end segments.
    if (length < 2) length = 2;

    // Load the textures used for the two ends and the repeatable middle.
    Texture texture_left = new Texture(Gdx.files.internal("blocks/heavy_left.png"));
    Texture texture_middle = new Texture(Gdx.files.internal("blocks/heavy_middle.png"));
    Texture texture_right = new Texture(Gdx.files.internal("blocks/heavy_right.png"));

    // Enable rotation around the centre of the complete block.
    setSize(length, 1);
    setTransform(true);
    setOrigin(getWidth() / 2f, getHeight() / 2f);

    // Assemble the block from its left end, middle segments and right end.
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
