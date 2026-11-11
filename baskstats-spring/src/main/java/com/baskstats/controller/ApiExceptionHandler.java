package com.baskstats.controller;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, String>> invalidReference(
      DataIntegrityViolationException error) {
    return ResponseEntity.badRequest()
        .body(Map.of("message", "Dados inválidos ou referência inexistente."));
  }
}
