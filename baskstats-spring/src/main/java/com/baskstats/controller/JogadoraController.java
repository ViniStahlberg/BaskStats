package com.baskstats.controller;

import com.baskstats.model.*;
import com.baskstats.service.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jogadoras")
public class JogadoraController {

  private final JogadoraService jogadoraService;

  public JogadoraController(JogadoraService jogadoraService) {
    this.jogadoraService = jogadoraService;
  }

  @GetMapping
  public ResponseEntity<List<Jogadora>> listarTodas() {
    List<Jogadora> jogadoras = jogadoraService.listarTodas();
    return ResponseEntity.ok(jogadoras);
  }

  @GetMapping("/{id}/medias")
  public ResponseEntity<MediaJogadoraResponse> buscarMediasPorId(@PathVariable Integer id) {
    return ResponseEntity.ok(jogadoraService.buscarMediasPorId(id));
  }
}
