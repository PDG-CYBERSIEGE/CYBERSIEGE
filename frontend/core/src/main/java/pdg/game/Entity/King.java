package pdg.game.Entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;

public class King extends Entity {

    public King(Texture sprite, Integer health, Body body, int mass){
        super(health, sprite, body, mass);
    }
}
