package com.pdg.match;

import io.quarkus.websockets.next.WebSocketConnection;

public class PlayerConnection {

  private final WebSocketConnection connection;
  private final Long playerId;
  private final Match match;

  public PlayerConnection(WebSocketConnection connection, Long playerId, Match match) {
    this.connection = connection;
    this.playerId = playerId;
    this.match = match;
  }

  public Long playerId() {
    return playerId;
  }

  public Match getMatch() {
    return match;
  }

  public void close() {
    match.removePlayer(this);
  }

  public void sendToPlayer(String msg) {
    connection.sendTextAndAwait(msg);
  }

  public void sendToMatch(String message) {
    match.receiveMsg(this, message);
  }
}
