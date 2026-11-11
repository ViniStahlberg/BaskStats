package com.baskstats;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BaskstatsSpringApplicationTests {
  static final EmbeddedPostgres postgres;

  static {
    try {
      postgres = EmbeddedPostgres.builder().start();
    } catch (IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry p) {
    p.add("spring.config.import", () -> "");
    p.add(
        "spring.datasource.url",
        () -> postgres.getJdbcUrl("postgres", "postgres") + "&currentSchema=lbf,public");
    p.add("spring.datasource.username", () -> "postgres");
    p.add("spring.datasource.password", () -> "");
    p.add("baskstats.admin.email", () -> "");
    p.add("baskstats.admin.password", () -> "");
  }

  @Autowired MockMvc mvc;
  @Autowired JdbcClient db;
  @Autowired PasswordEncoder encoder;

  @BeforeEach
  void seed() {
    db.sql(
            "truncate lbf.temporada,lbf.time,lbf.jogadora,lbf.usuario,lbf.categoriaclipe restart"
                + " identity cascade")
        .update();
    db.sql(
            "insert into lbf.usuario(email,senha_hash)"
                + " values('coach@example.com',:hash),('other@example.com',:hash)")
        .param("hash", encoder.encode("valid-password-123"))
        .update();
    db.sql("insert into lbf.temporada(id_temporada,nome) values(1,'Atual')").update();
    db.sql("insert into lbf.time(id_time,nome) values(1,'Equipe A'),(2,'Equipe B')").update();
    db.sql(
            "insert into lbf.partida(id_partida,temporada_id,confronto,data_hora) values (1,1,'A x"
                + " B',current_timestamp-interval '1 day'),(2,1,'B x A',current_timestamp+interval"
                + " '1 day')")
        .update();
    db.sql(
            "insert into lbf.partida_time(id_partida_time,partida_id,time_id,placar_final)"
                + " values(1,1,1,78),(2,1,2,64),(3,2,1,null),(4,2,2,null)")
        .update();
    for (int i = 1; i <= 7; i++) {
      db.sql("insert into lbf.jogadora(id_jogadora,nome) values(:id,:nome)")
          .param("id", i)
          .param("nome", "Jogadora " + i)
          .update();
      db.sql(
              "insert into lbf.elenco(jogadora_id,time_id,temporada_id,numero_camisa,posicao)"
                  + " values(:id,1,1,:id,'Ala')")
          .param("id", i)
          .update();
      db.sql(
              "insert into"
                  + " lbf.estatisticajogadorapartida(partida_time_id,jogadora_id,pontos_feitos,rebote_total,assistencias,arremesso_2_feitos,arremesso_2_tentados,tempo_jogado)"
                  + " values(1,:id,:p,4,3,2,4,'25:30')")
          .param("id", i)
          .param("p", 28 - i)
          .update();
    }
    db.sql("insert into lbf.quarto(id_quarto,partida_id,numero) values(1,1,1),(2,1,2)").update();
    db.sql(
            "insert into lbf.placarquarto(quarto_id,partida_time_id,placar_acumulado)"
                + " values(1,1,20),(2,1,45)")
        .update();
    db.sql("insert into lbf.categoriaclipe(id_categoria_clipe,nome) values(1,'Ataque')").update();
    db.sql(
            "insert into"
                + " lbf.clipe(id_clipe,categoria_clipe_id,partida_id,caminho_arquivo,inicio_segundos,fim_segundos)"
                + " values(1,1,1,'https://example.com/game.mp4',10,20)")
        .update();
  }

  @Test
  void requiresAuthenticationAndCsrf() throws Exception {
    mvc.perform(get("/api/times/1/dashboard")).andExpect(status().isUnauthorized());
    mvc.perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"coach@example.com\",\"password\":\"valid-password-123\"}"))
        .andExpect(status().isForbidden());
    mvc.perform(
            post("/api/auth/login")
                .with(csrf())
                .contentType("application/json")
                .content("{\"email\":\"coach@example.com\",\"password\":\"incorrect\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void authenticatesPersistsSessionAndLogsOut() throws Exception {
    var result =
        mvc.perform(
                post("/api/auth/login")
                    .with(csrf())
                    .contentType("application/json")
                    .content(
                        "{\"email\":\"coach@example.com\",\"password\":\"valid-password-123\"}"))
            .andExpect(status().isOk())
            .andReturn();
    var session = (MockHttpSession) result.getRequest().getSession(false);
    mvc.perform(get("/api/auth/me").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("coach@example.com"));
    mvc.perform(post("/api/auth/logout").session(session).with(csrf()))
        .andExpect(status().isNoContent());
    Assertions.assertTrue(session.isInvalid());
  }

  @Test
  void aggregatesRealPostgresStatisticsAndSixScorers() throws Exception {
    mvc.perform(get("/api/times/1/dashboard").with(user("coach@example.com")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.jogadoras", hasSize(6)))
        .andExpect(jsonPath("$.vitorias").value(1))
        .andExpect(jsonPath("$.derrotas").value(0))
        .andExpect(jsonPath("$.arremesso2").value(50))
        .andExpect(jsonPath("$.pontosPorPosicao[0].pos").value("Ala"));
    mvc.perform(get("/api/times/1/jogadoras").with(user("coach@example.com")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(7)))
        .andExpect(jsonPath("$[0].numero").value(1));
    mvc.perform(get("/api/jogadoras/1/estatisticas").with(user("coach@example.com")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tocos").value(nullValue()))
        .andExpect(jsonPath("$.ppj").value(27));
    mvc.perform(get("/api/times/999/dashboard").with(user("coach@example.com")))
        .andExpect(status().isNotFound());
  }

  @Test
  void selectsUpcomingOpponentAndLastMatchQuarters() throws Exception {
    mvc.perform(get("/api/times/1/pre-jogo").with(user("coach@example.com")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.partida.id_partida").value(2))
        .andExpect(jsonPath("$.partida.adversario_id").value(2));
    mvc.perform(get("/api/times/1/pos-jogo").with(user("coach@example.com")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.partida.placar_final").value(78))
        .andExpect(jsonPath("$.quartos[0].placar").value(20))
        .andExpect(jsonPath("$.quartos[1].placar").value(25));
  }

  @Test
  void playlistCrudValidatesClipsAndOwnership() throws Exception {
    mvc.perform(get("/api/clipes").with(user("coach@example.com")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].url").value("https://example.com/game.mp4"));
    mvc.perform(
            post("/api/playlists")
                .with(user("coach@example.com"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"nome\":\"Ataques\",\"clipeIds\":[1]}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.clipes", hasSize(1)));
    mvc.perform(get("/api/playlists").with(user("other@example.com")))
        .andExpect(jsonPath("$", hasSize(0)));
    mvc.perform(
            put("/api/playlists/1")
                .with(user("other@example.com"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"nome\":\"Indevido\",\"clipeIds\":[]}"))
        .andExpect(status().isNotFound());
    mvc.perform(
            put("/api/playlists/1")
                .with(user("coach@example.com"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"nome\":\"Defesa\",\"clipeIds\":[]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.clipes", hasSize(0)));
    mvc.perform(
            post("/api/playlists")
                .with(user("coach@example.com"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"nome\":\"Inválido\",\"clipeIds\":[999]}"))
        .andExpect(status().isBadRequest());
    mvc.perform(delete("/api/playlists/1").with(user("coach@example.com")).with(csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  void trainingCrudAndInputValidation() throws Exception {
    mvc.perform(
            post("/api/treinos")
                .with(user("coach@example.com"))
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"titulo\":\"Arremessos\",\"descricao\":\"20"
                        + " repetições\",\"jogadoraId\":1,\"data\":\"2026-11-04\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.jogadoraId").value(1));
    mvc.perform(
            put("/api/treinos/1")
                .with(user("coach@example.com"))
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"titulo\":\"Defesa\",\"descricao\":\"Treino"
                        + " coletivo\",\"jogadoraId\":null,\"data\":null}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.titulo").value("Defesa"));
    mvc.perform(
            post("/api/treinos")
                .with(user("coach@example.com"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"titulo\":\"\",\"descricao\":\"\"}"))
        .andExpect(status().isBadRequest());
    mvc.perform(delete("/api/treinos/1").with(user("other@example.com")).with(csrf()))
        .andExpect(status().isNotFound());
    mvc.perform(delete("/api/treinos/1").with(user("coach@example.com")).with(csrf()))
        .andExpect(status().isNoContent());
  }

  @AfterAll
  static void stop() throws IOException {
    postgres.close();
  }
}
