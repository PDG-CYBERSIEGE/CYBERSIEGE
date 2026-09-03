package com.pdg.user;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

  @Id @GeneratedValue public Long id;

  @Column(nullable = false, unique = true)
  public String email;

  @Column(nullable = false, unique = true)
  public String username;

  @Column(nullable = false)
  public String passwordHash;
}
