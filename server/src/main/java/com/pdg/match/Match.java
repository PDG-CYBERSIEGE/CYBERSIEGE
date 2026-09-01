package com.pdg.match;

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
      sendMsgToPlayer(player1, "MATCH_ID:" + matchId);
      sendMsgToPlayer(player2, "MATCH_ID:" + matchId);
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
   * Processes a message received from a player.
   *
   * @param player the player who sent the message
   * @param msg the message received
   */
  void receiveMsg(PlayerConnection player, String msg) {
    if (msg.equals("Hello !")) {
      sendMsgToPlayer(player, "Hello Back !");
    }
  }

  /**
   * Sends a message directly to a player.
   *
   * @param player the player who should receive the message
   * @param msg the message to send
   */
  public void sendMsgToPlayer(PlayerConnection player, String msg) {
    player.sendToPlayer(msg);
  }
}
