package com.time.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lance")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lance")
    private Integer idLance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quarto_id", nullable = false)
    private Quarto quarto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partida_time_id")
    private PartidaTime partidaTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jogadora_id")
    private Jogadora jogadora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_evento_id", nullable = false)
    private TipoEvento tipoEvento;

    @Column(name = "clock", length = 5)
    private String clock;

    @Column(name = "segundos_decorridos_quarto")
    private Short segundosDecorridosQuarto;

    @Column(name = "timestamp_video_segundos", precision = 8, scale = 1)
    private BigDecimal timestampVideoSegundos;

    @Column(name = "ordem_no_quarto")
    private Short ordemNoQuarto;

    @Column(name = "descricao_original", length = 500)
    private String descricaoOriginal;

    @Column(name = "jogador_feitos")
    private Byte jogadorFeitos;

    @Column(name = "jogador_tentados")
    private Byte jogadorTentados;

    @Column(name = "equipe_feitos")
    private Byte equipeFeitos;

    @Column(name = "equipe_tentados")
    private Byte equipeTentados;

    // Construtores
    public Lance() {
    }

    public Lance(Quarto quarto, PartidaTime partidaTime, Jogadora jogadora,
                 TipoEvento tipoEvento, String clock, Short segundosDecorridosQuarto,
                 BigDecimal timestampVideoSegundos, Short ordemNoQuarto,
                 String descricaoOriginal, Byte jogadorFeitos, Byte jogadorTentados,
                 Byte equipeFeitos, Byte equipeTentados) {
        this.quarto = quarto;
        this.partidaTime = partidaTime;
        this.jogadora = jogadora;
        this.tipoEvento = tipoEvento;
        this.clock = clock;
        this.segundosDecorridosQuarto = segundosDecorridosQuarto;
        this.timestampVideoSegundos = timestampVideoSegundos;
        this.ordemNoQuarto = ordemNoQuarto;
        this.descricaoOriginal = descricaoOriginal;
        this.jogadorFeitos = jogadorFeitos;
        this.jogadorTentados = jogadorTentados;
        this.equipeFeitos = equipeFeitos;
        this.equipeTentados = equipeTentados;
    }

    // Getters e Setters
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

    public Short getSegundosDecorridosQuarto() {
        return segundosDecorridosQuarto;
    }

    public void setSegundosDecorridosQuarto(Short segundosDecorridosQuarto) {
        this.segundosDecorridosQuarto = segundosDecorridosQuarto;
    }

    public BigDecimal getTimestampVideoSegundos() {
        return timestampVideoSegundos;
    }

    public void setTimestampVideoSegundos(BigDecimal timestampVideoSegundos) {
        this.timestampVideoSegundos = timestampVideoSegundos;
    }

    public Short getOrdemNoQuarto() {
        return ordemNoQuarto;
    }

    public void setOrdemNoQuarto(Short ordemNoQuarto) {
        this.ordemNoQuarto = ordemNoQuarto;
    }

    public String getDescricaoOriginal() {
        return descricaoOriginal;
    }

    public void setDescricaoOriginal(String descricaoOriginal) {
        this.descricaoOriginal = descricaoOriginal;
    }

    public Byte getJogadorFeitos() {
        return jogadorFeitos;
    }

    public void setJogadorFeitos(Byte jogadorFeitos) {
        this.jogadorFeitos = jogadorFeitos;
    }

    public Byte getJogadorTentados() {
        return jogadorTentados;
    }

    public void setJogadorTentados(Byte jogadorTentados) {
        this.jogadorTentados = jogadorTentados;
    }

    public Byte getEquipeFeitos() {
        return equipeFeitos;
    }

    public void setEquipeFeitos(Byte equipeFeitos) {
        this.equipeFeitos = equipeFeitos;
    }

    public Byte getEquipeTentados() {
        return equipeTentados;
    }

    public void setEquipeTentados(Byte equipeTentados) {
        this.equipeTentados = equipeTentados;
    }

    @Override
    public String toString() {
        return "Lance{" +
                "idLance=" + idLance +
                ", clock='" + clock + '\'' +
                ", segundosDecorridosQuarto=" + segundosDecorridosQuarto +
                ", timestampVideoSegundos=" + timestampVideoSegundos +
                ", ordemNoQuarto=" + ordemNoQuarto +
                ", descricaoOriginal='" + descricaoOriginal + '\'' +
                ", jogadorFeitos=" + jogadorFeitos +
                ", jogadorTentados=" + jogadorTentados +
                ", equipeFeitos=" + equipeFeitos +
                ", equipeTentados=" + equipeTentados +
                '}';
    }
}