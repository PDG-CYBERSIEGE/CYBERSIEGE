package pdg.game.network.websocket;

public interface GameWebSocket {
  void connect(String baseUrl, String jwt, GameListener gameListener);
  void send(String message);
  void disconnect();
}
