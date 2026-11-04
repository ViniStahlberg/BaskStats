package com.time.api.service;

import com.time.api.model.Lance;
import com.time.api.model.Partida;
import com.time.api.repository.LanceRepository;
import com.time.api.repository.PartidaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final LanceRepository lanceRepository;

    public PartidaService(PartidaRepository partidaRepository, LanceRepository lanceRepository) {
        this.partidaRepository = partidaRepository;
        this.lanceRepository = lanceRepository;
    }

    // Suporta dois jeitos de filtrar por data:
    //   ?data=2026-05-01                      -> só aquele dia
    //   ?dataInicio=...&dataFim=...            -> intervalo (um dos dois é opcional)
    // Sem nenhum parâmetro -> lista tudo, ordenado por data_hora.
    public List<Partida> listar(LocalDate data, LocalDate dataInicio, LocalDate dataFim) {
        if (data != null) {
            LocalDateTime inicio = data.atStartOfDay();
            LocalDateTime fim = data.plusDays(1).atStartOfDay();
            return partidaRepository.findByDataHoraBetweenOrderByDataHoraAsc(inicio, fim);
        }

        if (dataInicio != null || dataFim != null) {
            LocalDateTime inicio = (dataInicio != null)
                    ? dataInicio.atStartOfDay()
                    : LocalDateTime.of(1900, 1, 1, 0, 0);
            LocalDateTime fim = (dataFim != null)
                    ? dataFim.plusDays(1).atStartOfDay()
                    : LocalDateTime.of(2100, 1, 1, 0, 0);
            return partidaRepository.findByDataHoraBetweenOrderByDataHoraAsc(inicio, fim);
        }

        return partidaRepository.findAllByOrderByDataHoraAsc();
    }

    public List<Lance> buscarLances(Integer partidaId) {
        return lanceRepository.findByPartidaId(partidaId);
    }
}
