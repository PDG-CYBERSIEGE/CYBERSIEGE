package pdg.game.network;

import com.badlogic.gdx.Net;

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
