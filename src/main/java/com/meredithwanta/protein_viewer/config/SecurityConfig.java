package com.meredithwanta.protein_viewer.config;

import com.meredithwanta.protein_viewer.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Represents the Spring configuration class to allow customization via the filterChain method.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtFilter jwtFilter;

  /**
   * Constructs this security configuration with a JWT Filter.
   *
   * @param jwtFilter: the filter to use for this security config.
   */
  public SecurityConfig(JwtFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  /**
   * Configures the security filter chain for all HTTP requests.
   *
   * - Disables CSRF protection since the app uses stateless JWT authentication rather than session cookies
   * - Sets session management to stateless so no server-side sessions are created
   * - Permits unauthenticated access to /api/auth/** (login and register endpoints)
   * - Requires authentication for all other endpoints
   * - Registers JwtFilter to run before Spring's built-in UsernamePasswordAuthenticationFilter
   *      so the security context is populated from the JWT before authorization checks run
   *
   * @param http the HttpSecurity object used to configure web-based security
   * @return the configured SecurityFilterChain
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /**
   * Defines the password hashing algorithm.
   *
   * @return the password hashing algorithm.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
