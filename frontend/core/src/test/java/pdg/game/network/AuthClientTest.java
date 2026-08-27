package pdg.game.network;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AuthClientTest {

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    new HeadlessApplication(new ApplicationAdapter() {});
    wireMockServer = new WireMockServer(options().dynamicPort());
    wireMockServer.start();
  }

  @AfterAll
  static void stop() {
    wireMockServer.stop();
  }

  private AuthClient createAuthClient() {
    HttpClient httpClient = new HttpClient("http://localhost:" + wireMockServer.port());
    return new AuthClient(httpClient);
  }

  private static class TestResponseListener implements ResponseListener {
    CountDownLatch latch = new CountDownLatch(1);
    int successCalls;
    int failureCalls;
    int errorCalls;
    int statusCode;
    String result;
    String error;

    @Override
    public void success(String result) {
      successCalls++;
      this.result = result;
      latch.countDown();
    }

    @Override
    public void failure(int status, String result) {
      failureCalls++;
      statusCode = status;
      this.result = result;
      latch.countDown();
    }

    @Override
    public void error(String message) {
      errorCalls++;
      error = message;
      latch.countDown();
    }
  }

  @Test
  void registerSuccess() throws InterruptedException {
    AuthClient authClient = createAuthClient();
    TestResponseListener listener = new TestResponseListener();

    wireMockServer.stubFor(
        post(urlEqualTo("/auth/register"))
            .willReturn(aResponse().withStatus(200).withBody("Registration successful")));

    authClient.register("usr@test.com", "usr", "psw12345", listener);

    assertTrue(listener.latch.await(5, TimeUnit.SECONDS));
    assertEquals(1, listener.successCalls);
    assertEquals("Registration successful", listener.result);

    wireMockServer.verify(
        postRequestedFor(urlEqualTo("/auth/register"))
            .withRequestBody(
                equalToJson(
                    """
            {
              "email": "usr@test.com",
              "username": "usr",
              "password": "psw12345"
            }
            """)));
  }

  @Test
  void registerFailure() throws InterruptedException {
    AuthClient authClient = createAuthClient();
    TestResponseListener listener = new TestResponseListener();

    wireMockServer.stubFor(
        post(urlEqualTo("/auth/register"))
            .willReturn(aResponse().withStatus(409).withBody("Email already in use")));

    authClient.register("usr@test.com", "usr", "psw12345", listener);

    assertTrue(listener.latch.await(5, TimeUnit.SECONDS));
    assertEquals(1, listener.failureCalls);
    assertEquals(409, listener.statusCode);
    assertEquals("Email already in use", listener.result);
  }

  @Test
  void registerError() throws InterruptedException {
    HttpClient httpClient = new HttpClient("http://localhost:1");
    AuthClient authClient = new AuthClient(httpClient);
    TestResponseListener listener = new TestResponseListener();

    authClient.register("usr@test.com", "usr", "psw12345", listener);

    assertTrue(listener.latch.await(5, TimeUnit.SECONDS));
    assertEquals(1, listener.errorCalls);
    assertNotNull(listener.error);
  }

  @Test
  void loginSuccess() throws InterruptedException {
    AuthClient authClient = createAuthClient();
    TestResponseListener listener = new TestResponseListener();

    wireMockServer.stubFor(
        post(urlEqualTo("/auth/login"))
            .willReturn(aResponse().withStatus(200).withBody("blabla_jwt")));

    authClient.login("usr", "psw12345", listener);

    assertTrue(listener.latch.await(5, TimeUnit.SECONDS));
    assertEquals(1, listener.successCalls);
    assertEquals("blabla_jwt", listener.result);

    wireMockServer.verify(
        postRequestedFor(urlEqualTo("/auth/login"))
            .withRequestBody(
                equalToJson(
                    """
            {
              "username": "usr",
              "password": "psw12345"
            }
            """)));
  }

  @Test
  void loginFailure() throws InterruptedException {
    AuthClient authClient = createAuthClient();
    TestResponseListener listener = new TestResponseListener();

    wireMockServer.stubFor(
        post(urlEqualTo("/auth/login"))
            .willReturn(aResponse().withStatus(401).withBody("Invalid credentials")));

    authClient.login("usr", "wrongpassword", listener);

    assertTrue(listener.latch.await(5, TimeUnit.SECONDS));
    assertEquals(1, listener.failureCalls);
    assertEquals(401, listener.statusCode);
    assertEquals("Invalid credentials", listener.result);
  }

  @Test
  void loginError() throws InterruptedException {
    HttpClient httpClient = new HttpClient("http://localhost:1");
    AuthClient authClient = new AuthClient(httpClient);
    TestResponseListener listener = new TestResponseListener();

    authClient.login("usr", "psw12345", listener);

    assertTrue(listener.latch.await(5, TimeUnit.SECONDS));
    assertEquals(1, listener.errorCalls);
    assertNotNull(listener.error);
  }

  @Test
  void getUsernameWithToken() throws InterruptedException {
    AuthClient authClient = createAuthClient();
    authClient.setToken("blabla_jwt");
    TestResponseListener listener = new TestResponseListener();

    wireMockServer.stubFor(
        get(urlEqualTo("/users/me"))
            .withHeader("Authorization", equalTo("Bearer blabla_jwt"))
            .willReturn(aResponse().withStatus(200).withBody("usr")));

    authClient.getUsername(listener);

    assertTrue(listener.latch.await(5, TimeUnit.SECONDS));
    assertEquals(1, listener.successCalls);
    assertEquals("usr", listener.result);

    wireMockServer.verify(
        getRequestedFor(urlEqualTo("/users/me"))
            .withHeader("Authorization", equalTo("Bearer blabla_jwt")));
  }
}
