package com.baskstats.model;

/**
 * Corpo de resposta da rota GET /api/jogadoras/{id}/medias.
 * Record (Java 17) em vez de classe com getters/setters, só pra esse DTO de saída —
 * não guarda estado nem é persistido, então não precisa do padrão das entidades.
 */
public record MediaJogadoraResponse(
        Integer jogadoraId,
        String nomeJogadora,
        Double mediaPontos,
        Double mediaRebotes,
        Double mediaAssistencias,
        Long totalPartidas
) {
}