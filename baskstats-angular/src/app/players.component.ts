import { Component, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink } from "@angular/router";
import { catchError, of, switchMap } from "rxjs";
import { BaskstatsService } from "./baskstats.service";
@Component({
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `<header class="page-heading">
      <h1>Estatísticas individuais</h1>
      <p>
        Selecione uma atleta para ver o detalhamento completo das estatísticas.
      </p>
    </header>
    <p class="error" *ngIf="error" role="alert">{{ error }}</p>
    <div class="player-grid" *ngIf="players$ | async as players">
      <a
        class="player-card"
        *ngFor="let p of players"
        [routerLink]="['/jogadoras', p.id]"
        ><div class="jersey">{{ p.numero ?? "—" }}</div>
        <div class="player-name">
          <h2>{{ p.nome }}</h2>
          <p>{{ p.pos }}</p>
        </div>
        <div class="ppj">
          <strong>{{ p.ppj | number: "1.0-1" }}</strong
          ><small>PPJ</small>
        </div></a
      >
      <p class="empty" *ngIf="!players.length && !error">
        Nenhuma jogadora cadastrada neste elenco.
      </p>
    </div>`,
})
export class PlayersComponent {
  api = inject(BaskstatsService);
  error = "";
  players$ = this.api.teamId.pipe(
    switchMap((id) => {
      this.error = "";
      return this.api.players(id).pipe(
        catchError(() => {
          this.error = "Não foi possível carregar o elenco.";
          return of([]);
        }),
      );
    }),
  );
}
