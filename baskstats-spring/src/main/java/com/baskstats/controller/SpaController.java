package com.baskstats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {
  @GetMapping({
    "/",
    "/login",
    "/painel",
    "/jogadoras",
    "/jogadoras/{id}",
    "/videos",
    "/videos/clipes/{id}",
    "/videos/playlists/{id}",
    "/pre-jogo",
    "/pos-jogo",
    "/treinos"
  })
  public String index() {
    return "forward:/index.html";
  }
}
