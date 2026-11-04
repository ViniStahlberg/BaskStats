package com.time.api.service;

import com.time.api.dto.TopScorerDTO;
import com.time.api.repository.EstatisticaJogadoraPartidaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsService {

    private final EstatisticaJogadoraPartidaRepository estatisticaRepository;

    public StatsService(EstatisticaJogadoraPartidaRepository estatisticaRepository) {
        this.estatisticaRepository = estatisticaRepository;
    }

    public List<TopScorerDTO> buscarTop10Pontuadoras() {
        return estatisticaRepository.buscarTopPontuadoras(PageRequest.of(0, 10));
    }
}
