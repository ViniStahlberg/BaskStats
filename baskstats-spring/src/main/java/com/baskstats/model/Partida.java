package com.time.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "partida")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partida")
    private Integer idPartida;

    @Column(name = "temporada_id", nullable = false)
    private Integer temporadaId;

    @Column(name = "confronto", length = 255)
    private String confronto;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "local", length = 150)
    private String local;

    @Column(name = "arbitros", length = 255)
    private String arbitros;

    @Column(name = "link_video")
    private String linkVideo;

    public Partida() {
    }

    public Integer getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(Integer idPartida) {
        this.idPartida = idPartida;
    }

    public Integer getTemporadaId() {
        return temporadaId;
    }

    public void setTemporadaId(Integer temporadaId) {
        this.temporadaId = temporadaId;
    }

    public String getConfronto() {
        return confronto;
    }

    public void setConfronto(String confronto) {
        this.confronto = confronto;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getArbitros() {
        return arbitros;
    }

    public void setArbitros(String arbitros) {
        this.arbitros = arbitros;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }
}