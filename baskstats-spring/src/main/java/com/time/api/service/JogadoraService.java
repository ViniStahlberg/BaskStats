package com.time.api.service;

import com.time.api.model.Jogadora;
import com.time.api.repository.JogadoraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JogadoraService {

    private final JogadoraRepository jogadoraRepository;

    public JogadoraService(JogadoraRepository jogadoraRepository) {
        this.jogadoraRepository = jogadoraRepository;
    }

    public List<Jogadora> listarTodas() {
        return jogadoraRepository.findAll();
    }

    public Optional<Jogadora> buscarPorId(Integer id) {
        return jogadoraRepository.findById(id);
    }

    public List<Jogadora> listarPorTime(Integer timeId) {
        return jogadoraRepository.findByTimeId(timeId);
    }

    public List<Jogadora> listarPorPosicao(String posicao) {
        return jogadoraRepository.findByPosicao(posicao);
    }
}
