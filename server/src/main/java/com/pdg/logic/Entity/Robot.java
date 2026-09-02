package com.pdg.logic.Entity;

import com.badlogic.gdx.physics.box2d.Body;

public class Robot extends Entity {

    private int cooldown;
    private int currentCooldown;

    public Robot(
            Integer health,
            Body body,
            int mass,
            int cooldown,
            float height,
            float width
    ) {
        super(health, body, mass, height, width);
        this.cooldown = cooldown;
        this.currentCooldown = 0;
    }

    public void reduceCooldown() {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }

    public void startCooldown() {
        currentCooldown = cooldown;
    }

    public boolean isReady() {
        return currentCooldown == 0;
    }
}