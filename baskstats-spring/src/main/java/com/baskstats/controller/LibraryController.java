package com.baskstats.controller;

import com.baskstats.service.LibraryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LibraryController {
  public record PlaylistInput(
      @NotBlank @Size(max = 150) String nome,
      @NotNull @Size(max = 500) List<@NotNull @Positive Integer> clipeIds) {}

  public record TrainingInput(
      @NotBlank @Size(max = 150) String titulo,
      @NotNull @Size(max = 10000) String descricao,
      @Positive Integer jogadoraId,
      java.time.LocalDate data) {}

  private final LibraryService service;

  public LibraryController(LibraryService service) {
    this.service = service;
  }

  @GetMapping("/clipes")
  public List<Map<String, Object>> clips() {
    return service.clips();
  }

  @GetMapping("/playlists")
  public List<Map<String, Object>> playlists(Authentication a) {
    return service.playlists(a.getName());
  }

  @GetMapping("/playlists/{id}")
  public Map<String, Object> playlist(@PathVariable int id, Authentication a) {
    return service.playlists(a.getName()).stream()
        .filter(p -> ((Number) p.get("id")).intValue() == id)
        .findFirst()
        .orElseThrow(
            () -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @PostMapping("/playlists")
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> create(@Valid @RequestBody PlaylistInput p, Authentication a) {
    return service.playlist(null, a.getName(), p.nome(), p.clipeIds());
  }

  @PutMapping("/playlists/{id}")
  public Map<String, Object> update(
      @PathVariable int id, @Valid @RequestBody PlaylistInput p, Authentication a) {
    return service.playlist(id, a.getName(), p.nome(), p.clipeIds());
  }

  @DeleteMapping("/playlists/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePlaylist(@PathVariable int id, Authentication a) {
    service.deletePlaylist(id, a.getName());
  }

  @GetMapping("/treinos")
  public List<Map<String, Object>> trainings(Authentication a) {
    return service.trainings(a.getName());
  }

  @PostMapping("/treinos")
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> createTraining(@Valid @RequestBody TrainingInput t, Authentication a) {
    return service.training(null, a.getName(), t.titulo(), t.descricao(), t.jogadoraId(), t.data());
  }

  @PutMapping("/treinos/{id}")
  public Map<String, Object> updateTraining(
      @PathVariable int id, @Valid @RequestBody TrainingInput t, Authentication a) {
    return service.training(id, a.getName(), t.titulo(), t.descricao(), t.jogadoraId(), t.data());
  }

  @DeleteMapping("/treinos/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTraining(@PathVariable int id, Authentication a) {
    service.deleteTraining(id, a.getName());
  }
}
