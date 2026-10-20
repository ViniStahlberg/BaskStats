package com.baskstats.service;

import com.baskstats.model.*;
import com.baskstats.repository.*;
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
