package org.temuujinmust.lab12.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.temuujinmust.lab12.model.User;
import org.temuujinmust.lab12.service.UserService;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final UserService userService;

  public SecurityConfig(UserService userService) {
    this.userService = userService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(request -> {
          CorsConfiguration config = new CorsConfiguration();
          config.setAllowedOrigins(List.of("http://localhost:3000"));
          config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
          config.setAllowedHeaders(List.of("*"));
          return config;
        }))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/register", "/api/login").permitAll()
            .anyRequest().authenticated());
    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      User user = userService.findByUsername(username);
      if (user == null)
        throw new UsernameNotFoundException("User not found");
      return org.springframework.security.core.userdetails.User
          .withUsername(user.getUsername())
          .password(user.getPassword())
          .roles(user.getRole())
          .build();
    };
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }
}
