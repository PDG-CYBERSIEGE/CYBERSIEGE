package pdg.game.network.websocket;

/**
 * Listener for WebSocket connection events.
 *
 * <p>Implementations of this interface receive notifications about the WebSocket connection state,
 * incoming messages, and errors.
 */
public interface GameListener {

  /** Called when the WebSocket connection has been successfully established. */
  void onConnected();

  /**
   * Called when a message is received from the server.
   *
   * @param message the message received from the server
   */
  void onMessage(String message);

  /** Called when the WebSocket connection has been closed. */
  void onDisconnected();

  /**
   * Called when an error occurs during WebSocket communication.
   *
   * @param msg a description of the error
   */
  void onError(String msg);
}
