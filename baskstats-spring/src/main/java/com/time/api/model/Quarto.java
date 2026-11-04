package com.time.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Quarto")
public class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_quarto")
    private Integer idQuarto;

    // @JsonIgnore: evita serializar a Partida inteira dentro de cada Lance
    // (ela já é redundante, pois o id da partida já está na URL da rota).
    // O relacionamento continua funcionando normalmente nas queries JPQL.
    @ManyToOne
    @JoinColumn(name = "partida_id", nullable = false)
    @JsonIgnore
    private Partida partida;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "video_inicio_segundos", precision = 8, scale = 1)
    private BigDecimal videoInicioSegundos;

    @Column(name = "video_fim_segundos", precision = 8, scale = 1)
    private BigDecimal videoFimSegundos;

    public Quarto() {
    }

    public Integer getIdQuarto() {
        return idQuarto;
    }

    public void setIdQuarto(Integer idQuarto) {
        this.idQuarto = idQuarto;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public BigDecimal getVideoInicioSegundos() {
        return videoInicioSegundos;
    }

    public void setVideoInicioSegundos(BigDecimal videoInicioSegundos) {
        this.videoInicioSegundos = videoInicioSegundos;
    }

    public BigDecimal getVideoFimSegundos() {
        return videoFimSegundos;
    }

    public void setVideoFimSegundos(BigDecimal videoFimSegundos) {
        this.videoFimSegundos = videoFimSegundos;
    }
}
