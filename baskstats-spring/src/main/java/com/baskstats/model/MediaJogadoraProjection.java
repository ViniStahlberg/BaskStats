package com.baskstats.model;

/**
 * Projeção usada só pelo resultado da consulta de médias (AVG/COUNT) —
 * não é uma entidade, é a "forma" do retorno agregado do banco.
 */
public interface MediaJogadoraProjection {
    Double getMediaPontos();
    Double getMediaRebotes();
    Double getMediaAssistencias();
    Long getTotalPartidas();
}