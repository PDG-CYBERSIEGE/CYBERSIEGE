package pdg.game.gwt;


import com.google.gwt.core.client.JavaScriptObject;
import pdg.game.network.websocket.GameListener;
import pdg.game.network.websocket.GameWebSocket;

/**
 * GWT implementation of the {@link GameWebSocket} interface.
 *
 * <p>This implementation uses the browser's native WebSocket API through
 * GWT JavaScript Native Interface (JSNI).</p>
 *
 * <p>The JWT is transmitted during the WebSocket handshake using the
 * {@code bearer-token-carrier} subprotocol expected by the server.</p>
 */
public class GwtGameWebSocket implements GameWebSocket {

  /**
   * Reference to the browser's native WebSocket object.
   */
  private JavaScriptObject socket;

  /**
   * Establishes a WebSocket connection to the matchmaking endpoint.
   *
   * <p>The HTTP(S) base URL is converted to a WebSocket URL using
   * {@code ws://} or {@code wss://}, and the {@code /match} endpoint
   * is appended.</p>
   *
   * @param baseUrl the HTTP(S) base URL of the server
   * @param jwt the JWT used to authenticate the WebSocket connection
   * @param gameListener the listener that receives WebSocket events
   */
  @Override
  public void connect(String baseUrl, String jwt, GameListener gameListener) {
    String url = baseUrl.replace("http://", "ws://")
                .replace("https://", "wss://")
                + "/match";
    socket = createSocket(url, jwt);
    setHandlers(socket, gameListener);
  }

  /**
   * Sends a text message through the WebSocket connection.
   *
   * @param message the message to send
   */
  @Override
  public void send(String message) {
    sendMessage(socket, message);
  }

  /**
   * Closes the WebSocket connection.
   */
  @Override
  public void disconnect() {
    closeSocket(socket);
  }

  private native JavaScriptObject createSocket (String url, String jwt) /*-{
    var protocol = encodeURIComponent(
      "quarkus-http-upgrade#Authorization#Bearer " + jwt
    );

    return new $wnd.WebSocket(
      url,
      ["bearer-token-carrier", protocol]
    );
  }-*/;

  private native void setHandlers (JavaScriptObject socket, GameListener listener) /*-{
    socket.onopen = function() {
      listener.@pdg.game.network.websocket.GameListener::onConnected()();
    };

    socket.onmessage = function(event) {
      listener.@pdg.game.network.websocket.GameListener::onMessage(Ljava/lang/String;)(event.data);
    };

    socket.onclose = function() {
      listener.@pdg.game.network.websocket.GameListener::onDisconnected()();
    };

    socket.onerror = function() {
      listener.@pdg.game.network.websocket.GameListener::onError(Ljava/lang/String;)("WebSocket error");
    };
  }-*/;

  private native void sendMessage(JavaScriptObject socket, String message) /*-{
    socket.send(message);
  }-*/;

  private native void closeSocket(JavaScriptObject socket) /*-{
    socket.close();
  }-*/;
}
