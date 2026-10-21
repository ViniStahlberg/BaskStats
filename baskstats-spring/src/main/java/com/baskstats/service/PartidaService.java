package com.time.api.service;

import com.time.api.model.Partida;
import com.time.api.repository.PartidaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PartidaService {

    private final PartidaRepository repository;

    public PartidaService(PartidaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Partida buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Partida não encontrada: " + id));
    }
}