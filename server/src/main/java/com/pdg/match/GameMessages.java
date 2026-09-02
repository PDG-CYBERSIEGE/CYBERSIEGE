package com.pdg.match;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdg.game.DTO.BlockDTO;
import com.pdg.game.DTO.KingDTO;
import com.pdg.game.DTO.RobotDTO;
import com.pdg.game.DTO.TeamDTO;

/** Utility class for creating and parsing game WebSocket messages. */
public final class GameMessages {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static String json(Object message) {
    try {
      return MAPPER.writeValueAsString(message);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize game message", e);
    }
  }

  private static <T> T read(String message, Class<T> type) {
    try {
      return MAPPER.readValue(message, type);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Invalid game message", e);
    }
  }

  // Incoming ////////////////////////////////////////////////////

  /**
   * Extracts the type from a JSON message.
   *
   * @param message JSON message
   * @return message type, or an empty string if it is invalid
   */
  public static String type(String message) {
    try {
      return MAPPER.readTree(message).get("type").asText();
    } catch (JsonProcessingException | NullPointerException e) {
      return "";
    }
  }

  /**
   * Parses a fire message.
   *
   * @param message JSON message
   * @return parsed fire message
   */
  public static Fire parseFire(String message) {
    return read(message, Fire.class);
  }

  /**
   * Parses a build validation message.
   *
   * @param message JSON message
   * @return parsed build validation message
   */
  public static BuildValidate parseBuildValidate(String message) {
    return read(message, BuildValidate.class);
  }

  /**
   * Parses a team message.
   *
   * @param message JSON message
   * @return parsed team message
   */
  public static Team parseTeam(String message) {
    return read(message, Team.class);
  }

  /** Represents a fire action. */
  public record Fire(String type, int power, float angle, int robot) {}

  /** Represents a build validation action. */
  public record BuildValidate(String type, BlockDTO[] blocks, KingDTO king) {}

  /** Represents a team message. */
  public record Team(String type, TeamDTO team) {}

  // Outcoming ////////////////////////////////////////////////////

  /**
   * Creates a match start message.
   *
   * @param matchId match identifier
   * @param opponentName opponent's name
   * @return JSON message
   */
  public static String start(String matchId, String opponentName) {
    return json(new StartMessage("START", matchId, opponentName));
  }

  /**
   * Creates a message containing the components available to a player.
   *
   * @param blocks available blocks
   * @param king player's king
   * @param robots available robots
   * @return JSON message
   */
  public static String availableComponents(BlockDTO[] blocks, KingDTO king, RobotDTO[] robots) {
    return json(new AvailableComponentsMessage("AVAILABLE_COMPONENTS", blocks, king, robots));
  }

  /**
   * Creates a timer update message.
   *
   * @param timeInSecRemaining remaining time in seconds
   * @return JSON message
   */
  public static String timer(int timeInSecRemaining) {
    return json(new TimerMessage("TIMER", timeInSecRemaining));
  }

  /**
   * Creates an opponent structure message.
   *
   * @param blocks opponent structure blocks
   * @param king opponent's king
   * @return JSON message
   */
  public static String opponentStructure(BlockDTO[] blocks, KingDTO king) {
    return json(new OpponentStructureMessage("OPPONENT_STRUCTURE", blocks, king));
  }

  /**
   * Creates a winner message.
   *
   * @return JSON message
   */
  public static String winner() {
    return json(new ResultMessage("WINNER"));
  }

  /**
   * Creates a loser message.
   *
   * @return JSON message
   */
  public static String loser() {
    return json(new ResultMessage("LOSER"));
  }

  /**
   * Creates an invalid message.
   *
   * @return JSON message
   */
  public static String invalid() {
    return json(new ResultMessage("INVALID"));
  }

  /**
   * Creates a message containing a team.
   *
   * @param team team to send
   * @return JSON message
   */
  public static String team(TeamDTO team) {
    return json(new TeamMessage("TEAM", team));
  }

  private record StartMessage(String type, String matchId, String opponent) {}

  private record AvailableComponentsMessage(
      String type, BlockDTO[] blocks, KingDTO king, RobotDTO[] robots) {}

  private record TimerMessage(String type, int timeRemaining) {}

  private record OpponentStructureMessage(String type, BlockDTO[] blocks, KingDTO king) {}

  private record ResultMessage(String type) {}

  private record TeamMessage(String type, TeamDTO team) {}
}
