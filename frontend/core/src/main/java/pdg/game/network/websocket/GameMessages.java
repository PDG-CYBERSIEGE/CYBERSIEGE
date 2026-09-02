package pdg.game.network.websocket;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import pdg.game.DTO.BlockDTO;
import pdg.game.DTO.KingDTO;
import pdg.game.DTO.RobotDTO;

public final class GameMessages {

  private static final JsonReader READER = new JsonReader();

  private GameMessages() {}

  // Incoming ////////////////////////////////////////////////////

  public static String type(String message) {
    JsonValue root = READER.parse(message);
    return root.getString("type", "");
  }

  public static Start parseStart(String message) {
    JsonValue root = READER.parse(message);
    Start start = new Start();
    start.type = root.getString("type", "");
    start.matchId = root.getString("matchId", "");
    start.opponent = root.getString("opponent", "");
    return start;
  }

  public static AvailableComponents parseAvailableComponents(String message) {
    JsonValue root = READER.parse(message);
    AvailableComponents result = new AvailableComponents();
    result.type = root.getString("type", "");
    result.blocks = readBlocks(root.get("blocks"));
    result.king = readKing(root.get("king"));
    result.robots = readRobots(root.get("robots"));
    return result;
  }

  public static Timer parseTimer(String message) {
    JsonValue root = READER.parse(message);
    Timer timer = new Timer();
    timer.type = root.getString("type", "");
    timer.timeRemaining = root.getInt("timeRemaining", 0);
    return timer;
  }

  public static OpponentStructure parseOpponentStructure(String message) {
    JsonValue root = READER.parse(message);
    OpponentStructure result = new OpponentStructure();
    result.type = root.getString("type", "");
    result.blocks = readBlocks(root.get("blocks"));
    result.king = readKing(root.get("king"));
    return result;
  }

  public static Result parseResult(String message) {
    JsonValue root = READER.parse(message);
    Result result = new Result();
    result.type = root.getString("type", "");
    return result;
  }

  // Outgoing ////////////////////////////////////////////////////

  public static String buildValidate(BlockDTO[] blocks, KingDTO king) {
    StringBuilder sb = new StringBuilder();
    sb.append("{\"type\":\"BUILD_VALIDATE\",\"blocks\":[");
    for (int i = 0; i < blocks.length; i++) {
      if (i > 0) {
        sb.append(',');
      }
      appendBlock(sb, blocks[i]);
    }
    sb.append("],\"king\":");
    appendKing(sb, king);
    sb.append('}');
    return sb.toString();
  }

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

  public static String verifyState() {
    return "{\"type\":\"VERIFY_STATE\"}";
  }

  // Message DTOs ////////////////////////////////////////////////

  public static class Start {
    public String type;
    public String matchId;
    public String opponent;
  }

  public static class AvailableComponents {
    public String type;
    public BlockDTO[] blocks;
    public KingDTO king;
    public RobotDTO[] robots;
  }

  public static class Timer {
    public String type;
    public int timeRemaining;
  }

  public static class OpponentStructure {
    public String type;
    public BlockDTO[] blocks;
    public KingDTO king;
  }

  public static class Result {
    public String type;
  }

  // Reads helpers //////////////////////////////////////////////////////

  private static BlockDTO[] readBlocks(JsonValue value) {
    if (value == null || value.isNull() || value.size == 0) {
      return new BlockDTO[0];
    }
    BlockDTO[] blocks = new BlockDTO[value.size];
    int index = 0;
    for (JsonValue child = value.child; child != null; child = child.next) {
      blocks[index++] =
        new BlockDTO(
          child.getString("type", ""),
          child.getInt("health", 0),
          child.getInt("mass", 0),
          child.getBoolean("alive", false),
          child.getInt("x", 0),
          child.getInt("y", 0),
          child.getInt("length", 0));
    }
    return blocks;
  }

  private static KingDTO readKing(JsonValue value) {
    if (value == null || value.isNull()) {
      return null;
    }
    return new KingDTO(
      value.getString("sprite", ""),
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
      robots[index++] =
        new RobotDTO(
          child.getString("sprite", ""),
          child.getInt("health", 0),
          child.getInt("mass", 0),
          child.getInt("cooldown", 0));
    }
    return robots;
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
    sb.append("{\"sprite\":\"")
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
