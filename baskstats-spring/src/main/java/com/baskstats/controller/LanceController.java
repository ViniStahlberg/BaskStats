package com.time.api.controller;

import com.time.api.model.Lance;
import com.time.api.service.LanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lances")
public class LanceController {

    private final LanceService service;

    public LanceController(LanceService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Lance buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id);
    }
}