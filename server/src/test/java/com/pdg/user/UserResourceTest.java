package com.pdg.user;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserResourceTest {

  @BeforeEach
  @Transactional
  void cleanDatabase() {
    User.deleteAll();
  }

  private void createUser() {
    QuarkusTransaction.begin();
    User user = new User();
    user.email = "test@test.com";
    user.username = "usr";
    user.passwordHash = BcryptUtil.bcryptHash("psw12345");
    user.persist();
    QuarkusTransaction.commit();
  }

  @Test
  void meRejectsUnauthenticatedUser() {
    given().when().get("/users/me").then().statusCode(401);
  }

  @Test
  void meAcceptsValidJwt() {
    createUser();

    String token =
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                """
                  {
                    "username": "usr",
                    "password": "psw12345"
                  }
                """)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .asString();

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/users/me")
        .then()
        .statusCode(200)
        .body(is("usr"));
  }

  @Test
  void meRejectsInvalidJwt() {
    createUser();

    String token =
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                """
                  {
                    "username": "usr",
                    "password": "psw12345"
                  }
                """)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .asString();

    String invalidToken = token + "invalid";

    given()
        .header("Authorization", "Bearer " + invalidToken)
        .when()
        .get("/users/me")
        .then()
        .statusCode(401);
  }
}
