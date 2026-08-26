package pdg.game.Entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;


public class Robot extends Entity {

    private int cooldown;

    public Robot(Texture sprite, Integer health, Body body, int mass) {
        super(health, sprite, body, mass);
    }

    public void reduceCooldown (){
        cooldown--;
    }
}
