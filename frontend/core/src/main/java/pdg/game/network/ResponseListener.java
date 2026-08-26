package pdg.game.network;

public interface ResponseListener {
  void success(String result);

  void failure(int statusCode, String msg);

  void error(String msg);
}
