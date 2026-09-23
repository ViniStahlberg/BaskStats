package com.time.api.service;

import com.time.api.model.Jogadora;
import com.time.api.repository.JogadoraRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JogadoraService {

    private final JogadoraRepository jogadoraRepository;

    public JogadoraService(JogadoraRepository jogadoraRepository) {
        this.jogadoraRepository = jogadoraRepository;
    }

    public List<Jogadora> listarTodas() {
        return jogadoraRepository.findAll();
    }
}
