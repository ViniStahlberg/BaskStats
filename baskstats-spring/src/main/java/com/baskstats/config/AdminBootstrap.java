package com.baskstats.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrap {
  @Bean
  ApplicationRunner createAdmin(
      JdbcClient db,
      PasswordEncoder encoder,
      @Value("${baskstats.admin.email}") String email,
      @Value("${baskstats.admin.password}") String password) {
    return args -> {
      if (email.isBlank() && password.isBlank()) return;
      if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") || password.length() < 12)
        throw new IllegalArgumentException(
            "ADMIN_EMAIL válido e ADMIN_PASSWORD com pelo menos 12 caracteres são obrigatórios");
      db.sql(
              "insert into usuario(email,senha_hash) values(:email,:hash) on conflict(email) do"
                  + " nothing")
          .param("email", email.toLowerCase(java.util.Locale.ROOT))
          .param("hash", encoder.encode(password))
          .update();
    };
  }
}
