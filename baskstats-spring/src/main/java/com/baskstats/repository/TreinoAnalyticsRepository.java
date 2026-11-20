
package com.baskstats.repository;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TreinoAnalyticsRepository {

    private final JdbcClient db;

    public TreinoAnalyticsRepository(JdbcClient db) {
        this.db = db;
    }

    public List<Map<String, Object>> metricasTemporada(
            int timeId,
            String posicao) {

        String filtroPosicao =
                (posicao == null
                        || posicao.isBlank()
                        || posicao.equalsIgnoreCase("TODAS"))
                        ? ""
                        : " AND upper(coalesce(j.posicao, '')) = upper(:posicao) ";

        String sql = """
                WITH ultima_temp AS (
                    SELECT max(e.temporada_id) AS temp_id
                    FROM elenco e
                    WHERE e.time_id = :timeId
                ),
                stats_jogadora AS (
                    SELECT
                        s.jogadora_id,

                        count(s.id_estatistica_jogadora) AS jogos,

                        round(coalesce(avg(s.pontos_feitos), 0), 1) AS ppj,
                        round(coalesce(avg(s.rebote_total), 0), 1) AS reb,
                        round(coalesce(avg(s.assistencias), 0), 1) AS ast,
                        round(coalesce(avg(s.roubos_bola), 0), 1) AS roubos,
                        round(coalesce(avg(s.turnovers), 0), 1) AS turnovers,
                        round(coalesce(avg(s.eficiencia), 0), 1) AS eficiencia,

                        round(
                            coalesce(
                                avg(
                                    CASE
                                        WHEN s.tempo_jogado ~ '^[0-9]{1,3}:[0-5][0-9]$'
                                        THEN split_part(s.tempo_jogado, ':', 1)::numeric
                                             + split_part(s.tempo_jogado, ':', 2)::numeric / 60
                                        ELSE NULL
                                    END
                                ),
                                0
                            ),
                            1
                        ) AS minutos,

                        coalesce(sum(s.arremesso_2_feitos), 0) AS p2_feitos,
                        coalesce(sum(s.arremesso_2_tentados), 0) AS p2_tentados,

                        round(
                            coalesce(
                                100.0 * sum(s.arremesso_2_feitos)
                                / nullif(sum(s.arremesso_2_tentados), 0),
                                0
                            ),
                            1
                        ) AS arremesso2,

                        coalesce(sum(s.arremesso_3_feitos), 0) AS p3_feitos,
                        coalesce(sum(s.arremesso_3_tentados), 0) AS p3_tentados,

                        round(
                            coalesce(
                                100.0 * sum(s.arremesso_3_feitos)
                                / nullif(sum(s.arremesso_3_tentados), 0),
                                0
                            ),
                            1
                        ) AS arremesso3,

                        coalesce(sum(s.lance_livre_feitos), 0) AS ll_feitos,
                        coalesce(sum(s.lance_livre_tentados), 0) AS ll_tentados,

                        round(
                            coalesce(
                                100.0 * sum(s.lance_livre_feitos)
                                / nullif(sum(s.lance_livre_tentados), 0),
                                0
                            ),
                            1
                        ) AS "lanceLivre"

                    FROM estatisticajogadorapartida s

                    JOIN partida_time pt
                        ON pt.id_partida_time = s.partida_time_id

                    JOIN partida p
                        ON p.id_partida = pt.partida_id

                    CROSS JOIN ultima_temp ut

                    WHERE pt.time_id = :timeId
                      AND p.temporada_id = ut.temp_id

                    GROUP BY s.jogadora_id
                )

                SELECT
                    j.id_jogadora AS id,
                    j.nome,
                    coalesce(j.posicao, 'Não informada') AS posicao,
                    pos.numero_camisa AS numero,

                    coalesce(st.jogos, 0) AS jogos,
                    coalesce(st.minutos, 0) AS minutos,
                    coalesce(st.ppj, 0) AS ppj,
                    coalesce(st.reb, 0) AS reb,
                    coalesce(st.ast, 0) AS ast,
                    coalesce(st.roubos, 0) AS roubos,
                    coalesce(st.turnovers, 0) AS turnovers,
                    coalesce(st.eficiencia, 0) AS eficiencia,

                    coalesce(st.arremesso2, 0) AS arremesso2,
                    coalesce(st.arremesso3, 0) AS arremesso3,
                    coalesce(st."lanceLivre", 0) AS "lanceLivre",

                    coalesce(st.p2_tentados, 0) AS p2_tentados,
                    coalesce(st.p3_tentados, 0) AS p3_tentados,
                    coalesce(st.ll_tentados, 0) AS ll_tentados

                FROM jogadora j

                JOIN LATERAL (
                    SELECT
                        e.numero_camisa,
                        e.temporada_id
                    FROM elenco e
                    CROSS JOIN ultima_temp ut
                    WHERE e.jogadora_id = j.id_jogadora
                      AND e.time_id = :timeId
                      AND e.temporada_id = ut.temp_id
                    ORDER BY e.id_elenco DESC
                    LIMIT 1
                ) pos ON true

                LEFT JOIN stats_jogadora st
                    ON st.jogadora_id = j.id_jogadora

                WHERE 1 = 1
                """ + filtroPosicao + """
                ORDER BY j.nome
                """;

        var spec = db.sql(sql).param("timeId", timeId);

        if (!filtroPosicao.isBlank()) {
            spec = spec.param("posicao", posicao.trim());
        }

        return spec.query().listOfRows();
    }

    public List<Map<String, Object>> metricasRecentes(
            int timeId,
            int limiteJogos) {

        String sql = """
                WITH ultimas_partidas AS (
                    SELECT pt.id_partida_time
                    FROM partida_time pt
                    JOIN partida p
                        ON p.id_partida = pt.partida_id
                    WHERE pt.time_id = :timeId
                      AND pt.placar_final IS NOT NULL
                    ORDER BY p.data_hora DESC NULLS LAST,
                             p.id_partida DESC
                    LIMIT :limiteJogos
                )

                SELECT
                    s.jogadora_id AS id,
                    count(s.id_estatistica_jogadora) AS jogos_recentes,

                    round(coalesce(avg(s.pontos_feitos), 0), 1) AS ppj_recente,
                    round(coalesce(avg(s.rebote_total), 0), 1) AS reb_recente,
                    round(coalesce(avg(s.assistencias), 0), 1) AS ast_recente,
                    round(coalesce(avg(s.roubos_bola), 0), 1) AS roubos_recente,
                    round(coalesce(avg(s.turnovers), 0), 1) AS turnovers_recente,
                    round(coalesce(avg(s.eficiencia), 0), 1) AS eficiencia_recente,

                    round(
                        coalesce(
                            avg(
                                CASE
                                    WHEN s.tempo_jogado ~ '^[0-9]{1,3}:[0-5][0-9]$'
                                    THEN split_part(s.tempo_jogado, ':', 1)::numeric
                                         + split_part(s.tempo_jogado, ':', 2)::numeric / 60
                                    ELSE NULL
                                END
                            ),
                            0
                        ),
                        1
                    ) AS minutos_recente,

                    round(
                        coalesce(
                            100.0 * sum(s.arremesso_2_feitos)
                            / nullif(sum(s.arremesso_2_tentados), 0),
                            0
                        ),
                        1
                    ) AS arremesso2_recente,

                    round(
                        coalesce(
                            100.0 * sum(s.arremesso_3_feitos)
                            / nullif(sum(s.arremesso_3_tentados), 0),
                            0
                        ),
                        1
                    ) AS arremesso3_recente,

                    round(
                        coalesce(
                            100.0 * sum(s.lance_livre_feitos)
                            / nullif(sum(s.lance_livre_tentados), 0),
                            0
                        ),
                        1
                    ) AS "lanceLivre_recente"

                FROM estatisticajogadorapartida s

                JOIN ultimas_partidas up
                    ON up.id_partida_time = s.partida_time_id

                GROUP BY s.jogadora_id
                """;

        return db.sql(sql)
                .param("timeId", timeId)
                .param("limiteJogos", Math.max(1, Math.min(limiteJogos, 30)))
                .query()
                .listOfRows();
    }
}
