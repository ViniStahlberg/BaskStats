package com.time.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "jogadora", indexes = @Index(name = "ix_jogadora_nome", columnList = "nome"))
public class Jogadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jogadora")
    private Integer idJogadora;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "peso", precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(name = "altura", precision = 4, scale = 2)
    private BigDecimal altura;

    @Column(name = "envergadura", precision = 4, scale = 2)
    private BigDecimal envergadura;

    @Column(name = "data_ultima_medicao")
    private LocalDate dataUltimaMedicao;

    // Construtores
    public Jogadora() {
    }

    public Jogadora(String nome, BigDecimal peso, BigDecimal altura, 
                    BigDecimal envergadura, LocalDate dataUltimaMedicao) {
        this.nome = nome;
        this.peso = peso;
        this.altura = altura;
        this.envergadura = envergadura;
        this.dataUltimaMedicao = dataUltimaMedicao;
    }

    // Getters e Setters
    public Integer getIdJogadora() {
        return idJogadora;
    }

    public void setIdJogadora(Integer idJogadora) {
        this.idJogadora = idJogadora;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public BigDecimal getAltura() {
        return altura;
    }

    public void setAltura(BigDecimal altura) {
        this.altura = altura;
    }

    public BigDecimal getEnvergadura() {
        return envergadura;
    }

    public void setEnvergadura(BigDecimal envergadura) {
        this.envergadura = envergadura;
    }

    public LocalDate getDataUltimaMedicao() {
        return dataUltimaMedicao;
    }

    public void setDataUltimaMedicao(LocalDate dataUltimaMedicao) {
        this.dataUltimaMedicao = dataUltimaMedicao;
    }

    @Override
    public String toString() {
        return "Jogadora{" +
                "idJogadora=" + idJogadora +
                ", nome='" + nome + '\'' +
                ", peso=" + peso +
                ", altura=" + altura +
                ", envergadura=" + envergadura +
                ", dataUltimaMedicao=" + dataUltimaMedicao +
                '}';
    }
}
