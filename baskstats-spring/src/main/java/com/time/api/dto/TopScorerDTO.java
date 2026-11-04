package com.time.api.dto;

public class TopScorerDTO {

    private Integer idJogadora;
    private String nome;
    private Long totalPontos;
    private Long totalJogos;

    public TopScorerDTO(Integer idJogadora, String nome, Long totalPontos, Long totalJogos) {
        this.idJogadora = idJogadora;
        this.nome = nome;
        this.totalPontos = totalPontos;
        this.totalJogos = totalJogos;
    }

    public Integer getIdJogadora() {
        return idJogadora;
    }

    public String getNome() {
        return nome;
    }

    public Long getTotalPontos() {
        return totalPontos;
    }

    public Long getTotalJogos() {
        return totalJogos;
    }
}
