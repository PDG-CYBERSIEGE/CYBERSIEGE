package pdg.game.network.http;

import com.badlogic.gdx.Net;

/**
 * Adapts LibGDX HTTP responses to the application's {@link ResponseListener}.
 *
 * <p>Successful HTTP responses (2xx) are forwarded to {@link ResponseListener#success(String)}.
 * HTTP error responses are forwarded to {@link ResponseListener#failure(int, String)}. Network
 * errors and cancelled requests are forwarded to {@link ResponseListener#error(String)}.
 */
public class HttpResponseListener implements Net.HttpResponseListener {

  private final ResponseListener responseListener;

  public HttpResponseListener(ResponseListener responseListener) {
    this.responseListener = responseListener;
  }

  @Override
  public void handleHttpResponse(Net.HttpResponse response) {
    int status = response.getStatus().getStatusCode();
    String result = response.getResultAsString();

    if (status >= 200 && status <= 299) {
      responseListener.success(result);
    } else {
      responseListener.failure(status, result);
    }
  }

  @Override
  public void failed(Throwable t) {
    responseListener.error(t.getMessage());
  }

  @Override
  public void cancelled() {
    responseListener.error("Cancelled");
  }
}
