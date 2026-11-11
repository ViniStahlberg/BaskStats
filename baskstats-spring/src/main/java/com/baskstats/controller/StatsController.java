package com.baskstats.controller;

import com.baskstats.service.AnalyticsService;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class StatsController {
  private final AnalyticsService service;

  public StatsController(AnalyticsService service) {
    this.service = service;
  }

  @GetMapping("/times")
  public List<Map<String, Object>> teams() {
    return service.teams();
  }

  @GetMapping("/times/{id}/dashboard")
  public Map<String, Object> dashboard(@PathVariable int id) {
    return service.dashboard(id, null);
  }

  @GetMapping("/times/{id}/jogadoras")
  public List<Map<String, Object>> players(@PathVariable int id) {
    return service.players(id);
  }

  @GetMapping("/jogadoras/{id}/estatisticas")
  public Map<String, Object> player(@PathVariable int id) {
    return service.player(id);
  }

  @GetMapping("/times/{id}/pre-jogo")
  public Map<String, Object> pre(@PathVariable int id) {
    return service.match(id, true);
  }

  @GetMapping("/times/{id}/pos-jogo")
  public Map<String, Object> post(@PathVariable int id) {
    return service.match(id, false);
  }
}
