package com.baskstats.model;

import jakarta.persistence.*;

@Entity
@Table(name = "quarto", schema = "lbf")
public class Quarto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Quarto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
}