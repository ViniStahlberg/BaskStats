package com.time.api.service;

import com.time.api.model.Lance;
import com.time.api.repository.LanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LanceService {

    private final LanceRepository repository;

    public LanceService(LanceRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Lance buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lance não encontrado: " + id));
    }
}