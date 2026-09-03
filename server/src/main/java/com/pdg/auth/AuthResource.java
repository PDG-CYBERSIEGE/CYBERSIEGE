package com.pdg.auth;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST resource providing authentication endpoints.
 *
 * <p>Provides endpoints for user registration and login.
 */
@Path("/auth")
public class AuthResource {

  @Inject AuthService authService;

  /**
   * Registers a new user.
   *
   * @param request the registration information
   * @return an HTTP response indicating whether the registration succeeded
   */
  @POST
  @Path("/register")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response register(@Valid RegisterRequest request) {
    return authService.register(request.email, request.username, request.password);
  }

  /**
   * Authenticates a user and returns an authentication token.
   *
   * @param request the user's login credentials
   * @return an HTTP response containing the authentication token if successful
   */
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response login(@Valid LoginRequest request) {
    return authService.login(request.username, request.password);
  }
}
