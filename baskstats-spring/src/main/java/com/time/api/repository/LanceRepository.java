package com.time.api.repository;

import com.time.api.model.Lance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanceRepository extends JpaRepository<Lance, Integer> {

    // Navega Lance -> Quarto -> Partida (o id_partida não está direto na
    // tabela Lance, só em Quarto). Ordena por número do quarto e depois
    // pela ordem do lance dentro do quarto, reconstituindo a cronologia.
    @Query("SELECT l FROM Lance l " +
           "WHERE l.quarto.partida.idPartida = :partidaId " +
           "ORDER BY l.quarto.numero ASC, l.ordemNoQuarto ASC")
    List<Lance> findByPartidaId(@Param("partidaId") Integer partidaId);
}
