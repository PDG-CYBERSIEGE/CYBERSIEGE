package pdg.game.DTO;

/** Represents a block. */
public class BlockDTO {

  private final String type;
  private final long UUID;
  private final int health;
  private final int mass;
  private final boolean alive;
  private final int x;
  private final int y;
  private final int length;

  public BlockDTO(String type, long UUID, int health, int mass, boolean alive, float x, float y, int length) {
    this.UUID = UUID;
    this.type = type;
    this.health = health;
    this.mass = mass;
    this.alive = alive;
    this.x = (int) x;
    this.y = (int) y;
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

  public long UUID(){return UUID;}

  public float angle(){return 1l;}
}
