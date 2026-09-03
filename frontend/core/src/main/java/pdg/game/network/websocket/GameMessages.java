package pdg.game.network.websocket;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import pdg.game.DTO.BlockDTO;
import pdg.game.DTO.KingDTO;
import pdg.game.DTO.RobotDTO;
import pdg.game.DTO.TeamDTO;

/**
 * Utility class used to parse and build WebSocket messages exchanged between the client and the
 * server.
 *
 * <p>JSON parsing is performed manually using {@link JsonValue} to avoid relying on LibGDX
 * reflection, which can cause issues with the GWT backend.
 */
public final class GameMessages {

  private static final JsonReader READER = new JsonReader();

  private GameMessages() {}

  // Incoming ////////////////////////////////////////////////////

  /**
   * Returns the type of a WebSocket message.
   *
   * @param message JSON message
   * @return message type, or an empty string if none is present
   */
  public static String type(String message) {
    JsonValue root = READER.parse(message);
    return root.getString("type", "");
  }

  /**
   * Parses a start message.
   *
   * @param message JSON message
   * @return parsed start message
   */
  public static Start parseStart(String message) {
    JsonValue root = READER.parse(message);
    Start start = new Start();
    start.type = root.getString("type", "");
    start.matchId = root.getString("matchId", "");
    start.opponent = root.getString("opponent", "");
    return start;
  }

  /**
   * Parses an available components message.
   *
   * @param message JSON message
   * @return parsed available components message
   */
  public static AvailableComponents parseAvailableComponents(String message) {
    JsonValue root = READER.parse(message);
    AvailableComponents result = new AvailableComponents();
    result.type = root.getString("type", "");
    result.blocks = readBlocks(root.get("blocks"));
    result.king = readKing(root.get("king"));
    result.robots = readRobots(root.get("robots"));
    return result;
  }

  /**
   * Parses a timer message.
   *
   * @param message JSON message
   * @return parsed timer message
   */
  public static Timer parseTimer(String message) {
    JsonValue root = READER.parse(message);
    Timer timer = new Timer();
    timer.type = root.getString("type", "");
    timer.timeRemaining = root.getInt("timeRemaining", 0);
    return timer;
  }

  /**
   * Parses an opponent structure message.
   *
   * @param message JSON message
   * @return parsed opponent structure message
   */
  public static OpponentStructure parseOpponentStructure(String message) {
    JsonValue root = READER.parse(message);
    OpponentStructure result = new OpponentStructure();
    result.type = root.getString("type", "");
    result.blocks = readBlocks(root.get("blocks"));
    result.king = readKing(root.get("king"));
    return result;
  }

  /**
   * Parses a result message.
   *
   * @param message JSON message
   * @return parsed result message
   */
  public static Result parseResult(String message) {
    JsonValue root = READER.parse(message);
    Result result = new Result();
    result.type = root.getString("type", "");
    return result;
  }

  /**
   * Parses a team message.
   *
   * @param message JSON message
   * @return parsed team message
   */
  public static Team parseTeam(String message) {
    JsonValue root = READER.parse(message);
    Team result = new Team();
    result.type = root.getString("type", "");
    result.team = readTeam(root.get("team"));
    return result;
  }

  /**
   * Parses a build validation result message.
   *
   * @param message JSON message
   * @return parsed build validation result
   */
  public static BuildValidate parseBuildValidate(String message) {
    JsonValue root = READER.parse(message);

    BuildValidate result = new BuildValidate();
    result.type = root.getString("type", "");
    result.valid = root.getBoolean("valid", false);

    return result;
  }

  /**
   * Parses a fire message.
   *
   * @param message JSON message
   * @return parsed fire message
   */
  public static Fire parseFire(String message) {
    JsonValue root = READER.parse(message);

    Fire fire = new Fire();
    fire.type = root.getString("type", "");
    fire.power = root.getInt("power", 0);
    fire.angle = root.getFloat("angle", 0f);
    fire.robot = root.getInt("robot", 0);

    return fire;
  }

