package com.baskstats.model;

import jakarta.persistence.*;

@Entity
@Table(name = "partida_time", schema = "lbf")
public class PartidaTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public PartidaTime() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
}