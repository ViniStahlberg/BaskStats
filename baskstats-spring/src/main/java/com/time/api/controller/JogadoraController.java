package com.time.api.controller;

import com.time.api.model.Jogadora;
import com.time.api.service.JogadoraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jogadoras")
public class JogadoraController {

    private final JogadoraService jogadoraService;

    public JogadoraController(JogadoraService jogadoraService) {
        this.jogadoraService = jogadoraService;
    }

    // GET /api/jogadoras
    @GetMapping
    public ResponseEntity<List<Jogadora>> listarTodas() {
        return ResponseEntity.ok(jogadoraService.listarTodas());
    }

    // GET /api/jogadoras/{id}
    // Único lugar onde já vale a pena devolver 404: sem isso o front
    // recebe "200 com corpo vazio" e não dá pra distinguir de um erro.
    @GetMapping("/{id}")
    public ResponseEntity<Jogadora> buscarPorId(@PathVariable Integer id) {
        return jogadoraService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/jogadoras/time/{timeId}
    @GetMapping("/time/{timeId}")
    public ResponseEntity<List<Jogadora>> listarPorTime(@PathVariable Integer timeId) {
        return ResponseEntity.ok(jogadoraService.listarPorTime(timeId));
    }

    // GET /api/jogadoras?posicao=X
    // "params = posicao" faz essa rota só ser escolhida quando o query
    // param existe - sem ele, cai no listarTodas() acima normalmente.
    //
    // IMPORTANTE: só vai funcionar de verdade quando a coluna
    // Elenco.posicao existir no banco. Sem ela, a aplicação nem sobe
    // (ddl-auto=validate). Ver nota em application.properties.
    @GetMapping(params = "posicao")
    public ResponseEntity<List<Jogadora>> listarPorPosicao(@RequestParam String posicao) {
        return ResponseEntity.ok(jogadoraService.listarPorPosicao(posicao));
    }
}
