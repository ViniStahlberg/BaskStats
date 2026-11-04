package com.time.api.repository;
 
import com.time.api.dto.TopScorerDTO;
import com.time.api.model.EstatisticaJogadoraPartida;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface EstatisticaJogadoraPartidaRepository extends JpaRepository<EstatisticaJogadoraPartida, Integer> {
 
    // Soma os pontos de todas as partidas por jogadora e conta em quantos
    // jogos ela tem estatística registrada. O "Pageable" é usado só para
    // limitar o resultado (TOP 10) de forma portável entre bancos.
    // NULLS LAST: no PostgreSQL, DESC coloca NULL no topo; sem isso uma
    // jogadora sem pontos registrados apareceria como a maior pontuadora.
    @Query("SELECT new com.time.api.dto.TopScorerDTO(" +
           "   e.jogadora.idJogadora, e.jogadora.nome, " +
           "   SUM(e.pontosFeitos), COUNT(e)) " +
           "FROM EstatisticaJogadoraPartida e " +
           "GROUP BY e.jogadora.idJogadora, e.jogadora.nome " +
           "ORDER BY SUM(e.pontosFeitos) DESC NULLS LAST, e.jogadora.nome ASC")
    List<TopScorerDTO> buscarTopPontuadoras(Pageable pageable);
}
 
