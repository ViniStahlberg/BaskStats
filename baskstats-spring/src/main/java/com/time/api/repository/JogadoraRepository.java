package com.time.api.repository;

import com.time.api.model.Jogadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JogadoraRepository extends JpaRepository<Jogadora, Integer> {

    // findById(Integer) já vem pronto do JpaRepository - usado na rota
    // GET /api/jogadoras/{id}

    // Lista as jogadoras que têm (ou tiveram) vínculo com um time,
    // via tabela Elenco. Não filtra por temporada - traz todo o histórico.
    @Query("SELECT DISTINCT e.jogadora FROM Elenco e WHERE e.time.idTime = :timeId")
    List<Jogadora> findByTimeId(@Param("timeId") Integer timeId);

    // Opção B: posição vive em Elenco (pode variar por time/temporada).
    // Traz qualquer jogadora que já tenha jogado nessa posição em algum
    // elenco (não restringe à temporada atual) - mesmo critério usado
    // em findByTimeId, pra manter consistência.
    //
    // IMPORTANTE: só funciona quando a coluna Elenco.posicao existir de
    // fato no banco (ddl-auto=validate não cria/altera nada sozinho).
    @Query("SELECT DISTINCT e.jogadora FROM Elenco e WHERE e.posicao = :posicao")
    List<Jogadora> findByPosicao(@Param("posicao") String posicao);
}
