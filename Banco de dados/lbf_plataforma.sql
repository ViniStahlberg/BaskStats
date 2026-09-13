-- =====================================================================
-- Modelo Físico — Plataforma LBF
-- MySQL 8.x | InnoDB | utf8mb4
--
-- Como usar no Workbench:
--   Opção A) File > Run SQL Script... e apontar pra esse arquivo.
--   Opção B) Database > Reverse Engineer... escolhendo esse script,
--            pra já nascer com o EER Diagram desenhado.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS lbf_plataforma
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE lbf_plataforma;

SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- BASE REUTILIZÁVEL
-- ---------------------------------------------------------------------

CREATE TABLE Temporada (
    id_temporada    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(100)    NOT NULL,           -- ex: "Finais LBF 2026"
    ano_inicio      SMALLINT        NOT NULL,
    ano_fim         SMALLINT        NOT NULL,
    UNIQUE KEY uk_temporada_nome (nome)
) ENGINE=InnoDB;

CREATE TABLE Time (
    id_time         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(150)    NOT NULL,
    sigla           VARCHAR(10)     NOT NULL,
    UNIQUE KEY uk_time_sigla (sigla)
) ENGINE=InnoDB;

CREATE TABLE Jogadora (
    id_jogadora         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome                VARCHAR(150)    NOT NULL,
    peso                DECIMAL(5,2)    NULL,           -- kg — cache da medição mais recente
    altura              DECIMAL(4,2)    NULL,           -- metros — cache
    envergadura         DECIMAL(4,2)    NULL,           -- metros — cache
    data_ultima_medicao DATE            NULL,
    INDEX ix_jogadora_nome (nome)
) ENGINE=InnoDB;

CREATE TABLE MedidaFisica (
    id_medida       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    jogadora_id     INT UNSIGNED    NOT NULL,
    data_medicao    DATE            NOT NULL,
    peso            DECIMAL(5,2)    NOT NULL,
    altura          DECIMAL(4,2)    NOT NULL,
    envergadura     DECIMAL(4,2)    NOT NULL,
    CONSTRAINT fk_medida_jogadora FOREIGN KEY (jogadora_id)
        REFERENCES Jogadora(id_jogadora) ON DELETE CASCADE,
    UNIQUE KEY uk_medida_jogadora_data (jogadora_id, data_medicao)
) ENGINE=InnoDB;

