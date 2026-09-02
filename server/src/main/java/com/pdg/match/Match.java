package com.pdg.match;

import com.pdg.game.DTO.BlockDTO;
import com.pdg.game.DTO.KingDTO;
import com.pdg.game.DTO.RobotDTO;
import com.pdg.game.DTO.TeamDTO;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a match between two players.
 *
 * <p>A match can contain up to two {@link PlayerConnection} instances. When the second player
 * joins, a unique match identifier is generated and sent to both players.
 */
public class Match {

  private PlayerConnection player1;
  private PlayerConnection player2;
  private long matchId = 0;

  /** Global counter used to generate unique match identifiers. */
  public static AtomicLong nextMatchId = new AtomicLong(1);

  /**
   * Adds a player to the match.
   *
   * <p>The first player is stored as player 1. The second player is stored as player 2. Once two
   * players are present, a unique match ID is generated and sent to both players.
   *
   * @param player the player connection to add to the match
   */
  public void addPlayer(PlayerConnection player) {
    if (player1 == null) {
      player1 = player;
    } else {
      player2 = player;
      matchId = nextMatchId.getAndIncrement();
      sendStart(player1, String.valueOf(matchId), String.valueOf(player2.playerId()));
      sendStart(player2, String.valueOf(matchId), String.valueOf(player1.playerId()));
    }
  }

  /**
   * Removes a player from the match.
   *
   * <p>TODO
   *
   * @param playerConnection the player connection to remove
   */
  void removePlayer(PlayerConnection playerConnection) {}

  /**
   * Processes an incoming game message.
   *
   * @param player player who sent the message
   * @param msg received message
   */
  void receiveMsg(PlayerConnection player, String msg) {

    if (msg.equals("Hello !")) {
      player.sendToPlayer("Hello Back !");
    }

    switch (GameMessages.type(msg)) {
      case "BUILD_VALIDATE":
        // todo
        break;

      case "FIRE":
        // todo
        break;

      case "VERIFY_STATE":
        // todo
        break;

      case "TEAM":
        // todo
        break;

      default:
        player.sendToPlayer(GameMessages.invalid());
        break;
    }
  }

  /**
   * Sends the start message to a player.
   *
   * @param player target player
   * @param matchId match identifier
   * @param opponentName opponent name
   */
  public void sendStart(PlayerConnection player, String matchId, String opponentName) {
    player.sendToPlayer(GameMessages.start(matchId, opponentName));
  }

  /**
   * Sends the available components to a player.
   *
   * @param player target player
   * @param blocks available blocks
   * @param king player's king
   * @param robots available robots
   */
  public void sendAvailableComponents(
      PlayerConnection player, BlockDTO[] blocks, KingDTO king, RobotDTO[] robots) {
    player.sendToPlayer(GameMessages.availableComponents(blocks, king, robots));
  }

  /**
   * Sends a timer update to a player.
   *
   * @param player target player
   * @param timeInSecRemaining remaining time in seconds
   */
  public void sendTimer(PlayerConnection player, int timeInSecRemaining) {
    player.sendToPlayer(GameMessages.timer(timeInSecRemaining));
  }

  /**
   * Sends the opponent's structure to a player.
   *
   * @param player target player
   * @param blocks opponent structure blocks
   * @param king opponent's king
   */
  public void sendOpponentStructure(PlayerConnection player, BlockDTO[] blocks, KingDTO king) {
    player.sendToPlayer(GameMessages.opponentStructure(blocks, king));
  }

  /**
   * Sends a winner message to a player.
   *
   * @param player target player
   */
  public void sendWinner(PlayerConnection player) {
    player.sendToPlayer(GameMessages.winner());
  }

  /**
   * Sends a loser message to a player.
   *
   * @param player target player
   */
  public void sendLoser(PlayerConnection player) {
    player.sendToPlayer(GameMessages.loser());
  }

  /**
   * Sends a team message to a player.
   *
   * @param player target player
   * @param team team to send
   */
  public void sendTeam(PlayerConnection player, TeamDTO team) {
    player.sendToPlayer(GameMessages.team(team));
  }
}
