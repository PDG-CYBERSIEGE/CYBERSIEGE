package com.pdg.game.Entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.pdg.game.DTO.RobotDTO;

public class Robot extends Entity {

  private int id;
  private int cooldown;
  private int currentCooldown;

  public Robot(
      String type,
      int id,
      Integer health,
      Body body,
      int mass,
      int cooldown,
      float height,
      float width) {
    super(type, health, body, mass, height, width);
    this.id = id;
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

  public RobotDTO getDTO() {
    return new RobotDTO(type, id, health, mass, cooldown);
  }
}
