package com.time.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Elenco")
public class Elenco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_elenco")
    private Integer idElenco;

    @ManyToOne
    @JoinColumn(name = "jogadora_id", nullable = false)
    private Jogadora jogadora;

    @ManyToOne
    @JoinColumn(name = "time_id", nullable = false)
    private Time time;

    // Mantido como campo simples (sem relação) por enquanto, pois nenhuma
    // rota atual precisa dos dados da Temporada em si, apenas do id.
    @Column(name = "temporada_id", nullable = false)
    private Integer temporadaId;

    @Column(name = "numero_camisa")
    private Integer numeroCamisa;

    // Posição é tratada "por elenco" (opção B): a mesma jogadora pode
    // jogar em posições diferentes dependendo do time/temporada.
    // Sem validação de valores por enquanto (ex: Armadora, Ala,
    // Ala-Pivo, Pivo, Pivo-Central) - fica livre como VARCHAR.
    @Column(name = "posicao", length = 20)
    private String posicao;

    public Elenco() {
    }

    public Integer getIdElenco() {
        return idElenco;
    }

    public void setIdElenco(Integer idElenco) {
        this.idElenco = idElenco;
    }

    public Jogadora getJogadora() {
        return jogadora;
    }

    public void setJogadora(Jogadora jogadora) {
        this.jogadora = jogadora;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Integer getTemporadaId() {
        return temporadaId;
    }

    public void setTemporadaId(Integer temporadaId) {
        this.temporadaId = temporadaId;
    }

    public Integer getNumeroCamisa() {
        return numeroCamisa;
    }

    public void setNumeroCamisa(Integer numeroCamisa) {
        this.numeroCamisa = numeroCamisa;
    }
}
