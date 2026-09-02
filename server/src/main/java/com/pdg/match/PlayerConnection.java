package com.pdg.match;

import io.quarkus.websockets.next.WebSocketConnection;

/**
 * Represents the connection between a player and the matchmaking WebSocket server.
 *
 * <p>A {@code PlayerConnection} associates a player ID and a WebSocket connection with a {@link
 * Match}.
 */
public class PlayerConnection {

  private final WebSocketConnection connection;
  private final Long playerId;
  private final Match match;

  /**
   * Creates a player connection.
   *
   * @param connection the player's WebSocket connection
   * @param playerId the unique identifier of the player
   * @param match the match associated with the player
   */
  public PlayerConnection(WebSocketConnection connection, Long playerId, Match match) {
    this.connection = connection;
    this.playerId = playerId;
    this.match = match;
  }

  /**
   * Returns the player's identifier.
   *
   * @return the player ID
   */
  public Long playerId() {
    return playerId;
  }

  /**
   * Returns the match associated with this player.
   *
   * @return the player's match
   */
  public Match getMatch() {
    return match;
  }

  /**
   * Closes the player's connection.
   *
   * <p>The player is removed from the associated match.
   */
  public void close() {
    match.removePlayer(this);
  }

  /**
   * Sends a message directly to this player through the WebSocket.
   *
   * @param msg the message to send
   */
  public void sendToPlayer(String msg) {
    connection.sendTextAndAwait(msg);
  }

  /**
   * Sends a message to the associated match for processing.
   *
   * @param message the message received from the player
   */
  public void sendToMatch(String message) {
    match.receiveMsg(this, message);
  }
}
