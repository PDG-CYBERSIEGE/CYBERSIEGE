package pdg.game.network.websocket;


import com.google.gwt.core.client.JavaScriptObject;

public class GameWebSocket {

  private final JavaScriptObject socket;

  public GameWebSocket(String baseUrl, String jwt, GameListener gameListener) {
    String url = baseUrl.replace("http://", "ws://")
                        .replace("https://", "wss://")
                        + "/match";
    socket = createSocket(url, jwt);
    setHandlers(socket, gameListener);
  }

  public void send(String message) {
    sendMessage(socket, message);
  }

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
