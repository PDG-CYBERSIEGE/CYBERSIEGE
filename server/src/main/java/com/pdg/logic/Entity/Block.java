package com.pdg.logic.Entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import static com.pdg.logic.utils.StaticValues.HEAVYSPAWN;
import static com.pdg.logic.utils.StaticValues.LIGHTSPAWN;
import static com.pdg.logic.utils.StaticValues.MEDIUMSPAWN;

public class Block extends Entity {

    private boolean physicsEnabled = false;

    private final String type;
    private final Vector2 spawnPosition;

    private static final float SPAWN_THRESHOLD = 0.3f;

    private Vector2 savedPosition;
    private float savedAngle;

    public Block(
            int health,
            Body body,
            int mass,
            float height,
            float width,
            String type,
            int length
    ) {
        super(health, body, mass, height, width);

        this.type = type;

        switch (type) {
            case "HEAVY" -> spawnPosition = HEAVYSPAWN;
            case "MEDIUM" -> spawnPosition = MEDIUMSPAWN;
            case "LIGHT" -> spawnPosition = LIGHTSPAWN;
            default -> spawnPosition = null;
        }

        setGravityEnabled(false);
    }

    /**
     * Active/désactive la physique dynamique du bloc.
     */
    public void setGravityEnabled(boolean enabled) {

        physicsEnabled = enabled;

        if (enabled && !isAtSpawn()) {

            body.setType(BodyDef.BodyType.DynamicBody);
            body.setGravityScale(1f);

        } else {

            body.setLinearVelocity(0, 0);
            body.setAngularVelocity(0);
            body.setType(BodyDef.BodyType.KinematicBody);
        }
    }

    /**
     * Vrai si le bloc est encore proche de sa position de spawn.
     */
    public boolean isAtSpawn() {

        if (spawnPosition == null) {
            return false;
        }

        return body.getPosition().dst(spawnPosition) < SPAWN_THRESHOLD;
    }

    /**
     * Sauvegarde la position actuelle du bloc.
     */
    public void savePosition() {

        savedPosition = body.getPosition().cpy();
        savedAngle = body.getAngle();
    }

    /**
     * Replace le bloc à sa dernière position sauvegardée.
     */
    public void restorePosition() {

        if (savedPosition == null) {
            return;
        }

        setGravityEnabled(false);

        body.setTransform(
                savedPosition,
                savedAngle
        );
    }

    /**
     * Replace le bloc à sa position initiale.
     */
    public void initialState() {

        if (spawnPosition == null) {
            return;
        }

        body.setTransform(
                spawnPosition,
                0
        );

        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
    }

    public String getType() {
        return type;
    }

    public boolean isPhysicsEnabled() {
        return physicsEnabled;
    }
}