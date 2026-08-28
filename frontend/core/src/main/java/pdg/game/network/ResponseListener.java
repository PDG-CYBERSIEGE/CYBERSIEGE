package pdg.game.network;

/**
 * Receives the result of an asynchronous HTTP request.
 *
 * <p>Exactly one of {@link #success(String)}, {@link #failure(int, String)} or {@link
 * #error(String)} is expected to be called for each request.
 */
public interface ResponseListener {

  /**
   * Called when the HTTP request completed successfully.
   *
   * @param result the response body returned by the server
   */
  void success(String result);

  /**
   * Called when the server returned an HTTP error status.
   *
   * @param statusCode the HTTP status code
   * @param msg the response body returned by the server
   */
  void failure(int statusCode, String msg);

  /**
   * Called when the request could not be completed.
   *
   * @param msg a description of the error
   */
  void error(String msg);
}
