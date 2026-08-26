package pdg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Light block.
 * It represents a smaller and less robust element.
 * Its texture is repeated several times depending on the requested length.
 */
public class LightBlock extends Block {

    /**
     * Maximum health of a light block.
     */
    private static final float MAX_HEALTH = 50;

    /**
     * Constructor for a light block.
     *
     * @param length number of block segments
     */
    public LightBlock(int length) {
        super(MAX_HEALTH);

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