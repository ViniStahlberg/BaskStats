
package com.baskstats.controller;

import com.baskstats.service.TreinoAnalyticsService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/times/{timeId}/treinos")
public class TreinoAnalyticsController {

    private final TreinoAnalyticsService service;

    public TreinoAnalyticsController(TreinoAnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/plano")
    public Map<String, Object> planoTreino(
            @PathVariable int timeId,
            @RequestParam(defaultValue = "arremesso3") String foco,
            @RequestParam(required = false) BigDecimal meta,
            @RequestParam(defaultValue = "5") int ultimosJogos,
            @RequestParam(required = false) String posicao) {

        return service.gerarPlanoTreino(
                timeId,
                foco,
                meta,
                ultimosJogos,
                posicao
        );
    }
}
