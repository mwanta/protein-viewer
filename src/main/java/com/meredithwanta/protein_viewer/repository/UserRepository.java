package com.meredithwanta.protein_viewer.repository;

import com.meredithwanta.protein_viewer.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * To represent the operations that can be performed on the User table.
 * Extends JpaRepository for use of standard database operations.
 */
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds a User in the users table by username. Utilizes Spring Data JPA to automatically
   * generated an implementation based on the method name.
   *
   * Format:
   *  findBy: Generate SELECT query
   *  Username: Go to username field on User
   *
   * @param username: the username to search for.
   *
   * @return the User associated with the given username if they exist.
   */
  Optional<User> findByUsername(String username);
}
