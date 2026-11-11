package com.baskstats.service;

import com.baskstats.repository.AnalyticsRepository;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {
  private final AnalyticsRepository repo;

  public AnalyticsService(AnalyticsRepository repo) {
    this.repo = repo;
  }

  public List<Map<String, Object>> teams() {
    return repo.teams();
  }

  private void team(int id) {
    if (teams().stream().noneMatch(t -> ((Number) t.get("id")).intValue() == id))
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  }

  public List<Map<String, Object>> players(int id) {
    team(id);
    return repo.players(id, null);
  }

  public Map<String, Object> player(int id) {
    return repo.player(id).stream()
        .findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  public Map<String, Object> dashboard(int id, Integer partida) {
    team(id);
    var games = repo.games(id, partida);
    var players = repo.players(id, partida);
    Map<String, Object> result = new LinkedHashMap<>(repo.shooting(id, partida).get(0));
    var recent = games.subList(Math.max(0, games.size() - 10), games.size());
    result.put(
        "rodadas",
        java.util.stream.IntStream.range(0, recent.size())
            .mapToObj(i -> "R" + (games.size() - recent.size() + i + 1))
            .toList());
    result.put("pontosPorRodada", recent.stream().map(g -> g.get("placar_final")).toList());
    result.put(
        "vitorias",
        games.stream()
            .filter(
                g ->
                    g.get("adversario_placar") != null
                        && num(g.get("placar_final")) > num(g.get("adversario_placar")))
            .count());
    result.put(
        "derrotas",
        games.stream()
            .filter(
                g ->
                    g.get("adversario_placar") != null
                        && num(g.get("placar_final")) < num(g.get("adversario_placar")))
            .count());
    result.put(
        "jogadoras", players.stream().filter(p -> num(p.get("jogos")) > 0).limit(6).toList());
    Map<String, List<Double>> positions = new LinkedHashMap<>();
    players.stream()
        .filter(p -> num(p.get("jogos")) > 0 && !"Não informada".equals(p.get("pos")))
        .forEach(
            p ->
                positions
                    .computeIfAbsent((String) p.get("pos"), k -> new ArrayList<>())
                    .add(num(p.get("ppj"))));
    result.put(
        "pontosPorPosicao",
        positions.entrySet().stream()
            .map(
                e ->
                    Map.of(
                        "pos",
                        e.getKey(),
                        "media",
                        Math.round(
                                e.getValue().stream()
                                        .mapToDouble(Double::doubleValue)
                                        .average()
                                        .orElse(0)
                                    * 10)
                            / 10.0))
            .toList());
    return result;
  }

  public Map<String, Object> match(int id, boolean upcoming) {
    team(id);
    var m =
        repo.match(id, upcoming).stream()
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("partida", m);
    result.put(
        "dashboard",
        dashboard(
            upcoming ? ((Number) m.get("adversario_id")).intValue() : id,
            upcoming ? null : ((Number) m.get("id_partida")).intValue()));
    if (!upcoming)
      result.put("quartos", repo.quarters(((Number) m.get("id_partida_time")).intValue()));
    return result;
  }

  private static double num(Object n) {
    return n instanceof Number ? ((Number) n).doubleValue() : 0;
  }
}
