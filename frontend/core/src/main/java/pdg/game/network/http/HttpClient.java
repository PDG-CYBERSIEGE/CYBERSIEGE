package pdg.game.network.http;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;

/**
 * Provides low-level HTTP communication with the game server.
 *
 * <p>If an authentication token is set, it is automatically included in requests using the {@code
 * Authorization: Bearer <token>} header.
 */
public class HttpClient {

  private final String BASE_URL;
  private String token;

  public HttpClient(String base_url) {
    BASE_URL = base_url;
  }

  /**
   * Sets the authentication token used for subsequent requests.
   *
   * @param token the authentication token
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * Sends an asynchronous GET request.
   *
   * @param relativePath the server endpoint path
   * @param responseListener the listener receiving the HTTP response
   */
  public void get(String relativePath, Net.HttpResponseListener responseListener) {
    Net.HttpRequest httpRequest = createBasicRequest(relativePath);
    httpRequest.setMethod(Net.HttpMethods.GET);
    Gdx.net.sendHttpRequest(httpRequest, responseListener);
  }

  /**
   * Sends an asynchronous POST request with a JSON body.
   *
   * @param relativePath the server endpoint path
   * @param body the JSON request body
   * @param responseListener the listener receiving the HTTP response
   */
  public void post(String relativePath, String body, Net.HttpResponseListener responseListener) {
    Net.HttpRequest httpRequest = createBasicRequest(relativePath);
    httpRequest.setMethod(Net.HttpMethods.POST);
    httpRequest.setHeader("Content-Type", "application/json");
    httpRequest.setContent(body);
    Gdx.net.sendHttpRequest(httpRequest, responseListener);
  }

  private Net.HttpRequest createBasicRequest(String relativePath) {
    HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    Net.HttpRequest httpRequest = requestBuilder.newRequest().url(BASE_URL + relativePath).build();

    if (token != null) {
      httpRequest.setHeader("Authorization", "Bearer " + token);
    }

    return httpRequest;
  }
}
