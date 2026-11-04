package com.time.api.controller;

import com.time.api.dto.TopScorerDTO;
import com.time.api.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    // GET /api/stats/top-scorers
    @GetMapping("/top-scorers")
    public ResponseEntity<List<TopScorerDTO>> topScorers() {
        return ResponseEntity.ok(statsService.buscarTop10Pontuadoras());
    }
}
