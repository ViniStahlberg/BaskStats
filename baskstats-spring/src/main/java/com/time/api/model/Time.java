package com.time.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Time")
public class Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_time")
    private Integer idTime;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "sigla", nullable = false, length = 10)
    private String sigla;

    public Time() {
    }

    public Integer getIdTime() {
        return idTime;
    }

    public void setIdTime(Integer idTime) {
        this.idTime = idTime;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}
