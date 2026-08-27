package com.pdg.auth;

import com.pdg.user.User;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

/** Provides the logic for user authentication and registration. */
@ApplicationScoped
public class AuthService {

  /**
   * Registers a new user.
   *
   * <p>The password is hashed before being stored.
   *
   * @param email the user's email address
   * @param username the user's username
   * @param password the user's plain-text password
   * @return an HTTP response indicating the result of the registration
   */
  @Transactional
  public Response register(String email, String username, String password) {

    // Do not add user if mail or name already exist
    if (User.count("email", email) != 0) {
      return Response.status(Response.Status.CONFLICT).entity("Email already in use").build();
    }
    if (User.count("username", username) != 0) {
      return Response.status(Response.Status.CONFLICT).entity("Username already in use").build();
    }

    // Add user
    User user = new User();
    user.email = email;
    user.username = username;
    user.passwordHash = BcryptUtil.bcryptHash(password);
    user.persist();

    return Response.ok().build();
  }

  /**
   * Authenticates a user and generates an authentication token.
   *
   * @param username the user's username
   * @param password the user's plain-text password
   * @return an HTTP response containing the token if authentication succeeds
   */
  public Response login(String username, String password) {

    User user = User.find("username", username).firstResult();

    // Invalid username
    if (user == null) {
      return Response.status(Response.Status.UNAUTHORIZED)
          .entity("No player with that username")
          .build();
    }

    // Invalid password
    if (!BcryptUtil.matches(password, user.passwordHash)) {
      return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid password").build();
    }

    // Generate token
    String token = Jwt.subject(String.valueOf(user.id)).sign();

    return Response.ok(token).build();
  }
}
