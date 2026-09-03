package pdg.game.gwt;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import pdg.game.network.websocket.GameListener;

public class GwtGameWebSocketTest extends GWTTestCase {

  private static final String BASE_URL = "http://localhost:18080";
  private static final String JWT = "fake-token";

  @Override
  public String getModuleName() {
    return "pdg.game.GdxDefinition";
  }

  @Override
  protected void gwtSetUp() {
    installMockWebSocket();
  }

  public void testConnect() {
    delayTestFinish(5000);
    GwtGameWebSocket webSocket = new GwtGameWebSocket();
    TestGameListener listener = new TestGameListener() {
      @Override
      public void onConnected() {
        finishTest();
      }

      @Override
      public void onError(String error) {
        fail("WebSocket error: " + error);
      }
    };
    webSocket.connect(BASE_URL, JWT, listener);
  }

  public void testReceiveMessage() {
    delayTestFinish(5000);
    GwtGameWebSocket webSocket = new GwtGameWebSocket();
    TestGameListener listener = new TestGameListener() {
      @Override
      public void onMessage(String message) {
        assertEquals("CONNECTED", message);
        finishTest();
      }

      @Override
      public void onError(String error) {
        fail("WebSocket error: " + error);
      }
    };
    webSocket.connect(BASE_URL, JWT, listener);
  }

  public void testSendAndReceiveMessage() {
    delayTestFinish(5000);
    GwtGameWebSocket webSocket = new GwtGameWebSocket();
    TestGameListener listener = new TestGameListener() {
      @Override
      public void onConnected() {
        new Timer() {
          @Override
          public void run() {
            webSocket.send("Hello !");
          }
        }.schedule(100);
      }

      @Override
      public void onMessage(String message) {
        if ("Hello Back !".equals(message)) {
          finishTest();
        }
      }

      @Override
      public void onError(String error) {
        fail("WebSocket error: " + error);
      }
    };
    webSocket.connect(BASE_URL, JWT, listener);
  }

  public void testDisconnect() {
    delayTestFinish(5000);
    GwtGameWebSocket webSocket = new GwtGameWebSocket();
    TestGameListener listener = new TestGameListener() {
      @Override
      public void onConnected() {
        webSocket.disconnect();
      }

      @Override
      public void onDisconnected() {
        finishTest();
      }

      @Override
      public void onError(String error) {
        fail("WebSocket error: " + error);
      }
    };
    webSocket.connect(BASE_URL, JWT, listener);
  }

  private native void installMockWebSocket() /*-{

    var MockWebSocket = function(url, protocols) {

      this.url = url;
      this.protocols = protocols;

      this.onopen = null;
      this.onmessage = null;
      this.onclose = null;
      this.onerror = null;

      var self = this;

      setTimeout(function() {

        if (self.onopen) {
          self.onopen();
        }

        setTimeout(function() {

          if (self.onmessage) {
            self.onmessage({
              data: "CONNECTED"
            });
          }

        }, 10);

      }, 10);
    };

    MockWebSocket.prototype.send = function(message) {

      var self = this;

      setTimeout(function() {

        if (message === "Hello !") {

          if (self.onmessage) {
            self.onmessage({
              data: "Hello Back !"
            });
          }

        }

      }, 10);
    };

    MockWebSocket.prototype.close = function() {

      var self = this;

      setTimeout(function() {

        if (self.onclose) {
          self.onclose();
        }

      }, 10);
    };

    $wnd.WebSocket = MockWebSocket;

  }-*/;

  private static class TestGameListener implements GameListener {

    @Override
    public void onConnected() {
    }

    @Override
    public void onMessage(String message) {
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onError(String error) {
    }
  }
}
