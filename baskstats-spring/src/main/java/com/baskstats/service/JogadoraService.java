package com.baskstats.service;

import com.baskstats.model.*;
import com.baskstats.repository.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JogadoraService {

  private final JogadoraRepository jogadoraRepository;
  private final EstatisticaJogadoraPartidaRepository estatisticaRepository;

  public JogadoraService(
      JogadoraRepository jogadoraRepository,
      EstatisticaJogadoraPartidaRepository estatisticaRepository) {
    this.jogadoraRepository = jogadoraRepository;
    this.estatisticaRepository = estatisticaRepository;
  }

  public List<Jogadora> listarTodas() {
    return jogadoraRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Jogadora buscarPorId(Integer id) {
    return jogadoraRepository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Jogadora não encontrada: " + id));
  }

  @Transactional(readOnly = true)
  public MediaJogadoraResponse buscarMediasPorId(Integer id) {
    // garante 404 correto quando a jogadora não existe,
    // em vez de devolver médias vazias/nulas pra um ID inexistente
    Jogadora jogadora = buscarPorId(id);

    MediaJogadoraProjection projecao =
        estatisticaRepository
            .calcularMedias(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Nenhuma partida registrada para a jogadora: " + id));

    if (projecao.getTotalPartidas() == null || projecao.getTotalPartidas() == 0) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Nenhuma partida registrada para a jogadora: " + id);
    }

    return new MediaJogadoraResponse(
        jogadora.getIdJogadora(),
        jogadora.getNome(),
        arredondar(projecao.getMediaPontos()),
        arredondar(projecao.getMediaRebotes()),
        arredondar(projecao.getMediaAssistencias()),
        projecao.getTotalPartidas());
  }

  private Double arredondar(Double valor) {
    if (valor == null) return 0.0;
    return Math.round(valor * 100.0) / 100.0;
  }
}
