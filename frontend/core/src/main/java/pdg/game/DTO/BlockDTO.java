package pdg.game.DTO;

/** Represents a block. */
public class BlockDTO {

  private final String type;
  private final long uuid;
  private final int health;
  private final int mass;
  private final boolean alive;
  private final float x;
  private final float y;
  private final float angle;
  private final int length;

  public BlockDTO(String type, long uuid, int health, int mass, boolean alive, float x, float y, float angle, int length) {
    this.type = type;
    this.uuid = uuid;
    this.health = health;
    this.mass = mass;
    this.alive = alive;
    this.x = x;
    this.y = y;
    this.angle = angle;
    this.length = length;
  }

  public String type() {
    return type;
  }

  public long uuid() {
    return uuid;
  }

  public int health() {
    return health;
  }

  public int mass() {
    return mass;
  }

  public boolean alive() {
    return alive;
  }

  public float x() {
    return x;
  }

  public float y() {
    return y;
  }

  public float angle() {
    return angle;
  }

  public int length() {
    return length;
  }
}
