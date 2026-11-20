
package com.baskstats.service;

import com.baskstats.repository.TreinoAnalyticsRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TreinoAnalyticsService {

    private final TreinoAnalyticsRepository repository;

    public TreinoAnalyticsService(TreinoAnalyticsRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> gerarPlanoTreino(
            int timeId,
            String foco,
            BigDecimal metaInformada,
            int ultimosJogos,
            String posicao) {

        String chaveMetrica = normalizarFoco(foco);
        boolean menorMelhor = "turnovers".equals(chaveMetrica);

        int jogosAnalisados = Math.max(1, Math.min(ultimosJogos, 30));

        List<Map<String, Object>> base =
                repository.metricasTemporada(timeId, posicao);

        Map<Object, Map<String, Object>> recentesPorId =
                repository.metricasRecentes(timeId, jogosAnalisados)
                        .stream()
                        .collect(Collectors.toMap(
                                row -> row.get("id"),
                                row -> row,
                                (a, b) -> a
                        ));

        BigDecimal meta = metaInformada != null
                ? metaInformada
                : metaPadrao(chaveMetrica);

        List<Map<String, Object>> atletas = new ArrayList<>();

        for (Map<String, Object> row : base) {

            Map<String, Object> item = new LinkedHashMap<>(row);

            Map<String, Object> recente = recentesPorId.getOrDefault(
                    row.get("id"),
                    Map.of()
            );

            BigDecimal valorAtual = toBigDecimal(row.get(chaveMetrica));

            String chaveRecente = chaveMetrica + "_recente";

            BigDecimal valorRecente =
                    recente.get(chaveRecente) != null
                            ? toBigDecimal(recente.get(chaveRecente))
                            : valorAtual;

            // Gap positivo representa o quanto falta para atingir a meta.
            // Para turnovers, o objetivo é reduzir o valor.
            BigDecimal gapParaMeta = menorMelhor
                    ? valorAtual.subtract(meta)
                    .setScale(1, RoundingMode.HALF_UP)
                    : meta.subtract(valorAtual)
                    .setScale(1, RoundingMode.HALF_UP);

            BigDecimal deltaRecente = valorRecente
                    .subtract(valorAtual)
                    .setScale(1, RoundingMode.HALF_UP);

            String statusMeta = gapParaMeta.compareTo(BigDecimal.ZERO) <= 0
                    ? "ATINGIDA"
                    : "EM_FOCO";

            item.put("metricaFoco", chaveMetrica);
            item.put("meta", meta);
            item.put("valorAtual", valorAtual);
            item.put("valorRecente", valorRecente);
            item.put("deltaRecente", deltaRecente);
            item.put("gapParaMeta", gapParaMeta);
            item.put("statusMeta", statusMeta);

            item.put(
                    "recomendacaoTreino",
                    montarRecomendacao(
                            chaveMetrica,
                            valorAtual,
                            gapParaMeta,
                            row
                    )
            );

            atletas.add(item);
        }

        // Atletas com maior distância da meta aparecem primeiro.
        atletas.sort((a, b) ->
                toBigDecimal(b.get("gapParaMeta"))
                        .compareTo(toBigDecimal(a.get("gapParaMeta")))
        );

        long atletasEmFoco = atletas.stream()
                .filter(a -> "EM_FOCO".equals(a.get("statusMeta")))
                .count();

        Map<String, Object> resultado = new LinkedHashMap<>();

        resultado.put("timeId", timeId);
        resultado.put("foco", chaveMetrica);
        resultado.put("meta", meta);
        resultado.put("ultimosJogos", jogosAnalisados);
        resultado.put("totalAtletas", atletas.size());
        resultado.put("atletasEmFoco", atletasEmFoco);
        resultado.put("atletas", atletas);

        return resultado;
    }

    private String normalizarFoco(String foco) {

        if (foco == null || foco.isBlank()) {
            return "arremesso3";
        }

        return switch (foco.trim().toLowerCase(Locale.ROOT)) {
            case "arremesso2", "2p", "dois" -> "arremesso2";
            case "arremesso3", "3p", "tres", "três" -> "arremesso3";
            case "lancelivre", "lance_livre", "ll" -> "lanceLivre";
            case "ppj", "pontos" -> "ppj";
            case "reb", "rebotes", "rebote_total" -> "reb";
            case "ast", "assistencias", "assistência" -> "ast";
            case "turnovers", "to", "desperdicios", "desperdícios" -> "turnovers";
            case "eficiencia", "eff", "eficiência" -> "eficiencia";
            case "minutos", "min" -> "minutos";
            case "roubos", "steals" -> "roubos";
            default -> "arremesso3";
        };
    }

    private BigDecimal metaPadrao(String metrica) {

        return switch (metrica) {
            case "arremesso3" -> BigDecimal.valueOf(35.0);
            case "arremesso2" -> BigDecimal.valueOf(50.0);
            case "lanceLivre" -> BigDecimal.valueOf(75.0);
            case "turnovers" -> BigDecimal.valueOf(2.0);
            case "eficiencia" -> BigDecimal.valueOf(12.0);
            case "reb" -> BigDecimal.valueOf(6.0);
            case "ast" -> BigDecimal.valueOf(4.0);
            case "ppj" -> BigDecimal.valueOf(12.0);
            case "minutos" -> BigDecimal.valueOf(20.0);
            case "roubos" -> BigDecimal.valueOf(1.0);
            default -> BigDecimal.valueOf(20.0);
        };
    }

    private String montarRecomendacao(
            String metrica,
            BigDecimal atual,
            BigDecimal gap,
            Map<String, Object> row) {

        if (gap.compareTo(BigDecimal.ZERO) <= 0) {
            return "Meta atingida. Manter consistência e trabalhar situações de jogo, tomada de decisão e pressão.";
        }

        String posicao = String.valueOf(row.get("posicao"));

        return switch (metrica) {

            case "arremesso3" ->
                    "Prioridade: arremesso de 3 pontos. Trabalhar catch-and-shoot, "
                    + "arremessos em movimento, deslocamento sem bola e execução sob fadiga.";

            case "arremesso2" ->
                    "Prioridade: arremessos de 2 pontos. Trabalhar finalizações após drible, "
                    + "cortes para a cesta, pull-up e finalizações próximas ao aro.";

            case "lanceLivre" ->
                    "Prioridade: lance livre. Realizar séries com rotina padronizada, "
                    + "controle de execução e simulações de pressão ao final dos treinos.";

            case "turnovers" ->
                    "Prioridade: redução de desperdícios de bola. Trabalhar proteção de bola, "
                    + "passes sob pressão, leitura de marcação dupla e tomada de decisão.";

            case "eficiencia" ->
                    "Prioridade: eficiência geral. Melhorar seleção de arremessos, "
                    + "aproveitamento, rebotes e redução de erros não forçados.";

            case "reb" ->
                    "Prioridade: rebotes. Trabalhar posicionamento, box out, leitura da trajetória "
                    + "da bola, impulsão e disputa de rebotes ofensivos e defensivos.";

            case "ast" ->
                    "Prioridade: assistências. Trabalhar visão de jogo, passe em movimento, "
                    + "pick-and-roll, leitura de defesa e criação de oportunidades.";

            case "ppj" ->
                    "Prioridade: pontuação. Trabalhar variedade de finalizações, "
                    + "movimentação sem bola, criação de espaço e decisões ofensivas.";

            case "minutos" ->
                    "Prioridade: participação em quadra. Avaliar condicionamento, consistência, "
                    + "defesa, tomada de decisão e contribuição tática para ganhar mais minutos.";

            case "roubos" ->
                    "Prioridade: roubos de bola. Trabalhar antecipação, linhas de passe, "
                    + "posicionamento defensivo, mãos ativas e leitura das ações adversárias.";

            default ->
                    "Trabalhar o fundamento " + metrica
                    + " com exercícios específicos para a posição " + posicao + ".";
        };
    }

    private BigDecimal toBigDecimal(Object valor) {

        if (valor == null) {
            return BigDecimal.ZERO;
        }

        if (valor instanceof BigDecimal bd) {
            return bd;
        }

        if (valor instanceof Number num) {
            return BigDecimal.valueOf(num.doubleValue());
        }

        try {
            return new BigDecimal(valor.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
