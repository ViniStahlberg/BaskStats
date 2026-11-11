package com.baskstats.controller;

import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  public record Credentials(
      @NotBlank @Email String email,
      @NotBlank @Size(max = 200) String password,
      boolean remember) {}

  private final AuthenticationManager manager;
  private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

  private record Attempt(int count, long since) {}

  public AuthController(AuthenticationManager manager) {
    this.manager = manager;
  }

  @GetMapping("/csrf")
  public Map<String, String> csrf(CsrfToken token) {
    return Map.of("token", token.getToken());
  }

  @PostMapping("/login")
  public Map<String, String> login(
      @Valid @RequestBody Credentials credentials,
      HttpServletRequest req,
      HttpServletResponse res) {
    String key = req.getRemoteAddr() + ":" + credentials.email().toLowerCase(Locale.ROOT);
    long now = System.currentTimeMillis();
    attempts.entrySet().removeIf(e -> now - e.getValue().since() > 900000);
    Attempt attempt =
        attempts.compute(
            key, (k, a) -> a == null ? new Attempt(1, now) : new Attempt(a.count() + 1, a.since()));
    if (attempt.count() > 10) throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
    try {
      Authentication auth =
          manager.authenticate(
              UsernamePasswordAuthenticationToken.unauthenticated(
                  credentials.email(), credentials.password()));
      if (req.getSession(false) != null) req.getSession().invalidate();
      var session = req.getSession(true);
      session.setMaxInactiveInterval(credentials.remember() ? 604800 : 1800);
      var context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(auth);
      SecurityContextHolder.setContext(context);
      new HttpSessionSecurityContextRepository().saveContext(context, req, res);
      if (credentials.remember())
        res.addHeader(
            "Set-Cookie",
            org.springframework.http.ResponseCookie.from("JSESSIONID", session.getId())
                .httpOnly(true)
                .secure(req.isSecure())
                .sameSite("Lax")
                .path("/")
                .maxAge(604800)
                .build()
                .toString());
      attempts.remove(key);
      return Map.of("email", auth.getName());
    } catch (AuthenticationException ex) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }

  @GetMapping("/me")
  public Map<String, String> me(Authentication auth) {
    return Map.of("email", auth.getName());
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(HttpServletRequest req, HttpServletResponse res) {
    if (req.getSession(false) != null) req.getSession().invalidate();
    SecurityContextHolder.clearContext();
    var cookie = new Cookie("JSESSIONID", "");
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0);
    cookie.setSecure(req.isSecure());
    res.addCookie(cookie);
  }
}
