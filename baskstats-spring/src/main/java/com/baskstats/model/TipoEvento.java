package com.baskstats.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipoevento", schema = "lbf")
public class TipoEvento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public TipoEvento() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
}