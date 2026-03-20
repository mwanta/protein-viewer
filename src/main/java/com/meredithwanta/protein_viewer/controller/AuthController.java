package com.meredithwanta.protein_viewer.controller;

import com.meredithwanta.protein_viewer.model.User;
import com.meredithwanta.protein_viewer.repository.UserRepository;
import com.meredithwanta.protein_viewer.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * A controller to create a user and/or login a user in.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  /**
   * To construct this AuthController.
   *
   * @param userRepository: to interact with this User's data.
   * @param passwordEncoder: the password hashing algorithm.
   * @param jwtUtil: the utility method for this User's token.
   */
  public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  /**
   * Registers a User to the database.
   *
   * @param body: the given username and password to register.
   * @return a response indicating registration success.
   */
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
    String username = body.get("username");
    String password = body.get("password");

    //username must be unique
    if (userRepository.findByUsername(username).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body("Username is already in use");
    }

    //hash the password so that the plain text version is not stored
    User user = new User(username, passwordEncoder.encode(password));
    userRepository.save(user);
    return ResponseEntity.ok("User registered successfully");
  }

  /**
   * Uses the given username and password to attempt a login.
   *
   * @param body: the given username and password.
   *
   * @return a map of "token" -> JSON string
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
    String username = body.get("username");
    String password = body.get("password");

    Optional<User> user = userRepository.findByUsername(username);

    if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    String token = jwtUtil.generateToken(user.get());
    return ResponseEntity.ok(Map.of("token", token));
  }

}
