package com.baskstats.controller;

import com.baskstats.model.Partida;
import com.baskstats.service.PartidaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

  private final PartidaService service;

  public PartidaController(PartidaService service) {
    this.service = service;
  }

  @GetMapping("/{id}")
  public Partida buscarPorId(@PathVariable Integer id) {
    return service.buscarPorId(id);
  }
}
