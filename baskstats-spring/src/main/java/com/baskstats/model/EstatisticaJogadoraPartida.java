package com.baskstats.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "estatisticajogadorapartida", schema = "lbf")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EstatisticaJogadoraPartida {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_estatistica_jogadora")
  private Integer idEstatisticaJogadora;

  @Column(name = "partida_time_id", nullable = false)
  private Integer partidaTimeId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "jogadora_id", nullable = false)
  private Jogadora jogadora;

  @Column(name = "numero_camisa")
  private Short numeroCamisa;

  @Column(name = "tempo_jogado", length = 10)
  private String tempoJogado;

  @Column(name = "pontos_feitos")
  private Integer pontosFeitos;

  @Column(name = "pontos_tentados")
  private Integer pontosTentados;

  @Column(name = "arremesso_3_feitos")
  private Short arremesso3Feitos;

  @Column(name = "arremesso_3_tentados")
  private Short arremesso3Tentados;

  @Column(name = "arremesso_2_feitos")
  private Short arremesso2Feitos;

  @Column(name = "arremesso_2_tentados")
  private Short arremesso2Tentados;

  @Column(name = "lance_livre_feitos")
  private Short lanceLivreFeitos;

  @Column(name = "lance_livre_tentados")
  private Short lanceLivreTentados;

  @Column(name = "rebote_ofensivo")
  private Short reboteOfensivo;

  @Column(name = "rebote_defensivo")
  private Short reboteDefensivo;

  @Column(name = "rebote_total")
  private Short reboteTotal;

  @Column(name = "assistencias")
  private Short assistencias;

  @Column(name = "roubos_bola")
  private Short roubosBola;

  @Column(name = "turnovers")
  private Short turnovers;

  @Column(name = "faltas_cometidas")
  private Short faltasCometidas;

  @Column(name = "faltas_recebidas")
  private Short faltasRecebidas;

  @Column(name = "mais_menos")
  private Integer maisMenos;

  @Column(name = "eficiencia")
  private Integer eficiencia;

  public EstatisticaJogadoraPartida() {}

  public Integer getIdEstatisticaJogadora() {
    return idEstatisticaJogadora;
  }

  public void setIdEstatisticaJogadora(Integer idEstatisticaJogadora) {
    this.idEstatisticaJogadora = idEstatisticaJogadora;
  }

  public Integer getPartidaTimeId() {
    return partidaTimeId;
  }

  public void setPartidaTimeId(Integer partidaTimeId) {
    this.partidaTimeId = partidaTimeId;
  }

  public Jogadora getJogadora() {
    return jogadora;
  }

  public void setJogadora(Jogadora jogadora) {
    this.jogadora = jogadora;
  }

  public Short getNumeroCamisa() {
    return numeroCamisa;
  }

  public void setNumeroCamisa(Short numeroCamisa) {
    this.numeroCamisa = numeroCamisa;
  }

  public String getTempoJogado() {
    return tempoJogado;
  }

  public void setTempoJogado(String tempoJogado) {
    this.tempoJogado = tempoJogado;
  }

  public Integer getPontosFeitos() {
    return pontosFeitos;
  }

  public void setPontosFeitos(Integer pontosFeitos) {
    this.pontosFeitos = pontosFeitos;
  }

  public Integer getPontosTentados() {
    return pontosTentados;
  }

  public void setPontosTentados(Integer pontosTentados) {
    this.pontosTentados = pontosTentados;
  }

  public Short getArremesso3Feitos() {
    return arremesso3Feitos;
  }

  public void setArremesso3Feitos(Short arremesso3Feitos) {
    this.arremesso3Feitos = arremesso3Feitos;
  }

  public Short getArremesso3Tentados() {
    return arremesso3Tentados;
  }

  public void setArremesso3Tentados(Short arremesso3Tentados) {
    this.arremesso3Tentados = arremesso3Tentados;
  }

  public Short getArremesso2Feitos() {
    return arremesso2Feitos;
  }

  public void setArremesso2Feitos(Short arremesso2Feitos) {
    this.arremesso2Feitos = arremesso2Feitos;
  }

  public Short getArremesso2Tentados() {
    return arremesso2Tentados;
  }

  public void setArremesso2Tentados(Short arremesso2Tentados) {
    this.arremesso2Tentados = arremesso2Tentados;
  }

  public Short getLanceLivreFeitos() {
    return lanceLivreFeitos;
  }

  public void setLanceLivreFeitos(Short lanceLivreFeitos) {
    this.lanceLivreFeitos = lanceLivreFeitos;
  }

  public Short getLanceLivreTentados() {
    return lanceLivreTentados;
  }

  public void setLanceLivreTentados(Short lanceLivreTentados) {
    this.lanceLivreTentados = lanceLivreTentados;
  }

  public Short getReboteOfensivo() {
    return reboteOfensivo;
  }

  public void setReboteOfensivo(Short reboteOfensivo) {
    this.reboteOfensivo = reboteOfensivo;
  }

  public Short getReboteDefensivo() {
    return reboteDefensivo;
  }

  public void setReboteDefensivo(Short reboteDefensivo) {
    this.reboteDefensivo = reboteDefensivo;
  }

  public Short getReboteTotal() {
    return reboteTotal;
  }

  public void setReboteTotal(Short reboteTotal) {
    this.reboteTotal = reboteTotal;
  }

  public Short getAssistencias() {
    return assistencias;
  }

  public void setAssistencias(Short assistencias) {
    this.assistencias = assistencias;
  }

  public Short getRoubosBola() {
    return roubosBola;
  }

  public void setRoubosBola(Short roubosBola) {
    this.roubosBola = roubosBola;
  }

  public Short getTurnovers() {
    return turnovers;
  }

  public void setTurnovers(Short turnovers) {
    this.turnovers = turnovers;
  }

  public Short getFaltasCometidas() {
    return faltasCometidas;
  }

  public void setFaltasCometidas(Short faltasCometidas) {
    this.faltasCometidas = faltasCometidas;
  }

  public Short getFaltasRecebidas() {
    return faltasRecebidas;
  }

  public void setFaltasRecebidas(Short faltasRecebidas) {
    this.faltasRecebidas = faltasRecebidas;
  }

  public Integer getMaisMenos() {
    return maisMenos;
  }

  public void setMaisMenos(Integer maisMenos) {
    this.maisMenos = maisMenos;
  }

  public Integer getEficiencia() {
    return eficiencia;
  }

  public void setEficiencia(Integer eficiencia) {
    this.eficiencia = eficiencia;
  }
}
