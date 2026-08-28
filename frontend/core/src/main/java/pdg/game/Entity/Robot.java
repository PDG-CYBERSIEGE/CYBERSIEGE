package pdg.game.Entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;


public class Robot extends Entity {

    private int cooldown;
    private int currentCooldown;

    public Robot(Texture sprite, Integer health, Body body, int mass, int cooldown) {
        super(health, sprite, body, mass);
        this.cooldown = cooldown;
        this.currentCooldown = 0;
    }

    public void reduceCooldown (){
        currentCooldown--;
    }

    public void startCooldown(){
        currentCooldown = cooldown;
    }

    public boolean isReady() {
        return currentCooldown == 0;
    }
}
