package com.pdg.match;

import static org.junit.jupiter.api.Assertions.*;

import com.pdg.user.User;
import com.pdg.user.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.websockets.next.BasicWebSocketConnector;
import io.quarkus.websockets.next.WebSocketClientConnection;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class MatchWebSocketIntegrationTest {

  @TestHTTPResource("/match")
  URI matchUri;

  @Inject UserRepository userRepository;

  @Inject Instance<BasicWebSocketConnector> connector;

  @BeforeEach
  @Transactional
  void cleanDatabase() {
    userRepository.deleteAll();
  }

  private User createUser(String email, String username) {
    QuarkusTransaction.begin();
    User user = new User();
    user.email = email;
    user.username = username;
    user.passwordHash = BcryptUtil.bcryptHash("psw12345");
    userRepository.persist(user);
    QuarkusTransaction.commit();
    return user;
  }

  private String createToken(User user) {
    return Jwt.subject(String.valueOf(user.id)).issuer("https://cybersiege.com").sign();
  }

  private static class TestClient {

    private WebSocketClientConnection connection;
    private final LinkedBlockingDeque<String> messages = new LinkedBlockingDeque<>();

    private void setConnection(WebSocketClientConnection connection) {
      this.connection = connection;
    }

    void send(String message) {
      connection.sendTextAndAwait(message);
    }

    String awaitMessage() throws InterruptedException {
      return messages.poll(5, TimeUnit.SECONDS);
    }

    void receive(String message) {
      messages.add(message);
    }

    void close() {
      connection.closeAndAwait();
    }
  }

  private TestClient connect(String token) {
    TestClient client = new TestClient();

    WebSocketClientConnection connection =
        connector
            .get()
            .baseUri(matchUri)
            .addHeader("Authorization", "Bearer " + token)
            .onTextMessage((c, message) -> client.receive(message))
            .connectAndAwait();

    client.setConnection(connection);

    return client;
  }

  @Test
  void matchReceivesMessageAndCanSend() throws Exception {
    User user = createUser("player@test.com", "player");
    TestClient player = connect(createToken(user));
    player.send("Hello !");
    assertEquals("Hello Back !", player.awaitMessage());
    player.close();
  }

  @Test
  void twoPlayersCanConnectAndAreMatched() throws Exception {
    User user1 = createUser("player1@test.com", "player1");
    User user2 = createUser("player2@test.com", "player2");

    TestClient player1 = connect(createToken(user1));
    TestClient player2 = connect(createToken(user2));

    String match1 = player1.awaitMessage();
    String match2 = player2.awaitMessage();

    assertNotNull(match1);
    assertNotNull(match2);

    assertTrue(match1.contains("\"type\":\"START\""));
    assertTrue(match2.contains("\"type\":\"START\""));

    assertTrue(match1.contains("\"matchId\":\"1\""));
    assertTrue(match2.contains("\"matchId\":\"1\""));

    assertTrue(match1.contains("\"opponent\":\"" + user2.id + "\""));
    assertTrue(match2.contains("\"opponent\":\"" + user1.id + "\""));

    player1.close();
    player2.close();
  }

  @Test
  void fourPlayersAreMatchedIntoTwoMatches() throws Exception {
    User user1 = createUser("player1@test.com", "player1");
    User user2 = createUser("player2@test.com", "player2");
    User user3 = createUser("player3@test.com", "player3");
    User user4 = createUser("player4@test.com", "player4");

    TestClient player1 = connect(createToken(user1));
    TestClient player2 = connect(createToken(user2));
    TestClient player3 = connect(createToken(user3));
    TestClient player4 = connect(createToken(user4));

    String match1 = player1.awaitMessage();
    String match2 = player2.awaitMessage();
    String match3 = player3.awaitMessage();
    String match4 = player4.awaitMessage();

    assertNotNull(match1);
    assertNotNull(match2);
    assertNotNull(match3);
    assertNotNull(match4);

    assertEquals("START", GameMessages.type(match1));
    assertEquals("START", GameMessages.type(match2));
    assertEquals("START", GameMessages.type(match3));
    assertEquals("START", GameMessages.type(match4));

    assertTrue(match1.contains("\"matchId\""));
    assertTrue(match2.contains("\"matchId\""));
    assertTrue(match3.contains("\"matchId\""));
    assertTrue(match4.contains("\"matchId\""));

    assertTrue(match1.contains("\"opponent\":\"" + user2.id + "\""));
    assertTrue(match2.contains("\"opponent\":\"" + user1.id + "\""));
    assertTrue(match3.contains("\"opponent\":\"" + user4.id + "\""));
    assertTrue(match4.contains("\"opponent\":\"" + user3.id + "\""));

    player1.close();
    player2.close();
    player3.close();
    player4.close();
  }

  @Test
  void playerDisconnectsWhileSearching() throws Exception {
    User user1 = createUser("player1@test.com", "player1");
    User user2 = createUser("player2@test.com", "player2");

    TestClient player1 = connect(createToken(user1));
    player1.close();
    TestClient player2 = connect(createToken(user2));

    assertNull(player2.awaitMessage());

    player2.close();
  }
}
