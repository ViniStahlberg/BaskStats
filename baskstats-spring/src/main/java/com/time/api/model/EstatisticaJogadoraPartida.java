package com.time.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "EstatisticaJogadoraPartida")
public class EstatisticaJogadoraPartida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estatistica_jogadora")
    private Integer idEstatisticaJogadora;

    @ManyToOne
    @JoinColumn(name = "partida_time_id", nullable = false)
    private PartidaTime partidaTime;

    @ManyToOne
    @JoinColumn(name = "jogadora_id", nullable = false)
    private Jogadora jogadora;

    @Column(name = "numero_camisa")
    private Integer numeroCamisa;

    @Column(name = "tempo_jogado", length = 10)
    private String tempoJogado;

    @Column(name = "pontos_feitos")
    private Integer pontosFeitos;

    @Column(name = "pontos_tentados")
    private Integer pontosTentados;

    // Demais colunas da tabela (arremesso_3_*, arremesso_2_*, lance_livre_*,
    // rebotes, assistencias, roubos_bola, turnovers, faltas, mais_menos,
    // eficiencia...) não foram mapeadas ainda porque nenhuma rota atual
    // precisa delas. Adicione seguindo o mesmo padrão quando necessário
    // (ex: rota de "ficha técnica" de uma jogadora numa partida).

    public EstatisticaJogadoraPartida() {
    }

    public Integer getIdEstatisticaJogadora() {
        return idEstatisticaJogadora;
    }

    public void setIdEstatisticaJogadora(Integer idEstatisticaJogadora) {
        this.idEstatisticaJogadora = idEstatisticaJogadora;
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

    public Integer getNumeroCamisa() {
        return numeroCamisa;
    }

    public void setNumeroCamisa(Integer numeroCamisa) {
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
}
