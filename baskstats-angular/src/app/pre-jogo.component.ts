import { Component, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { catchError, of, switchMap } from "rxjs";
import { BaskstatsService } from "./baskstats.service";
import { StatsGridComponent } from "./stats-cards.component";
@Component({
  standalone: true,
  imports: [CommonModule, StatsGridComponent],
  template: `<header class="page-heading">
      <h1>Pré-jogo</h1>
      <p>Conheça os números do próximo adversário.</p>
    </header>
    <p class="empty" *ngIf="message" role="status">{{ message }}</p>
    <ng-container *ngIf="data$ | async as data"
      ><div class="match-summary">
        <strong>{{ data.partida.confronto }}</strong
        ><span *ngIf="data.partida.data_hora">{{
          data.partida.data_hora | date: "dd/MM/yyyy HH:mm"
        }}</span
        ><span *ngFor="let q of data.quartos"
          >{{ q.numero }}º quarto: {{ q.placar }}</span
        >
      </div>
      <app-stats-grid [data]="data.dashboard"
    /></ng-container>`,
})
export class PreGameComponent {
  api = inject(BaskstatsService);
  message = "";
  data$ = this.api.teamId.pipe(
    switchMap((id) => {
      this.message = "";
      return this.api.match(id, "pre-jogo").pipe(
        catchError((e) => {
          this.message =
            e.status === 404
              ? "Nenhuma partida disponível para esta equipe."
              : "Não foi possível carregar os dados da partida.";
          return of(null);
        }),
      );
    }),
  );
}
