package com.pdg.logic.Entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.pdg.logic.DTO.KingDTO;

public class King extends Entity {

    private boolean physicsEnabled = false;
    private Vector2 savedPosition;
    
    public King(
            String type,
            Integer health,
            Body body,
            int mass,
            float height,
            float width
    ) {
        super(type, health, body, mass, height, width);

        setGravityEnabled(false);
    }

    /**
     * Active/désactive la gravité et la physique dynamique du king.
     */
    public void setGravityEnabled(boolean enabled) {

        physicsEnabled = enabled;

        if (enabled) {

            body.setType(BodyDef.BodyType.DynamicBody);

        } else {

            body.setLinearVelocity(0, 0);
            body.setType(BodyDef.BodyType.KinematicBody);
        }
    }

    /**
     * Capture la position actuelle.
     */
    public void savePosition() {
        savedPosition = body.getPosition().cpy();
    }

    /**
     * Replace le king à sa dernière position sauvegardée.
     */
    public void restorePosition() {

        if (savedPosition == null) {
            return;
        }

        setGravityEnabled(false);

        body.setTransform(savedPosition, 0);
    }

    public KingDTO getDTO() {
        return new KingDTO(type, body.getPosition().x, body.getPosition().y, health, mass);
    }
}