  // Outgoing ////////////////////////////////////////////////////

  /**
   * Builds a structure validation message.
   *
   * @param team team to validate
   * @return JSON message
   */
  public static String buildValidate(TeamDTO team) {
    StringBuilder sb = new StringBuilder();

    sb.append("{\"type\":\"BUILD_VALIDATE\",\"team\":");
    appendTeam(sb, team);
    sb.append('}');

    return sb.toString();
  }

  /**
   * Builds a fire message.
   *
   * @param power firing power
   * @param angle firing angle
   * @param robot robot used for the attack
   * @return JSON message
   */
  public static String fire(int power, float angle, int robot) {
    StringBuilder sb = new StringBuilder();
    sb.append("{\"type\":\"FIRE\",\"power\":")
        .append(power)
        .append(",\"angle\":")
        .append(angle)
        .append(",\"robot\":")
        .append(robot)
        .append('}');
    return sb.toString();
  }

  /**
   * Builds a state verification message.
   *
   * @return JSON message
   */
  public static String verifyState() {
    return "{\"type\":\"VERIFY_STATE\"}";
  }

  /**
   * Builds a team message.
   *
   * @param team team to send
   * @return JSON message
   */
  public static String team(TeamDTO team) {
    StringBuilder sb = new StringBuilder();
    sb.append("{\"type\":\"TEAM\",\"team\":");
    appendTeam(sb, team);
    sb.append('}');
    return sb.toString();
  }

  // Message DTOs ////////////////////////////////////////////////

  /** Represents a match start message. */
  public static class Start {
    public String type;
    public String matchId;
    public String opponent;
  }

  /** Represents a message containing the components available to a player. */
  public static class AvailableComponents {
    public String type;
    public BlockDTO[] blocks;
    public KingDTO king;
    public RobotDTO[] robots;
  }

  /** Represents a timer update message. */
  public static class Timer {
    public String type;
    public int timeRemaining;
  }

  /** Represents the opponent's structure message. */
  public static class OpponentStructure {
    public String type;
    public BlockDTO[] blocks;
    public KingDTO king;
  }

  /** Represents a game result message. */
  public static class Result {
    public String type;
  }

  /** Represents a team message, exchanged in both directions. */
  public static class Team {
    public String type;
    public TeamDTO team;
  }

  /** Represents a build validation result received from the server. */
  public static class BuildValidate {
    public String type;
    public boolean valid;
  }

  /** Represents a fire message. */
  public static class Fire {
    public String type;
    public int power;
    public float angle;
    public int robot;
  }

  // Reads helpers //////////////////////////////////////////////////////

  private static BlockDTO[] readBlocks(JsonValue value) {
    if (value == null || value.isNull() || value.size == 0) {
      return new BlockDTO[0];
    }
    BlockDTO[] blocks = new BlockDTO[value.size];
    int index = 0;
    for (JsonValue child = value.child; child != null; child = child.next) {
      blocks[index++] = readBlock(child);
    }
    return blocks;
  }

  private static BlockDTO readBlock(JsonValue value) {
    return new BlockDTO(
        value.getString("type", ""),
        value.getInt("health", 0),
        value.getInt("mass", 0),
        value.getBoolean("alive", false),
        value.getInt("x", 0),
        value.getInt("y", 0),
        value.getInt("length", 0));
  }

  private static KingDTO readKing(JsonValue value) {
    if (value == null || value.isNull()) {
      return null;
    }
    return new KingDTO(
        value.getString("type", ""),
        value.getInt("x", 0),
        value.getInt("y", 0),
        value.getInt("health", 0),
        value.getInt("mass", 0));
  }

  private static RobotDTO[] readRobots(JsonValue value) {
    if (value == null || value.isNull() || value.size == 0) {
      return new RobotDTO[0];
    }
    RobotDTO[] robots = new RobotDTO[value.size];
    int index = 0;
    for (JsonValue child = value.child; child != null; child = child.next) {
      robots[index++] = readRobot(child);
    }
    return robots;
  }

