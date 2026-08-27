package pdg.game.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;

public class HttpClient {

  private final String BASE_URL;
  private String token;

  public HttpClient(String base_url) {
    BASE_URL = base_url;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void get(String relativePath, Net.HttpResponseListener responseListener) {
    Net.HttpRequest httpRequest = createBasicRequest(relativePath);
    httpRequest.setMethod(Net.HttpMethods.GET);
    Gdx.net.sendHttpRequest(httpRequest, responseListener);
  }

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
