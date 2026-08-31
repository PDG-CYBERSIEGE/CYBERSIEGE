package pdg.game.network.websocket;

public interface GameListener {

  void onConnected();

  void onMessage(String message);

  void onDisconnected();

  void onError(String msg);
}
