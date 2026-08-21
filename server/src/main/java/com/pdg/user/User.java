package com.pdg.user;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

  @Column(nullable = false, unique = true)
  public String email;

  @Column(nullable = false, unique = true)
  public String username;

  @Column(nullable = false)
  public String passwordHash;
}