  private static RobotDTO readRobot(JsonValue value) {
    return new RobotDTO(
        value.getString("type", ""),
        value.getInt("health", 0),
        value.getInt("mass", 0),
        value.getInt("cooldown", 0));
  }

  private static ArrayList<BlockDTO> readBlockList(JsonValue value) {
    ArrayList<BlockDTO> blocks = new ArrayList<>();
    if (value == null || value.isNull()) {
      return blocks;
    }
    for (JsonValue child = value.child; child != null; child = child.next) {
      blocks.add(readBlock(child));
    }
    return blocks;
  }

  private static ArrayList<RobotDTO> readRobotList(JsonValue value) {
    ArrayList<RobotDTO> robots = new ArrayList<>();
    if (value == null || value.isNull()) {
      return robots;
    }
    for (JsonValue child = value.child; child != null; child = child.next) {
      robots.add(readRobot(child));
    }
    return robots;
  }

  private static TeamDTO readTeam(JsonValue value) {
    if (value == null || value.isNull()) {
      return null;
    }
    return new TeamDTO(
        value.getString("name", ""),
        readBlockList(value.get("blocks")),
        readRobotList(value.get("robots")),
        readKing(value.get("king")));
  }

  // Writing helpers /////////////////////////////////////////////

  private static void appendBlock(StringBuilder sb, BlockDTO block) {
    sb.append("{\"type\":\"")
        .append(escape(block.type()))
        .append("\",\"health\":")
        .append(block.health())
        .append(",\"mass\":")
        .append(block.mass())
        .append(",\"alive\":")
        .append(block.alive())
        .append(",\"x\":")
        .append(block.x())
        .append(",\"y\":")
        .append(block.y())
        .append(",\"length\":")
        .append(block.length())
        .append('}');
  }

  private static void appendKing(StringBuilder sb, KingDTO king) {
    sb.append("{\"type\":\"")
        .append(escape(king.sprite()))
        .append("\",\"x\":")
        .append(king.x())
        .append(",\"y\":")
        .append(king.y())
        .append(",\"health\":")
        .append(king.health())
        .append(",\"mass\":")
        .append(king.mass())
        .append('}');
  }

  private static void appendRobot(StringBuilder sb, RobotDTO robot) {
    sb.append("{\"type\":\"")
        .append(escape(robot.sprite()))
        .append("\",\"health\":")
        .append(robot.health())
        .append(",\"mass\":")
        .append(robot.mass())
        .append(",\"cooldown\":")
        .append(robot.cooldown())
        .append('}');
  }

  private static void appendTeam(StringBuilder sb, TeamDTO team) {
    sb.append("{\"name\":\"").append(escape(team.name())).append("\",\"blocks\":[");
    ArrayList<BlockDTO> blocks = team.blocks();
    for (int i = 0; i < blocks.size(); i++) {
      if (i > 0) {
        sb.append(',');
      }
      appendBlock(sb, blocks.get(i));
    }
    sb.append("],\"robots\":[");
    ArrayList<RobotDTO> robots = team.robots();
    for (int i = 0; i < robots.size(); i++) {
      if (i > 0) {
        sb.append(',');
      }
      appendRobot(sb, robots.get(i));
    }
    sb.append("],\"king\":");
    appendKing(sb, team.king());
    sb.append('}');
  }

  private static String escape(String value) {
    if (value == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder(value.length());
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      switch (c) {
        case '"':
          sb.append("\\\"");
          break;
        case '\\':
          sb.append("\\\\");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\r':
          sb.append("\\r");
          break;
        case '\t':
          sb.append("\\t");
          break;
        default:
          if (c < 0x20) {
            sb.append("\\u");
            String hex = Integer.toHexString(c);
            for (int pad = hex.length(); pad < 4; pad++) {
              sb.append('0');
            }
            sb.append(hex);
          } else {
            sb.append(c);
          }
      }
    }
    return sb.toString();
  }
}
