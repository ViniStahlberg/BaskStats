package com.time.api.controller;

import com.time.api.model.Lance;
import com.time.api.model.Partida;
import com.time.api.service.PartidaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    private final PartidaService partidaService;

    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    // GET /api/partidas
    // GET /api/partidas?data=2026-05-01
    // GET /api/partidas?dataInicio=2026-05-01&dataFim=2026-05-31
    @GetMapping
    public ResponseEntity<List<Partida>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(partidaService.listar(data, dataInicio, dataFim));
    }

    // GET /api/partidas/{id}/lances
    @GetMapping("/{id}/lances")
    public ResponseEntity<List<Lance>> listarLances(@PathVariable Integer id) {
        return ResponseEntity.ok(partidaService.buscarLances(id));
    }
}
