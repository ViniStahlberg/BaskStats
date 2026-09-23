package com.time.api.controller;

import com.time.api.model.Jogadora;
import com.time.api.service.JogadoraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
