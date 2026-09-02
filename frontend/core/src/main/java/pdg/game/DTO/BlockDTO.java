package pdg.game.DTO;

/** Represents a block. */
public class BlockDTO {

  private final String type;
  private final int health;
  private final int mass;
  private final boolean alive;
  private final int x;
  private final int y;
  private final int length;

  public BlockDTO(String type, int health, int mass, boolean alive, int x, int y, int length) {
    this.type = type;
    this.health = health;
    this.mass = mass;
    this.alive = alive;
    this.x = x;
    this.y = y;
    this.length = length;
  }

  public String type() {
    return type;
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

  public int x() {
    return x;
  }

  public int y() {
    return y;
  }

  public int length() {
    return length;
  }
}
