package pdg.game.gwt;


import com.google.gwt.core.client.JavaScriptObject;
import pdg.game.network.websocket.GameListener;
import pdg.game.network.websocket.GameWebSocket;

public class GwtGameWebSocket implements GameWebSocket {

  private JavaScriptObject socket;

  @Override
  public void connect(String baseUrl, String jwt, GameListener gameListener) {
    String url = baseUrl.replace("http://", "ws://")
                .replace("https://", "wss://")
                + "/match";
    socket = createSocket(url, jwt);
    setHandlers(socket, gameListener);
  }

  @Override
  public void send(String message) {
    sendMessage(socket, message);
  }

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
