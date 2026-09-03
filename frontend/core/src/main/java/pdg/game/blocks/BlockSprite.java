package pdg.game.blocks;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Base block of the game. This class represents a destructible element displayed in the world. It
 * extends Table so it can contain several graphical sub-elements, such as left, middle and right
 * images to form a longer block.
 */
public class BlockSprite extends Table {
  /**
   * Resizes the block according to a size factor. The width depends on the number of child
   * elements, allowing blocks made of several pieces to be assembled.
   *
   * @param factor unit size used for resizing
   */
  public void resize(int factor) {
    int ratio = getChildren().size;

    setSize(factor * ratio, factor);
    setOrigin(getWidth() / 2f, getHeight() / 2f);
  }

  public void disposeTextures() {
    for (Actor child : getChildren()) {
      if (child instanceof Image) {
        Drawable drawable = ((Image) child).getDrawable();
        if (drawable instanceof TextureRegionDrawable) {
          ((TextureRegionDrawable) drawable).getRegion().getTexture().dispose();
        }
      }
    }
  }
}
