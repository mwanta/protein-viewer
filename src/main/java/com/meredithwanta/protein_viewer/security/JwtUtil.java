package com.meredithwanta.protein_viewer.security;

import com.meredithwanta.protein_viewer.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * A class to represent utility methods for JWTs.
 */
@Component
public class JwtUtil {

  //secret key for signing tokens
  private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private final long EXPIRATION_TIME = 864_000_000;

  /**
   * To generate a JWT Token for the given user.
   *
   * @param user: the User to generate a token for.
   *
   * @return the given User's token.
   */
  public String generateToken(User user) {
    return Jwts.builder()
        .setSubject(user.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(key)
        .compact();
  }

  /**
   * Returns the username associated with this JWT Token.
   *
   * @param token: the token to lookup.
   *
   * @return the username associated with a given token.
   */
  public String extractUsername(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  /**
   * Validates the given token.
   *
   * @param token: the token to be validated.
   *
   * @return true if the token is valid, false otherwise.
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }
}