CREATE TABLE Elenco (
    id_elenco       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    jogadora_id     INT UNSIGNED    NOT NULL,
    time_id         INT UNSIGNED    NOT NULL,
    temporada_id    INT UNSIGNED    NOT NULL,
    numero_camisa   TINYINT UNSIGNED NULL,
    CONSTRAINT fk_elenco_jogadora  FOREIGN KEY (jogadora_id)  REFERENCES Jogadora(id_jogadora)   ON DELETE CASCADE,
    CONSTRAINT fk_elenco_time      FOREIGN KEY (time_id)      REFERENCES Time(id_time)           ON DELETE CASCADE,
    CONSTRAINT fk_elenco_temporada FOREIGN KEY (temporada_id) REFERENCES Temporada(id_temporada) ON DELETE CASCADE,
    UNIQUE KEY uk_elenco (jogadora_id, time_id, temporada_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- PARTIDA (hub central)
-- ---------------------------------------------------------------------

CREATE TABLE Partida (
    id_partida      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    temporada_id    INT UNSIGNED    NOT NULL,
    confronto       VARCHAR(255)    NULL,               -- texto bruto do scraping
    data_hora       DATETIME        NOT NULL,
    local           VARCHAR(150)    NULL,
    arbitros        VARCHAR(255)    NULL,
    link_video      VARCHAR(500)    NULL,
    CONSTRAINT fk_partida_temporada FOREIGN KEY (temporada_id)
        REFERENCES Temporada(id_temporada) ON DELETE RESTRICT,
    INDEX ix_partida_data (data_hora)
) ENGINE=InnoDB;

CREATE TABLE Partida_Time (
    id_partida_time INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partida_id      INT UNSIGNED    NOT NULL,
    time_id         INT UNSIGNED    NOT NULL,
    tecnico         VARCHAR(150)    NULL,
    placar_final    SMALLINT UNSIGNED NULL,
    CONSTRAINT fk_pt_partida FOREIGN KEY (partida_id) REFERENCES Partida(id_partida) ON DELETE CASCADE,
    CONSTRAINT fk_pt_time    FOREIGN KEY (time_id)    REFERENCES Time(id_time)       ON DELETE RESTRICT,
    UNIQUE KEY uk_partida_time (partida_id, time_id)
) ENGINE=InnoDB;

CREATE TABLE Quarto (
    id_quarto               INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partida_id              INT UNSIGNED NOT NULL,
    numero                  TINYINT UNSIGNED NOT NULL,
    video_inicio_segundos   DECIMAL(8,1) NULL,
    video_fim_segundos      DECIMAL(8,1) NULL,
    CONSTRAINT fk_quarto_partida FOREIGN KEY (partida_id)
        REFERENCES Partida(id_partida) ON DELETE CASCADE,
    UNIQUE KEY uk_quarto_partida_numero (partida_id, numero)
) ENGINE=InnoDB;

CREATE TABLE PlacarQuarto (
    id_placar_quarto   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    quarto_id           INT UNSIGNED NOT NULL,
    partida_time_id      INT UNSIGNED NOT NULL,
    placar_acumulado    SMALLINT UNSIGNED NOT NULL,
    CONSTRAINT fk_pq_quarto       FOREIGN KEY (quarto_id)       REFERENCES Quarto(id_quarto)             ON DELETE CASCADE,
    CONSTRAINT fk_pq_partida_time FOREIGN KEY (partida_time_id) REFERENCES Partida_Time(id_partida_time) ON DELETE CASCADE,
    UNIQUE KEY uk_placar_quarto (quarto_id, partida_time_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- PLAY-BY-PLAY (opcional por partida)
-- ---------------------------------------------------------------------

CREATE TABLE TipoEvento (
    id_tipo_evento  INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(50)     NOT NULL,           -- arremesso_2_convertido, rebote_ofensivo, ...
    descricao       VARCHAR(255)    NULL,
    UNIQUE KEY uk_tipo_evento_nome (nome)
) ENGINE=InnoDB;

CREATE TABLE Lance (
    id_lance                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    quarto_id                   INT UNSIGNED NOT NULL,
    partida_time_id             INT UNSIGNED NULL,       -- nulo em marcador de período
    jogadora_id                 INT UNSIGNED NULL,       -- nulo em lance de time
    tipo_evento_id              INT UNSIGNED NOT NULL,
    clock                       VARCHAR(5)   NOT NULL,   -- "MM:SS"
    segundos_decorridos_quarto  SMALLINT UNSIGNED NOT NULL,
    timestamp_video_segundos    DECIMAL(8,1) NULL,
    ordem_no_quarto             SMALLINT UNSIGNED NOT NULL,
    descricao_original          VARCHAR(500) NOT NULL,
    jogador_feitos               TINYINT UNSIGNED NULL,
    jogador_tentados             TINYINT UNSIGNED NULL,
    equipe_feitos                TINYINT UNSIGNED NULL,
    equipe_tentados              TINYINT UNSIGNED NULL,
    CONSTRAINT fk_lance_quarto       FOREIGN KEY (quarto_id)       REFERENCES Quarto(id_quarto)             ON DELETE CASCADE,
    CONSTRAINT fk_lance_partidatime  FOREIGN KEY (partida_time_id) REFERENCES Partida_Time(id_partida_time) ON DELETE SET NULL,
    CONSTRAINT fk_lance_jogadora     FOREIGN KEY (jogadora_id)     REFERENCES Jogadora(id_jogadora)         ON DELETE SET NULL,
    CONSTRAINT fk_lance_tipoevento   FOREIGN KEY (tipo_evento_id)  REFERENCES TipoEvento(id_tipo_evento)    ON DELETE RESTRICT,
    INDEX ix_lance_quarto_ordem (quarto_id, ordem_no_quarto),
    INDEX ix_lance_timestamp_video (timestamp_video_segundos),
    INDEX ix_lance_tipo_evento (tipo_evento_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- SCOUTING OFICIAL (opcional por partida)
-- ---------------------------------------------------------------------

CREATE TABLE EstatisticaJogadoraPartida (
    id_estatistica_jogadora    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partida_time_id            INT UNSIGNED NOT NULL,
    jogadora_id                INT UNSIGNED NOT NULL,
    numero_camisa               TINYINT UNSIGNED NULL,
    tempo_jogado                VARCHAR(10) NULL,
    pontos_feitos                SMALLINT UNSIGNED NULL,
    pontos_tentados              SMALLINT UNSIGNED NULL,
    arremesso_3_feitos           TINYINT UNSIGNED NULL,
    arremesso_3_tentados         TINYINT UNSIGNED NULL,
    arremesso_2_feitos           TINYINT UNSIGNED NULL,
    arremesso_2_tentados         TINYINT UNSIGNED NULL,
    lance_livre_feitos           TINYINT UNSIGNED NULL,
    lance_livre_tentados         TINYINT UNSIGNED NULL,
    rebote_ofensivo              TINYINT UNSIGNED NULL,
    rebote_defensivo             TINYINT UNSIGNED NULL,
    rebote_total                 TINYINT UNSIGNED NULL,
    assistencias                 TINYINT UNSIGNED NULL,
    roubos_bola                  TINYINT UNSIGNED NULL,
    turnovers                    TINYINT UNSIGNED NULL,
    en                           TINYINT UNSIGNED NULL,   -- ver ressalva no modelo lógico
    faltas_cometidas             TINYINT UNSIGNED NULL,
    faltas_recebidas             TINYINT UNSIGNED NULL,
    er                           TINYINT UNSIGNED NULL,   -- ver ressalva no modelo lógico
    vi                           TINYINT UNSIGNED NULL,   -- ver ressalva no modelo lógico
    mais_menos                   SMALLINT NULL,
    eficiencia                   SMALLINT NULL,
    CONSTRAINT fk_ej_partidatime FOREIGN KEY (partida_time_id) REFERENCES Partida_Time(id_partida_time) ON DELETE CASCADE,
    CONSTRAINT fk_ej_jogadora    FOREIGN KEY (jogadora_id)     REFERENCES Jogadora(id_jogadora)         ON DELETE CASCADE,
    UNIQUE KEY uk_estatistica_jogadora (partida_time_id, jogadora_id),
    INDEX ix_ej_jogadora (jogadora_id)
) ENGINE=InnoDB;

CREATE TABLE EstatisticaTimePartida (
    id_estatistica_time     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partida_time_id         INT UNSIGNED NOT NULL,
    tipo                     ENUM('acoes_coletivas','total') NOT NULL,
    pontos_feitos             SMALLINT UNSIGNED NULL,
    pontos_tentados           SMALLINT UNSIGNED NULL,
    arremesso_3_feitos        TINYINT UNSIGNED NULL,
    arremesso_3_tentados      TINYINT UNSIGNED NULL,
    arremesso_2_feitos        TINYINT UNSIGNED NULL,
    arremesso_2_tentados      TINYINT UNSIGNED NULL,
    lance_livre_feitos        TINYINT UNSIGNED NULL,
    lance_livre_tentados      TINYINT UNSIGNED NULL,
    rebote_ofensivo           TINYINT UNSIGNED NULL,
    rebote_defensivo          TINYINT UNSIGNED NULL,
    rebote_total              TINYINT UNSIGNED NULL,
    assistencias               TINYINT UNSIGNED NULL,
    roubos_bola                 TINYINT UNSIGNED NULL,
    turnovers                   TINYINT UNSIGNED NULL,
    faltas_cometidas            TINYINT UNSIGNED NULL,
    faltas_recebidas            TINYINT UNSIGNED NULL,
    CONSTRAINT fk_et_partidatime FOREIGN KEY (partida_time_id)
        REFERENCES Partida_Time(id_partida_time) ON DELETE CASCADE,
    UNIQUE KEY uk_estatistica_time (partida_time_id, tipo)
) ENGINE=InnoDB;

CREATE TABLE ExtrasPartidaTime (
    id_extras           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partida_time_id     INT UNSIGNED NOT NULL,
    reservas_pontos      SMALLINT UNSIGNED NULL,
    pontos_garrafao       SMALLINT UNSIGNED NULL,
    desqualificados       VARCHAR(255) NULL,
    CONSTRAINT fk_extras_partidatime FOREIGN KEY (partida_time_id)
        REFERENCES Partida_Time(id_partida_time) ON DELETE CASCADE,
    UNIQUE KEY uk_extras_partidatime (partida_time_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- VÍDEO / CLIPES
-- ---------------------------------------------------------------------

CREATE TABLE CategoriaClipe (
    id_categoria_clipe  INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome                 VARCHAR(50) NOT NULL,          -- Defesa / Ataque / Finalizacoes / Rebote_Ofensivo
    descricao             VARCHAR(255) NULL,
    UNIQUE KEY uk_categoria_clipe_nome (nome)
) ENGINE=InnoDB;

CREATE TABLE Clipe (
    id_clipe             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    categoria_clipe_id    INT UNSIGNED NOT NULL,
    partida_id            INT UNSIGNED NOT NULL,        -- denormalizado de propósito
    inicio_segundos        DECIMAL(8,1) NOT NULL,
    fim_segundos            DECIMAL(8,1) NOT NULL,
    duracao_segundos        DECIMAL(8,1) NOT NULL,
    caminho_arquivo          VARCHAR(500) NULL,
    CONSTRAINT fk_clipe_categoria FOREIGN KEY (categoria_clipe_id) REFERENCES CategoriaClipe(id_categoria_clipe) ON DELETE RESTRICT,
    CONSTRAINT fk_clipe_partida   FOREIGN KEY (partida_id)         REFERENCES Partida(id_partida)                ON DELETE CASCADE,
    INDEX ix_clipe_partida_categoria (partida_id, categoria_clipe_id)
) ENGINE=InnoDB;

CREATE TABLE Clipe_Lance (
    clipe_id    INT UNSIGNED NOT NULL,
    lance_id    INT UNSIGNED NOT NULL,
    PRIMARY KEY (clipe_id, lance_id),
    CONSTRAINT fk_cl_clipe FOREIGN KEY (clipe_id) REFERENCES Clipe(id_clipe) ON DELETE CASCADE,
    CONSTRAINT fk_cl_lance FOREIGN KEY (lance_id) REFERENCES Lance(id_lance) ON DELETE CASCADE
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- DADOS DE APOIO (lookup) — popular na criação do banco
-- =====================================================================

INSERT INTO CategoriaClipe (nome, descricao) VALUES
    ('Defesa',          'Ataque do time adversário — o que a defesa enfrenta'),
    ('Ataque',           'Posse ofensiva do time principal, do início ao fim'),
    ('Finalizacoes',     'Arremessos e lances livres do time principal'),
    ('Rebote_Ofensivo',  'Rebotes ofensivos do time principal');

INSERT INTO TipoEvento (nome) VALUES
    ('arremesso_2_convertido'), ('arremesso_2_errado'),
    ('arremesso_3_convertido'), ('arremesso_3_errado'),
    ('lance_livre_convertido'), ('lance_livre_errado'),
    ('rebote_ofensivo'), ('rebote_defensivo'), ('rebote_time'),
    ('assistencia'), ('turnover'), ('recuperacao'), ('bloqueio'),
    ('falta_sofrida'), ('falta_cometida'), ('falta_ofensiva'),
    ('violacao_saida_quadra'), ('violacao_3s_garrafao'),
    ('substituicao_entra'), ('substituicao_sai'), ('pedido_tempo'),
    ('inicio_partida'), ('fim_partida'), ('inicio_quarto'), ('fim_quarto');

-- =====================================================================
-- VIEW — eficiência de arremessos (exemplo já discutido)
-- =====================================================================

CREATE OR REPLACE VIEW vw_eficiencia_arremessos AS
SELECT
    l.jogadora_id,
    SUM(CASE WHEN te.nome IN ('arremesso_2_convertido','arremesso_2_errado') THEN 1 ELSE 0 END) AS tentativas_2pt,
    SUM(CASE WHEN te.nome = 'arremesso_2_convertido' THEN 2 ELSE 0 END)                          AS pontos_2pt,
    SUM(CASE WHEN te.nome IN ('arremesso_3_convertido','arremesso_3_errado') THEN 1 ELSE 0 END) AS tentativas_3pt,
    SUM(CASE WHEN te.nome = 'arremesso_3_convertido' THEN 3 ELSE 0 END)                          AS pontos_3pt
FROM Lance l
JOIN TipoEvento te ON te.id_tipo_evento = l.tipo_evento_id
WHERE l.jogadora_id IS NOT NULL
GROUP BY l.jogadora_id;