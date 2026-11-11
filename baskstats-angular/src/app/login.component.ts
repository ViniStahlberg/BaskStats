import { Component, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { AuthService } from "./auth.service";
import { BaskstatsService } from "./baskstats.service";
@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `<section class="login-screen">
    <form class="login-card" #form="ngForm" (ngSubmit)="form.valid && login()">
      <img class="login-logo" src="/img/logo.png" alt="BaskStats" /><label
        for="email"
        >E-mail</label
      ><input
        id="email"
        type="email"
        name="email"
        [(ngModel)]="email"
        required
        email
        autocomplete="username"
        placeholder="seu@email.com"
      /><label for="password">Senha</label
      ><input
        id="password"
        type="password"
        name="password"
        [(ngModel)]="password"
        required
        minlength="8"
        autocomplete="current-password"
        placeholder="••••••••"
      /><label class="remember"
        ><input type="checkbox" name="remember" [(ngModel)]="remember" />
        Lembrar de mim</label
      >
      <p class="error" role="alert" *ngIf="error">{{ error }}</p>
      <button class="primary" [disabled]="busy || !form.valid">
        {{ busy ? "Entrando…" : "Entrar" }}</button
      ><button class="demo-button" type="button" (click)="demo()">
        Explorar demonstração →
      </button>
    </form>
  </section>`,
})
export class LoginComponent {
  auth = inject(AuthService);
  api = inject(BaskstatsService);
  router = inject(Router);
  email = "";
  password = "";
  remember = false;
  busy = false;
  error = "";
  login() {
    this.busy = true;
    this.error = "";
    this.auth.login(this.email, this.password, this.remember).subscribe({
      next: () => {
        sessionStorage.removeItem("bask-demo");
        this.api.demo.set(false);
        this.router.navigateByUrl("/painel");
      },
      error: (e) => {
        this.busy = false;
        this.error =
          e.status === 401
            ? "E-mail ou senha incorretos."
            : "Não foi possível conectar à API. Tente novamente.";
      },
    });
  }
  demo() {
    sessionStorage.setItem("bask-demo", "true");
    this.api.demo.set(true);
    this.router.navigateByUrl("/painel");
  }
}
