package com.pdg.game.Entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Canon {

  private final World world;
  private final Vector2 canonPosition;

  private Robot loadedRobot;

  public Canon(World world, Vector2 canonPosition) {
    this.world = world;
    this.canonPosition = canonPosition;
  }

  /** Charge un robot dans le canon. */
  public void loadRobot(Robot robot) {
    if (robot == null || !robot.isReady()) {
      return;
    }

    loadedRobot = robot;
  }

  /**
   * Tire le robot actuellement chargé.
   *
   * @param power puissance du tir
   * @param angle angle du tir en degrés
   */
  public void fire(float power, float angle) {

    if (loadedRobot == null) {
      return;
    }

    if (!loadedRobot.isReady()) {
      return;
    }

    Vector2 launchVelocity = new Vector2(power, 0f).setAngleDeg(angle);

    loadedRobot.getBody().setTransform(canonPosition.x, canonPosition.y, 0f);

    loadedRobot.getBody().setLinearVelocity(launchVelocity);

    loadedRobot.startCooldown();

    loadedRobot.getBody().setBullet(true);

    loadedRobot = null;
  }

  public boolean hasRobotLoaded() {
    return loadedRobot != null;
  }

  public Robot getLoadedRobot() {
    return loadedRobot;
  }

  public Vector2 getCanonPosition() {
    return canonPosition;
  }
}
