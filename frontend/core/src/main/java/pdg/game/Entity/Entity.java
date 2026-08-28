package pdg.game.Entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import static pdg.game.utils.StaticValues.DAMAGETRESHOLD;

public class Entity {
    protected int health;
    protected Texture sprite;
    protected Vector2 pos;
    protected Body body;
    protected int mass;


    public Entity (int health, Texture sprite, Body body, int mass){
        this.health = health;
        this.sprite = sprite;
        this.body = body;
        this.mass = mass;
    }

    public void setPos(Vector2 pos){
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
