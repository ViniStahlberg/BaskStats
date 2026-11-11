package com.baskstats.repository;

import java.util.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class AnalyticsRepository {
  private final JdbcClient db;

  public AnalyticsRepository(JdbcClient db) {
    this.db = db;
  }

  public List<Map<String, Object>> query(String sql, Map<String, ?> args) {
    return db.sql(sql).params(args).query().listOfRows();
  }

  public List<Map<String, Object>> teams() {
    return query("select id_time as id,nome from time order by nome", Map.of());
  }

  public List<Map<String, Object>> games(int id, Integer partidaId) {
    return query(
        """
        select pt.id_partida_time, p.id_partida, p.confronto,p.data_hora,pt.placar_final,
        (select max(o.placar_final) from partida_time o where o.partida_id=p.id_partida and o.time_id<>pt.time_id) as adversario_placar
        from partida_time pt join partida p on p.id_partida=pt.partida_id
        where pt.time_id=:id and pt.placar_final is not null and p.data_hora<=current_timestamp
        and p.temporada_id=(select max(p2.temporada_id) from partida p2 join partida_time pt2 on pt2.partida_id=p2.id_partida where pt2.time_id=:id)
        """
            + (partidaId == null ? "" : " and p.id_partida=:partida")
            + " order by p.data_hora,p.id_partida",
        partidaId == null ? Map.of("id", id) : Map.of("id", id, "partida", partidaId));
  }

  private static final String STATS =
      """
      select j.id_jogadora as id,j.nome,coalesce(pos.posicao,'Não informada') as pos,
      coalesce(pos.numero_camisa,max(s.numero_camisa)) as numero,count(s.id_estatistica_jogadora) as jogos,
      round(coalesce(avg(s.pontos_feitos),0),1) as ppj,round(coalesce(avg(s.rebote_total),0),1) as reb,
      round(coalesce(avg(s.assistencias),0),1) as ast,round(coalesce(avg(s.roubos_bola),0),1) as roubos,
      round(coalesce(avg(s.turnovers),0),1) as turnovers,round(coalesce(avg(s.eficiencia),0),1) as eficiencia,
      cast(null as numeric) as tocos,
      round(avg(case when s.tempo_jogado ~ '^[0-9]{1,3}:[0-5][0-9]$'
      then split_part(s.tempo_jogado,':',1)::numeric + split_part(s.tempo_jogado,':',2)::numeric/60
      else null end),1) as minutos,
      round(coalesce(100.0*sum(s.arremesso_2_feitos)/nullif(sum(s.arremesso_2_tentados),0),0),1) as "arremesso2",
      round(coalesce(100.0*sum(s.arremesso_3_feitos)/nullif(sum(s.arremesso_3_tentados),0),0),1) as "arremesso3",
      round(coalesce(100.0*sum(s.lance_livre_feitos)/nullif(sum(s.lance_livre_tentados),0),0),1) as "lanceLivre"
      from jogadora j
      """;

  public List<Map<String, Object>> players(int id, Integer partida) {
    return query(
        STATS
            + """
            join lateral (select e.posicao,e.numero_camisa,e.temporada_id from elenco e
            where e.jogadora_id=j.id_jogadora and e.time_id=:id
            and e.temporada_id=(select max(e2.temporada_id) from elenco e2 where e2.time_id=:id)
            order by e.temporada_id desc,e.id_elenco desc limit 1) pos on true
            left join estatisticajogadorapartida s on s.jogadora_id=j.id_jogadora and s.partida_time_id in
            (select pt.id_partida_time from partida_time pt join partida p on p.id_partida=pt.partida_id
            where pt.time_id=:id and p.temporada_id=pos.temporada_id
            """
            + (partida == null ? "" : " and pt.partida_id=:partida")
            + """
            ) group by j.id_jogadora,j.nome,pos.posicao,pos.numero_camisa
            order by ppj desc,j.nome
            """,
        partida == null ? Map.of("id", id) : Map.of("id", id, "partida", partida));
  }

  public List<Map<String, Object>> player(int id) {
    return query(
        STATS
            + """
            left join lateral (select e.posicao,e.numero_camisa from elenco e where e.jogadora_id=j.id_jogadora
            order by e.temporada_id desc,e.id_elenco desc limit 1) pos on true
            left join estatisticajogadorapartida s on s.jogadora_id=j.id_jogadora
            where j.id_jogadora=:id group by j.id_jogadora,j.nome,pos.posicao,pos.numero_camisa
            """,
        Map.of("id", id));
  }

  public List<Map<String, Object>> shooting(int id, Integer partida) {
    return query(
        """
        select round(coalesce(100.0*sum(s.arremesso_2_feitos)/nullif(sum(s.arremesso_2_tentados),0),0),1) as "arremesso2",
        round(coalesce(100.0*sum(s.arremesso_3_feitos)/nullif(sum(s.arremesso_3_tentados),0),0),1) as "arremesso3",
        round(coalesce(100.0*sum(s.lance_livre_feitos)/nullif(sum(s.lance_livre_tentados),0),0),1) as "lanceLivre"
        from estatisticajogadorapartida s join partida_time pt on pt.id_partida_time=s.partida_time_id
        join partida p on p.id_partida=pt.partida_id where pt.time_id=:id
        and p.temporada_id=(select max(p2.temporada_id) from partida p2 join partida_time pt2 on pt2.partida_id=p2.id_partida where pt2.time_id=:id)
        """
            + (partida == null ? "" : " and pt.partida_id=:partida"),
        partida == null ? Map.of("id", id) : Map.of("id", id, "partida", partida));
  }

  public List<Map<String, Object>> match(int id, boolean upcoming) {
    return query(
        """
        select p.*,pt.id_partida_time,pt.placar_final,o.time_id as adversario_id,o.placar_final as adversario_placar,t.nome as adversario
        from partida_time pt join partida p on p.id_partida=pt.partida_id
        join partida_time o on o.partida_id=p.id_partida and o.time_id<>pt.time_id
        join time t on t.id_time=o.time_id where pt.time_id=:id
        """
            + (upcoming
                ? " and p.data_hora>current_timestamp and pt.placar_final is null order by"
                    + " p.data_hora asc"
                : " and p.data_hora<=current_timestamp and pt.placar_final is not null and"
                    + " o.placar_final is not null order by p.data_hora desc")
            + ",p.id_partida limit 1",
        Map.of("id", id));
  }

  public List<Map<String, Object>> quarters(int partidaTime) {
    return query(
        """
        select q.numero,
        pq.placar_acumulado-lag(pq.placar_acumulado,1,0) over(order by q.numero) as placar,
        pq.placar_acumulado as acumulado,
        op.placar_acumulado-lag(op.placar_acumulado,1,0) over(order by q.numero) as "adversarioPlacar"
        from placarquarto pq join quarto q on q.id_quarto=pq.quarto_id
        left join lateral (select max(pq2.placar_acumulado) as placar_acumulado from placarquarto pq2
        where pq2.quarto_id=q.id_quarto and pq2.partida_time_id<>pq.partida_time_id) op on true
        where pq.partida_time_id=:id order by q.numero
        """,
        Map.of("id", partidaTime));
  }
}
