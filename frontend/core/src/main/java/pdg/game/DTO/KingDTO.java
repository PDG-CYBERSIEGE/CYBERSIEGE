package pdg.game.DTO;

/** Represents a king. */
public class KingDTO {

  private final String sprite;
  private final int x;
  private final int y;
  private final int health;
  private final int mass;

  public KingDTO(String type, float x, float y, int health, int mass) {
    this.sprite = type;
    this.x = (int) x;
    this.y = (int) y;
    this.health = health;
    this.mass = mass;
  }

  public String sprite() {
    return sprite;
  }

  public int x() {
    return x;
  }

  public int y() {
    return y;
  }

  public int health() {
    return health;
  }

  public int mass() {
    return mass;
  }
}
