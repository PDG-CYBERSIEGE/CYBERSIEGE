package pdg.game.network;

public class AuthClient {

  private final HttpClient httpClient;

  public AuthClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

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

  public void login(String username, String password, ResponseListener responseListener) {
    String body = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
    httpClient.post("/auth/login", body, new HttpResponseListener(responseListener));
  }

  public void setToken(String token) {
    httpClient.setToken(token);
  }

  public void getUsername(ResponseListener responseListener) {
    httpClient.get("/users/me", new HttpResponseListener(responseListener));
  }
}
