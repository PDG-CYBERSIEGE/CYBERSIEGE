package pdg.game.network.websocket;

/**
 * Provides a platform-independent interface for WebSocket communication.
 *
 * <p>Platform-specific implementations are responsible for establishing, maintaining, and closing
 * the WebSocket connection.
 */
public interface GameWebSocket {

  /**
   * Establishes a WebSocket connection to the server.
   *
   * @param baseUrl the base URL of the server
   * @param jwt the JWT used to authenticate the connection
   * @param gameListener the listener that receives connection events, messages, and errors
   */
  void connect(String baseUrl, String jwt, GameListener gameListener);

  /**
   * Sends a message to the server through the WebSocket connection.
   *
   * @param message the message to send
   */
  void send(String message);

  /** Closes the WebSocket connection. */
  void disconnect();
}
