package pdg.game.network;

/**
 * Provides the client-side authentication API.
 *
 * <p>This class handles authentication requests and delegates their results
 * to a {@link ResponseListener}.
 */
public class AuthClient {

  private final HttpClient httpClient;

  public AuthClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }


  /**
   * Registers a new user.
   *
   * @param email the user's email address
   * @param username the user's username
   * @param password the user's password
   * @param responseListener listener receiving the request result
   */
  public void register(
      String email, String username, String password, ResponseListener responseListener) {
    String body =
        "{\"email\":\""
            + email
            + "\",\"username\":\""
            + username
            + "\",\"password\":\""
            + password
            + "\"}";
    httpClient.post("/auth/register", body, new HttpResponseListener(responseListener));
  }

  /**
   * Authenticates a user.
   *
   * @param username the user's username
   * @param password the user's password
   * @param responseListener listener receiving the request result
   */
  public void login(String username, String password, ResponseListener responseListener) {
    String body = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
    httpClient.post("/auth/login", body, new HttpResponseListener(responseListener));
  }

  /**
   * Sets the authentication token used for subsequent requests.
   *
   * @param token the authentication token
   */
  public void setToken(String token) {
    httpClient.setToken(token);
  }

  /**
   * Retrieves the username of the currently authenticated user.
   *
   * <p>The authentication token previously set with {@link #setToken(String)}
   * is automatically used for this request.
   *
   * @param responseListener listener receiving the request result
   */
  public void getUsername(ResponseListener responseListener) {
    httpClient.get("/users/me", new HttpResponseListener(responseListener));
  }
}
