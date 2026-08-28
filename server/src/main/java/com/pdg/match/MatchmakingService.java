package com.pdg.match;

import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MatchmakingService {

  private Match currentMatch;

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

  public synchronized void removePlayer(PlayerConnection player) {
    if (currentMatch == player.getMatch()) {
      currentMatch = null;
    }
  }
}
