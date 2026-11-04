package com.baskstats.repository;

import com.baskstats.model.EstatisticaJogadoraPartida;
import com.baskstats.model.MediaJogadoraProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstatisticaJogadoraPartidaRepository
        extends JpaRepository<EstatisticaJogadoraPartida, Integer> {

    @Query("""
            SELECT
                AVG(e.pontosFeitos)   AS mediaPontos,
                AVG(e.reboteTotal)    AS mediaRebotes,
                AVG(e.assistencias)   AS mediaAssistencias,
                COUNT(e)              AS totalPartidas
            FROM EstatisticaJogadoraPartida e
            WHERE e.jogadora.idJogadora = :jogadoraId
            """)
    Optional<MediaJogadoraProjection> calcularMedias(@Param("jogadoraId") Integer jogadoraId);
}