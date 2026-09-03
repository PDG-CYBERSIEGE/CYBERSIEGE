package com.pdg.match;

import com.pdg.user.User;
import com.pdg.user.UserRepository;
import io.quarkus.security.Authenticated;
import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * WebSocket endpoint responsible for handling player connections to the matchmaking system.
 *
 * <p>The endpoint is available at {@code /match} and requires an authenticated user. The player's
 * ID is retrieved from the JWT subject when the connection is opened.
 *
 * <p>Each WebSocket connection is associated with a {@link PlayerConnection}, which is stored in
 * the connection's user data.
 */
@WebSocket(path = "/match")
@Authenticated
public class MatchWebSocket {

  private static final UserData.TypedKey<PlayerConnection> PLAYER_CONNECTION =
      new UserData.TypedKey<>("playerConnection");

  @Inject JsonWebToken jwt;

  @Inject MatchmakingService matchmakingService;

  /**
   * Handles the opening of a WebSocket connection.
   *
   * <p>The authenticated player's ID is extracted from the JWT. A {@link PlayerConnection} is then
   * created or retrieved through the {@link MatchmakingService} and associated with the WebSocket
   * connection.
   *
   * @param connection the newly opened WebSocket connection
   */
  @OnOpen
  public void onOpen(WebSocketConnection connection) {
    Long playerId = Long.valueOf(jwt.getSubject());
    UserRepository userRepository = new UserRepository();
    String playerName = userRepository.findById(playerId).username;
    PlayerConnection playerConnection = matchmakingService.findMatch(playerName, playerId, connection);
    connection.userData().put(PLAYER_CONNECTION, playerConnection);
  }

  /**
   * Handles the closing of a WebSocket connection.
   *
   * <p>The associated {@link PlayerConnection} is closed and removed from the matchmaking service.
   *
   * @param connection the WebSocket connection being closed
   */
  @OnClose
  public void onClose(WebSocketConnection connection) {
    PlayerConnection playerConnection = connection.userData().get(PLAYER_CONNECTION);
    playerConnection.close();
    matchmakingService.removePlayer(playerConnection);
  }

  /**
   * Handles a text message received through the WebSocket.
   *
   * <p>The message is forwarded to the match associated with the connected player.
   *
   * @param connection the WebSocket connection that sent the message
   * @param msg the received message
   */
  @OnTextMessage
  public void onMessage(WebSocketConnection connection, String msg) {
    PlayerConnection playerConnection = connection.userData().get(PLAYER_CONNECTION);
    playerConnection.sendToMatch(msg);
  }
}
