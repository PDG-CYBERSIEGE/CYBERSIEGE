package com.pdg.match;

import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Service responsible for matchmaking players.
 *
 * <p>The service maintains the current match waiting for a second player. When a first player
 * connects, a new {@link Match} is created and stored as the current match. When a second player
 * connects, they are added to the same match and the match is removed from the waiting state.
 *
 * <p>The service is application-scoped, meaning that a single instance manages the matchmaking
 * state for the application.
 */
@ApplicationScoped
public class MatchmakingService {

  /** Match currently waiting for a second player. */
  private Match currentMatch;

  /**
   * Finds a match for a player.
   *
   * <p>If no match is currently waiting, a new {@link Match} is created and the player is added to
   * it as the first player. If a match is already waiting, the player is added to that match as the
   * second player and the match is removed from the waiting state.
   *
   * @param playerId the unique identifier of the player
   * @param connection the player's WebSocket connection
   * @return the {@link PlayerConnection} associated with the player
   */
  public synchronized PlayerConnection findMatch(Long playerId, WebSocketConnection connection) {

    Match connectionMatch;

    if (currentMatch == null) {
      // First player
      currentMatch = new Match();
      connectionMatch = currentMatch;
    } else {
      // Second player
      connectionMatch = currentMatch;
      currentMatch = null;
    }

    PlayerConnection playerConnection = new PlayerConnection(connection, playerId, connectionMatch);
    connectionMatch.addPlayer(playerConnection);

    return playerConnection;
  }

  /**
   * Removes a player from the matchmaking system.
   *
   * <p>If the player's match is currently the match waiting for a second player, the waiting match
   * is cleared.
   *
   * @param player the player connection to remove
   */
  public synchronized void removePlayer(PlayerConnection player) {
    if (currentMatch == player.getMatch()) {
      currentMatch = null;
    }
  }
}
