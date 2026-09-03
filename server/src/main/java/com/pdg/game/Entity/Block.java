package com.pdg.game.Entity;

import static com.pdg.game.utils.StaticValues.HEAVYSPAWN;
import static com.pdg.game.utils.StaticValues.LIGHTSPAWN;
import static com.pdg.game.utils.StaticValues.MEDIUMSPAWN;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.pdg.game.DTO.BlockDTO;

public class Block extends Entity {

  private boolean physicsEnabled = true;

  private final long UUID;
  private final String type;
  private final int length;
  private final Vector2 spawnPosition;

  private static final float SPAWN_THRESHOLD = 0.3f;

  private Vector2 savedPosition;
  private float savedAngle;

  public Block(
      String type,
      long UUID,
      int health,
      Body body,
      int mass,
      float height,
      float width,
      int length) {
    super(type, health, body, mass, height, width);

    this.type = type;
    this.UUID = UUID;
    this.length = length;

    switch (type) {
      case "HEAVY" -> spawnPosition = HEAVYSPAWN;
      case "MEDIUM" -> spawnPosition = MEDIUMSPAWN;
      case "LIGHT" -> spawnPosition = LIGHTSPAWN;
      default -> spawnPosition = null;
    }

    setGravityEnabled(true);
  }

  /** Active/désactive la physique dynamique du bloc. */
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

  /** Vrai si le bloc est encore proche de sa position de spawn. */
  public boolean isAtSpawn() {

    if (spawnPosition == null) {
      return false;
    }

    return body.getPosition().dst(spawnPosition) < SPAWN_THRESHOLD;
  }

  /** Sauvegarde la position actuelle du bloc. */
  public void savePosition() {

    savedPosition = body.getPosition().cpy();
    savedAngle = body.getAngle();
  }

  /** Replace le bloc à sa dernière position sauvegardée. */
  public void restorePosition() {

    if (savedPosition == null) {
      return;
    }

    setGravityEnabled(false);

    body.setTransform(savedPosition, savedAngle);
  }

  /** Replace le bloc à sa position initiale. */
  public void initialState() {

    if (spawnPosition == null) {
      return;
    }

    body.setTransform(spawnPosition, 0);

    body.setLinearVelocity(0, 0);
    body.setAngularVelocity(0);
  }

  public String getType() {
    return type;
  }

  public boolean isPhysicsEnabled() {
    return physicsEnabled;
  }

  public BlockDTO getDTO() {
    return new BlockDTO(
        type,
        UUID,
        health,
        mass,
        true,
        body.getPosition().x - width / 2f,
        body.getPosition().y - height / 2f,
        savedAngle,
        length);
  }
}
