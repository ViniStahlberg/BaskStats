package com.time.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Lance")
public class Lance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lance")
    private Integer idLance;

    @ManyToOne
    @JoinColumn(name = "quarto_id", nullable = false)
    private Quarto quarto;

    @ManyToOne
    @JoinColumn(name = "partida_time_id")
    private PartidaTime partidaTime;

    @ManyToOne
    @JoinColumn(name = "jogadora_id")
    private Jogadora jogadora;

    @ManyToOne
    @JoinColumn(name = "tipo_evento_id", nullable = false)
    private TipoEvento tipoEvento;

    @Column(name = "clock", length = 5, nullable = false)
    private String clock;

    @Column(name = "segundos_decorridos_quarto", nullable = false)
    private Integer segundosDecorridosQuarto;

    @Column(name = "timestamp_video_segundos", precision = 8, scale = 1)
    private BigDecimal timestampVideoSegundos;

    @Column(name = "ordem_no_quarto", nullable = false)
    private Integer ordemNoQuarto;

    @Column(name = "descricao_original", length = 500, nullable = false)
    private String descricaoOriginal;

    @Column(name = "jogador_feitos")
    private Integer jogadorFeitos;

    @Column(name = "jogador_tentados")
    private Integer jogadorTentados;

    @Column(name = "equipe_feitos")
    private Integer equipeFeitos;

    @Column(name = "equipe_tentados")
    private Integer equipeTentados;

    public Lance() {
    }

    public Integer getIdLance() {
        return idLance;
    }

    public void setIdLance(Integer idLance) {
        this.idLance = idLance;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }

    public PartidaTime getPartidaTime() {
        return partidaTime;
    }

    public void setPartidaTime(PartidaTime partidaTime) {
        this.partidaTime = partidaTime;
    }

    public Jogadora getJogadora() {
        return jogadora;
    }

    public void setJogadora(Jogadora jogadora) {
        this.jogadora = jogadora;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public Integer getSegundosDecorridosQuarto() {
        return segundosDecorridosQuarto;
    }

    public void setSegundosDecorridosQuarto(Integer segundosDecorridosQuarto) {
        this.segundosDecorridosQuarto = segundosDecorridosQuarto;
    }

    public BigDecimal getTimestampVideoSegundos() {
        return timestampVideoSegundos;
    }

    public void setTimestampVideoSegundos(BigDecimal timestampVideoSegundos) {
        this.timestampVideoSegundos = timestampVideoSegundos;
    }

    public Integer getOrdemNoQuarto() {
        return ordemNoQuarto;
    }

    public void setOrdemNoQuarto(Integer ordemNoQuarto) {
        this.ordemNoQuarto = ordemNoQuarto;
    }

    public String getDescricaoOriginal() {
        return descricaoOriginal;
    }

    public void setDescricaoOriginal(String descricaoOriginal) {
        this.descricaoOriginal = descricaoOriginal;
    }

    public Integer getJogadorFeitos() {
        return jogadorFeitos;
    }

    public void setJogadorFeitos(Integer jogadorFeitos) {
        this.jogadorFeitos = jogadorFeitos;
    }

    public Integer getJogadorTentados() {
        return jogadorTentados;
    }

    public void setJogadorTentados(Integer jogadorTentados) {
        this.jogadorTentados = jogadorTentados;
    }

    public Integer getEquipeFeitos() {
        return equipeFeitos;
    }

    public void setEquipeFeitos(Integer equipeFeitos) {
        this.equipeFeitos = equipeFeitos;
    }

    public Integer getEquipeTentados() {
        return equipeTentados;
    }

    public void setEquipeTentados(Integer equipeTentados) {
        this.equipeTentados = equipeTentados;
    }
}
