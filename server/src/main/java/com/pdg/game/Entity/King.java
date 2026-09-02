package com.pdg.game.Entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

public class King extends Entity {

  private boolean physicsEnabled = false;
  private Vector2 savedPosition;

  public King(Integer health, Body body, int mass, float height, float width) {
    super(health, body, mass, height, width);

    setGravityEnabled(false);
  }

  /** Active/désactive la gravité et la physique dynamique du king. */
  public void setGravityEnabled(boolean enabled) {

    physicsEnabled = enabled;

    if (enabled) {

      body.setType(BodyDef.BodyType.DynamicBody);

    } else {

      body.setLinearVelocity(0, 0);
      body.setType(BodyDef.BodyType.KinematicBody);
    }
  }

  /** Capture la position actuelle. */
  public void savePosition() {
    savedPosition = body.getPosition().cpy();
  }

  /** Replace le king à sa dernière position sauvegardée. */
  public void restorePosition() {

    if (savedPosition == null) {
      return;
    }

    setGravityEnabled(false);

    body.setTransform(savedPosition, 0);
  }
}
