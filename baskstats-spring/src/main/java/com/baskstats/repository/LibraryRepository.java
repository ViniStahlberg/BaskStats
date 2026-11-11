package com.baskstats.repository;

import java.util.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class LibraryRepository {
  private final JdbcClient db;

  public LibraryRepository(JdbcClient db) {
    this.db = db;
  }

  public List<Map<String, Object>> query(String sql, Map<String, ?> args) {
    return db.sql(sql).params(args).query().listOfRows();
  }

  public List<Map<String, Object>> clips() {
    return query(
        """
        select c.id_clipe as id,coalesce(cc.nome,'Clipe')||'_'||c.id_clipe as nome,cc.nome as categoria,
        case when c.caminho_arquivo ~ '^https?://' then c.caminho_arquivo
        when p.link_video ~ '^https?://' then p.link_video else null end as url,
        coalesce(c.inicio_segundos,0) as inicio,coalesce(c.fim_segundos,0) as fim
        from clipe c left join categoriaclipe cc on cc.id_categoria_clipe=c.categoria_clipe_id
        left join partida p on p.id_partida=c.partida_id order by c.id_clipe desc
        """,
        Map.of());
  }

  public List<Map<String, Object>> playlists(String owner) {
    return query(
        "select id,nome from playlist where usuario_email=:owner order by id",
        Map.of("owner", owner));
  }

  public List<Integer> clipIds(int id) {
    return db.sql("select clipe_id from playlist_clipe where playlist_id=:id order by ordem")
        .param("id", id)
        .query(Integer.class)
        .list();
  }

  public Optional<Integer> playlist(int id, String owner) {
    return db.sql("select id from playlist where id=:id and usuario_email=:owner")
        .param("id", id)
        .param("owner", owner)
        .query(Integer.class)
        .optional();
  }

  public int savePlaylist(Integer id, String owner, String name) {
    if (id == null)
      return db.sql("insert into playlist(nome,usuario_email) values(:name,:owner) returning id")
          .param("name", name)
          .param("owner", owner)
          .query(Integer.class)
          .single();
    db.sql("update playlist set nome=:name where id=:id and usuario_email=:owner")
        .param("id", id)
        .param("name", name)
        .param("owner", owner)
        .update();
    return id;
  }

  public void replaceClips(int id, List<Integer> clips) {
    db.sql("delete from playlist_clipe where playlist_id=:id").param("id", id).update();
    for (int i = 0; i < clips.size(); i++)
      db.sql("insert into playlist_clipe(playlist_id,clipe_id,ordem) values(:id,:clip,:ordem)")
          .param("id", id)
          .param("clip", clips.get(i))
          .param("ordem", i)
          .update();
  }

  public int deletePlaylist(int id, String owner) {
    return db.sql("delete from playlist where id=:id and usuario_email=:owner")
        .param("id", id)
        .param("owner", owner)
        .update();
  }

  public List<Map<String, Object>> trainings(String owner) {
    return query(
        "select id,titulo,descricao,jogadora_id as \"jogadoraId\",data from treino where"
            + " usuario_email=:owner order by data nulls last,id",
        Map.of("owner", owner));
  }

  public int saveTraining(
      Integer id,
      String owner,
      String title,
      String description,
      Integer player,
      java.time.LocalDate date) {
    var params = new HashMap<String, Object>();
    params.put("owner", owner);
    params.put("title", title);
    params.put("description", description);
    params.put("player", player);
    params.put("date", date);
    if (id == null)
      return db.sql(
              "insert into treino(usuario_email,titulo,descricao,jogadora_id,data)"
                  + " values(:owner,:title,:description,:player,:date) returning id")
          .params(params)
          .query(Integer.class)
          .single();
    params.put("id", id);
    return db.sql(
            "update treino set titulo=:title,descricao=:description,jogadora_id=:player,data=:date"
                + " where id=:id and usuario_email=:owner returning id")
        .params(params)
        .query(Integer.class)
        .optional()
        .orElse(-1);
  }

  public int deleteTraining(int id, String owner) {
    return db.sql("delete from treino where id=:id and usuario_email=:owner")
        .param("id", id)
        .param("owner", owner)
        .update();
  }
}
