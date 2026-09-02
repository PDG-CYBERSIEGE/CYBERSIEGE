package pdg.game.DTO;

/** Represents a robot. */
public class RobotDTO {

  private final String sprite;
  private final int health;
  private final int mass;
  private final int cooldown;

  public RobotDTO(String sprite, int health, int mass, int cooldown) {
    this.sprite = sprite;
    this.health = health;
    this.mass = mass;
    this.cooldown = cooldown;
  }

  public String sprite() {
    return sprite;
  }

  public int health() {
    return health;
  }

  public int mass() {
    return mass;
  }

  public int cooldown() {
    return cooldown;
  }
}
