package com.pdg.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

  @NotBlank @Email public String email;

  @NotBlank
  @Size(min = 3, max = 15)
  public String username;

  @NotBlank
  @Size(min = 6)
  public String password;
}
