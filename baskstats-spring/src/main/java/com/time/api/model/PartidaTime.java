package com.time.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Partida_Time")
public class PartidaTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partida_time")
    private Integer idPartidaTime;

    @ManyToOne
    @JoinColumn(name = "time_id", nullable = false)
    private Time time;

    @Column(name = "tecnico", length = 150)
    private String tecnico;

    @Column(name = "placar_final")
    private Integer placarFinal;

    public PartidaTime() {
    }

    public Integer getIdPartidaTime() {
        return idPartidaTime;
    }

    public void setIdPartidaTime(Integer idPartidaTime) {
        this.idPartidaTime = idPartidaTime;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public Integer getPlacarFinal() {
        return placarFinal;
    }

    public void setPlacarFinal(Integer placarFinal) {
        this.placarFinal = placarFinal;
    }
}
