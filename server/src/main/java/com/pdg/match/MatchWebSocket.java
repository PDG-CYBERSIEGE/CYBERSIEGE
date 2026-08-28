package com.pdg.match;

import io.quarkus.security.Authenticated;
import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@WebSocket(path = "/match")
@Authenticated
public class MatchWebSocket {

  private static final UserData.TypedKey<PlayerConnection> PLAYER_CONNECTION =
      new UserData.TypedKey<>("playerConnection");

  @Inject JsonWebToken jwt;


  @Inject MatchmakingService matchmakingService;

  @OnOpen
  public void onOpen(WebSocketConnection connection) {
    Long playerId = Long.valueOf(jwt.getSubject());
    PlayerConnection playerConnection = matchmakingService.findMatch(playerId, connection);
    connection.userData().put(PLAYER_CONNECTION, playerConnection);
  }

  @OnClose
  public void onClose(WebSocketConnection connection) {
    PlayerConnection playerConnection = connection.userData().get(PLAYER_CONNECTION);
    playerConnection.close();
    matchmakingService.removePlayer(playerConnection);
  }

  @OnTextMessage
  public void onMessage(WebSocketConnection connection, String msg) {
    PlayerConnection playerConnection = connection.userData().get(PLAYER_CONNECTION);
    playerConnection.sendToMatch(msg);
  }
}
