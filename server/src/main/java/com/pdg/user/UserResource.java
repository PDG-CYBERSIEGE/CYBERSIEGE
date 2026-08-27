package com.pdg.user;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * REST resource providing operations for the currently authenticated user.
 */
@Path("/users")
public class UserResource {

  @Inject JsonWebToken jwt;

  /**
   * Returns the username of the currently authenticated user.
   *
   * <p>The user is identified using the subject ({@code sub}) claim of the JWT.
   *
   * @return an HTTP 200 response containing the username, or HTTP 404 if the
   *         user does not exist
   */
  @GET
  @Path("/me")
  @Authenticated
  public Response me() {
    User user = User.findById(Long.valueOf(jwt.getSubject()));
    if (user == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(user.username).build();
  }
}
