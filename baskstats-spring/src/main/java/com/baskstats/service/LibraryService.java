package com.baskstats.service;

import com.baskstats.repository.LibraryRepository;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class LibraryService {
  private final LibraryRepository repo;

  public LibraryService(LibraryRepository repo) {
    this.repo = repo;
  }

  public List<Map<String, Object>> clips() {
    return repo.clips();
  }

  public List<Map<String, Object>> playlists(String owner) {
    var clips = repo.clips();
    return repo.playlists(owner).stream()
        .map(
            p -> {
              var row = new LinkedHashMap<>(p);
              var ids = repo.clipIds(((Number) p.get("id")).intValue());
              row.put(
                  "clipes",
                  ids.stream()
                      .flatMap(
                          id -> clips.stream().filter(c -> ((Number) c.get("id")).intValue() == id))
                      .toList());
              return (Map<String, Object>) row;
            })
        .toList();
  }

  public Map<String, Object> playlist(Integer id, String owner, String name, List<Integer> clips) {
    if (id != null && repo.playlist(id, owner).isEmpty()) throw missing();
    var existing = repo.clips().stream().map(c -> ((Number) c.get("id")).intValue()).toList();
    if (!existing.containsAll(clips) || new HashSet<>(clips).size() != clips.size())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    int saved = repo.savePlaylist(id, owner, name.trim());
    repo.replaceClips(saved, clips);
    return playlists(owner).stream()
        .filter(p -> ((Number) p.get("id")).intValue() == saved)
        .findFirst()
        .orElseThrow();
  }

  public void deletePlaylist(int id, String owner) {
    if (repo.deletePlaylist(id, owner) == 0) throw missing();
  }

  public List<Map<String, Object>> trainings(String owner) {
    return repo.trainings(owner);
  }

  public Map<String, Object> training(
      Integer id,
      String owner,
      String title,
      String description,
      Integer player,
      java.time.LocalDate date) {
    int saved = repo.saveTraining(id, owner, title.trim(), description, player, date);
    return trainings(owner).stream()
        .filter(t -> ((Number) t.get("id")).intValue() == saved)
        .findFirst()
        .orElseThrow(LibraryService::missing);
  }

  public void deleteTraining(int id, String owner) {
    if (repo.deleteTraining(id, owner) == 0) throw missing();
  }

  private static ResponseStatusException missing() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND);
  }
}
