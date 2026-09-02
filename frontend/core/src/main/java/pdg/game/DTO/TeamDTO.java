package pdg.game.DTO;

import java.util.ArrayList;

/** Represents a team. */
public class TeamDTO {

  private final String name;
  private final ArrayList<BlockDTO> blocks;
  private final ArrayList<RobotDTO> robots;
  private final KingDTO king;

  public TeamDTO(
      String name, ArrayList<BlockDTO> blocks, ArrayList<RobotDTO> robots, KingDTO king) {
    this.name = name;
    this.blocks = blocks;
    this.robots = robots;
    this.king = king;
  }

  public String name() {
    return name;
  }

  public ArrayList<BlockDTO> blocks() {
    return blocks;
  }

  public ArrayList<RobotDTO> robots() {
    return robots;
  }

  public KingDTO king() {
    return king;
  }
}
