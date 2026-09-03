package com.pdg.logic.Entity;

import com.badlogic.gdx.physics.box2d.Body;

import static com.pdg.logic.utils.StaticValues.DAMAGETRESHOLD;

public class Entity {

    protected String type;
    protected int health;
    protected Body body;
    protected int mass;

    protected float width;
    protected float height;

    public Entity(
            String type,
            int health,
            Body body,
            int mass,
            float height,
            float width
    ) {
        this.type = type;
        this.health = health;
        this.body = body;
        this.mass = mass;
        this.height = height;
        this.width = width;
    }

    public void takeDamage(int health) {
        this.health -= health;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int damageoutput() {
        int damage = Math.round(
                this.body.getLinearVelocity().len() * mass
        );

        return damage > DAMAGETRESHOLD ? damage : 0;
    }

    public Body getBody() {
        return body;
    }
    
}