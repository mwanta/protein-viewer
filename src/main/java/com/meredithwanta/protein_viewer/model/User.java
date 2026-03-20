package com.meredithwanta.protein_viewer.model;

import jakarta.persistence.*;

/**
 * Entity class:
 * A user data table to represent a User that has a username and password.
 * Stores and fetches data from the PostgreSQL database.
 */
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  /**
   * Basic constructor
   */
  public User() {}

  /**
   * To construct a User with a unique username and non-unique password.
   *
   * @param username: this User's username.
   * @param password: this User's password.
   */
  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  /**
   * Returns the ID of this User.
   *
   * @return the ID of this User.
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns the username of this User.
   *
   * @return the username of this User.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Returns the password of this User.
   *
   * @return the password of this User.
   */
  public String getPassword() {
    return password;
  }

}
