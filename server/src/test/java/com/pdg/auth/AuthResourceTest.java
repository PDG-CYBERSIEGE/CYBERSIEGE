package com.pdg.auth;

import static io.restassured.RestAssured.given;

import com.pdg.user.User;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class AuthResourceTest {

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
  void registerOk() {
    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            """
              {
                  "email": "test@test.com",
                  "username": "usr",
                  "password": "psw12345"
              }
            """)
        .when()
        .post("/auth/register")
        .then()
        .statusCode(200);
  }

  @Test
  void registerRejectsDuplicateEmail() {
    createUser();

    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            """
              {
                "email": "test@test.com",
                "username": "usr2",
                "password": "psw12345"
              }
            """)
        .when()
        .post("/auth/register")
        .then()
        .statusCode(409);
  }

  @Test
  void registerRejectsDuplicateUsername() {
    createUser();

    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            """
              {
                "email": "test@test.com",
                "username": "usr",
                "password": "psw12345"
              }
            """)
        .when()
        .post("/auth/register")
        .then()
        .statusCode(409);
  }

  @Test
  void registerRejectsInvalidEmail() {
    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            """
              {
                "email": "not_email",
                "username": "usr",
                "password": "psw12345"
              }
              """)
        .when()
        .post("/auth/register")
        .then()
        .statusCode(400);
  }

  @Test
  void registerRejectsShortUsername() {
    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            """
                {
                  "email": "test@test.com",
                  "username": "ab",
                  "password": "psw12345"
                }
                """)
        .when()
        .post("/auth/register")
        .then()
        .statusCode(400);
  }

  @Test
  void registerRejectsShortPassword() {
    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            """
              {
                "email": "test@test.com",
                "username": "usr",
                "password": "psw"
              }
              """)
        .when()
        .post("/auth/register")
        .then()
        .statusCode(400);
  }

  @Test
  void registerRejectsEmptyFields() {
    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            """
                  {
                    "email": "",
                    "username": "",
                    "password": ""
                  }
                """)
        .when()
        .post("/auth/register")
        .then()
        .statusCode(400);
  }

  @Test
  void registerRejectsMissingFields() {
    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            """
              {
                "email": "test@test.com"
              }
              """)
        .when()
        .post("/auth/register")
        .then()
        .statusCode(400);
  }

  @Test
  void loginOk() {
    createUser();

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
        .statusCode(200);
  }

  @Test
  void loginRejectsWrongPassword() {
    createUser();

    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            """
              {
                "username": "usr",
                "password": "wrong"
              }
            """)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(401);
  }

  @Test
  void loginRejectsUnknownUsername() {
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
        .statusCode(401);
  }

  @Test
  void loginRejectsEmptyFields() {
    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            """
              {
                "username": "",
                "password": ""
              }
              """)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(400);
  }

  @Test
  void loginRejectsMissingFields() {
    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            """
              {
                "username": "usr"
              }
              """)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(400);
  }
}
