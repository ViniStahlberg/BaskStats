import { Component, inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  Router,
  RouterOutlet,
  RouterLink,
  RouterLinkActive,
} from "@angular/router";
import { BaskstatsService } from "./baskstats.service";
import { AuthService } from "./auth.service";
import { Team } from "./baskstats.model";
@Component({
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
  ],
  template: ` <div class="app-shell">
    <aside class="sidebar">
      <a routerLink="/painel" class="brand"
        ><img src="/img/logo.png" alt="BaskStats"
      /></a>
      <nav aria-label="Menu principal">
        <a
          *ngFor="let item of menu"
          [routerLink]="'/' + item[0]"
          routerLinkActive="active"
          #active="routerLinkActive"
          [attr.aria-current]="active.isActive ? 'page' : null"
          ><span class="nav-icon"
            ><img
              [src]="
                '/img/' + item[0] + (active.isActive ? '-dark' : '') + '.png'
              "
              alt="" /></span
          ><span>{{ item[1] }}</span></a
        >
      </nav>
      <button class="logout" (click)="logout()">Sair ↗</button>
    </aside>
    <main>
      <div class="context-bar">
        <span *ngIf="api.demo()" class="demo-badge"
          >Demonstração · dados de exemplo</span
        ><label
          >Time
          <select
            aria-label="Time selecionado"
            [ngModel]="api.teamId.value"
            (ngModelChange)="api.selectTeam(+$event)"
          >
            <option *ngFor="let t of teams" [value]="t.id">{{ t.nome }}</option>
          </select></label
        >
      </div>
      <router-outlet />
    </main>
  </div>`,
})
export class ShellComponent implements OnInit {
  api = inject(BaskstatsService);
  auth = inject(AuthService);
  router = inject(Router);
  teams: Team[] = [];
  menu = [
    ["painel", "Painel"],
    ["jogadoras", "Jogadoras"],
    ["videos", "Vídeos"],
    ["pre-jogo", "Pré-jogo"],
    ["pos-jogo", "Pós-jogo"],
    ["treinos", "Treinos"],
  ];
  ngOnInit() {
    this.api.teams().subscribe({
      next: (t) => {
        this.teams = t;
        if (t.length && !t.some((x) => x.id === this.api.teamId.value))
          this.api.selectTeam(t[0].id);
      },
    });
  }
  logout() {
    if (this.api.demo()) {
      this.finish();
      return;
    }
    this.auth.logout().subscribe({
      next: () => this.finish(),
      error: () =>
        alert("Não foi possível encerrar a sessão. Tente novamente."),
    });
  }
  finish() {
    sessionStorage.removeItem("bask-demo");
    this.api.demo.set(false);
    this.router.navigateByUrl("/login");
  }
}
