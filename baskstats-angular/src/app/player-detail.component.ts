import { Component, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { catchError, of, switchMap } from "rxjs";
import { BaskstatsService } from "./baskstats.service";
import { ShootingCardComponent } from "./stats-cards.component";
@Component({
  standalone: true,
  imports: [CommonModule, RouterLink, ShootingCardComponent],
  template: `<p class="error" *ngIf="error" role="alert">{{ error }}</p>
    <ng-container *ngIf="player$ | async as p"
      ><header class="page-heading">
        <h1>
          <a
            routerLink="/jogadoras"
            class="back"
            aria-label="Voltar às jogadoras"
            >〈</a
          >{{ p.nome }}
        </h1>
        <p>
          {{ p.pos }} · {{ p.jogos }} jogos disputados ·
          {{ p.minutos ?? "—" }} min por jogo · Eficiência {{ p.eficiencia }}
        </p>
      </header>
      <div class="individual-grid">
        <article class="card stat" *ngFor="let s of stats(p)">
          <h2>{{ s[0] }}</h2>
          <strong>{{ s[1] ?? "—" }}</strong
          ><small *ngIf="s[1] === null">Não registrado no banco</small>
        </article>
      </div>
      <div class="detail-charts">
        <article class="card">
          <h2>Principais estatísticas</h2>
          <div class="positions">
            <div *ngFor="let s of stats(p)">
              <span>{{ s[0] }}</span>
              <div>
                <i [style.width.%]="width(s[1])"></i><b>{{ s[1] ?? "—" }}</b>
              </div>
            </div>
          </div>
        </article>
        <app-shooting-card [data]="p" /></div
    ></ng-container>`,
})
export class PlayerDetailComponent {
  api = inject(BaskstatsService);
  route = inject(ActivatedRoute);
  error = "";
  player$ = this.route.paramMap.pipe(
    switchMap((params) =>
      this.api.player(Number(params.get("id"))).pipe(
        catchError(() => {
          this.error = "Jogadora não encontrada ou estatísticas indisponíveis.";
          return of(null);
        }),
      ),
    ),
  );
  stats(p: any): [string, number | null][] {
    return [
      ["Pontos por jogo", p.ppj],
      ["Rebotes por jogo", p.reb],
      ["Assistência por jogo", p.ast],
      ["Roubos de bola", p.roubos],
      ["Tocos", p.tocos],
      ["Perdas de bola", p.turnovers],
    ];
  }
  width(n: any) {
    return Math.min(85, Math.max(0, Number(n) || 0) * 3);
  }
}
