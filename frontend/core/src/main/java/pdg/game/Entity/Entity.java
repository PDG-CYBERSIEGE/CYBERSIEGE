package pdg.game.Entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import jdk.internal.net.http.common.Pair;

import static pdg.game.utils.StaticValues.DAMAGETRESHOLD;

public class Entity {
    protected int health;
    protected Texture sprite;
    protected Pair<Integer, Integer> pos;
    protected Body body;
    protected int mass;


    public Entity (int health, Texture sprite, Body body, int mass){
        this.health = health;
        this.sprite = sprite;
        this.body = body;
        this.mass = mass;
    }

    public void setPos(Pair<Integer, Integer> pos){
        this.pos = pos;
    }

    public void takeDamage (int health) {
        this.health -= health;
    }

    public boolean isDead(){
        return health <= 0;
    }

    public int damageoutput(){
        int damage = Math.round(this.body.getLinearVelocity().len() * mass);
        return damage > DAMAGETRESHOLD ? damage : 0;
    }

    public Texture getSprite() {
        return sprite;
    }

    public Body getBody() {
        return body;
    }

}
