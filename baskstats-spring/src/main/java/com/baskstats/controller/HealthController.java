package com.baskstats.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.*;

@RestController
public class HealthController {
  private final JdbcClient db;

  public HealthController(JdbcClient db) {
    this.db = db;
  }

  @GetMapping("/api/health")
  public ResponseEntity<Map<String, String>> health() {
    try {
      db.sql("select 1").query(Integer.class).single();
      return ResponseEntity.ok(Map.of("status", "UP"));
    } catch (Exception e) {
      return ResponseEntity.status(503).body(Map.of("status", "DOWN"));
    }
  }
}
