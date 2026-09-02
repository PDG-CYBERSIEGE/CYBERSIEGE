package com.pdg.match;

import com.pdg.game.DTO.BlockDTO;
import com.pdg.game.DTO.KingDTO;
import com.pdg.game.DTO.RobotDTO;
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

  void receiveMsg(PlayerConnection player, String msg) {
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

      default:
        player.sendToPlayer(GameMessages.invalid());
        break;
    }
  }

  public void sendStart(PlayerConnection player, String matchId, String opponentName) {
    player.sendToPlayer(GameMessages.start(matchId, opponentName));
  }

  public void sendAvailableComponents(
      PlayerConnection player, BlockDTO[] blocks, KingDTO king, RobotDTO[] robots) {
    player.sendToPlayer(GameMessages.availableComponents(blocks, king, robots));
  }

  public void sendTimer(PlayerConnection player, int timeInSecRemaining) {
    player.sendToPlayer(GameMessages.timer(timeInSecRemaining));
  }

  public void sendOpponentStructure(PlayerConnection player, BlockDTO[] blocks, KingDTO king) {
    player.sendToPlayer(GameMessages.opponentStructure(blocks, king));
  }

  public void sendWinner(PlayerConnection player) {
    player.sendToPlayer(GameMessages.winner());
  }

  public void sendLoser(PlayerConnection player) {
    player.sendToPlayer(GameMessages.loser());
  }
}
