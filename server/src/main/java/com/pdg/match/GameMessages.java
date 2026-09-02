package com.pdg.match;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdg.game.DTO.BlockDTO;
import com.pdg.game.DTO.KingDTO;
import com.pdg.game.DTO.RobotDTO;

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

  public static String type(String message) {
    try {
      return MAPPER.readTree(message).get("type").asText();
    } catch (JsonProcessingException | NullPointerException e) {
      return "";
    }
  }

  public static Fire parseFire(String message) {
    return read(message, Fire.class);
  }

  public static BuildValidate parseBuildValidate(String message) {
    return read(message, BuildValidate.class);
  }

  public record Fire(String type, int power, float angle, int robot) {}

  public record BuildValidate(String type, BlockDTO[] blocks, KingDTO king) {}

  // Outcoming ////////////////////////////////////////////////////

  public static String start(String matchId, String opponentName) {
    return json(new StartMessage("START", matchId, opponentName));
  }

  public static String availableComponents(BlockDTO[] blocks, KingDTO king, RobotDTO[] robots) {
    return json(new AvailableComponentsMessage("AVAILABLE_COMPONENTS", blocks, king, robots));
  }

  public static String timer(int timeInSecRemaining) {
    return json(new TimerMessage("TIMER", timeInSecRemaining));
  }

  public static String opponentStructure(BlockDTO[] blocks, KingDTO king) {
    return json(new OpponentStructureMessage("OPPONENT_STRUCTURE", blocks, king));
  }

  public static String winner() {
    return json(new ResultMessage("WINNER"));
  }

  public static String loser() {
    return json(new ResultMessage("LOSER"));
  }

  public static String invalid() {
    return json(new ResultMessage("INVALID"));
  }

  private record StartMessage(String type, String matchId, String opponent) {}

  private record AvailableComponentsMessage(
      String type, BlockDTO[] blocks, KingDTO king, RobotDTO[] robots) {}

  private record TimerMessage(String type, int timeRemaining) {}

  private record OpponentStructureMessage(String type, BlockDTO[] blocks, KingDTO king) {}

  private record ResultMessage(String type) {}
}
