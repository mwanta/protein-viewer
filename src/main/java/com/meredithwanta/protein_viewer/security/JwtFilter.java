package com.meredithwanta.protein_viewer.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Represents the Spring component class to run on each request.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  /**
   * To construct a JwtFilter with an instance of JwtUtil.
   *
   * @param jwtUtil: the jwtUtil object adaptor.
   */
  public JwtFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  /**
   * Runs on every request
   *
   * Steps:
   *  1. Extract the token.
   *  2. Validate the token.
   *  3. Retrieve the user.
   *  4. Pass the request to the next filter or controller (if at the end of the filerChain).
   *
   * @param request: incoming HTTP request
   * @param response: the outgoing response
   * @param filterChain: the chain of filters to run
   * @throws ServletException
   * @throws IOException
   */
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      if (jwtUtil.validateToken(token)) {
        String username = jwtUtil.extractUsername(token);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(username, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    filterChain.doFilter(request, response);
  }
}
