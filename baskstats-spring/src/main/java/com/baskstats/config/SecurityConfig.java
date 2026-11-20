package com.baskstats.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.*;

@Configuration
public class SecurityConfig {
  @Bean
  PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService users(
      PasswordEncoder encoder,
      @Value("${baskstats.admin.email:admin@baskstats.local}") String adminEmail,
      @Value("${baskstats.admin.password}") String adminPassword) {
    UserDetails admin =
        User.withUsername(adminEmail.toLowerCase(java.util.Locale.ROOT))
            .password(encoder.encode(adminPassword))
            .roles("ADMIN", "USER")
            .build();
    return new InMemoryUserDetailsManager(admin);
  }

  @Bean
  AuthenticationManager authenticationManager(UserDetailsService users, PasswordEncoder encoder) {
    var provider = new DaoAuthenticationProvider(users);
    provider.setPasswordEncoder(encoder);
    return new ProviderManager(provider);
  }

  @Bean
  SecurityFilterChain security(HttpSecurity http) throws Exception {
    var handler = new CsrfTokenRequestAttributeHandler();
    return http.csrf(
            c ->
                c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(handler))
        .authorizeHttpRequests(
            a ->
                a.requestMatchers("/api/health", "/api/auth/csrf", "/api/auth/login", "/error")
                    .permitAll()
                    .requestMatchers("/api/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint((req, res, ex) -> res.sendError(401))
                    .accessDeniedHandler((req, res, ex) -> res.sendError(403)))
        .requestCache(c -> c.disable())
        .logout(l -> l.disable())
        .build();
  }
}