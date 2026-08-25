package com.pdg.user;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/users")
public class UserResource {

  @Inject JsonWebToken jwt;

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
