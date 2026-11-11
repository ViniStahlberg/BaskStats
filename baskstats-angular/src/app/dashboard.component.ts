import { Component, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { catchError, of, switchMap } from "rxjs";
import { BaskstatsService } from "./baskstats.service";
import { StatsGridComponent } from "./stats-cards.component";
@Component({
  standalone: true,
  imports: [CommonModule, StatsGridComponent],
  template: `<header class="page-heading">
      <h1>Visão geral da temporada do NBF</h1>
      <p>
        {{
          api.demo()
            ? "Dados de demonstração para explorar as estatísticas."
            : "Acompanhe o desempenho da sua equipe na temporada."
        }}
      </p>
    </header>
    <p class="error" role="alert" *ngIf="error">{{ error }}</p>
    <ng-container *ngIf="data$ | async as data; else loading"
      ><app-stats-grid [data]="data" /></ng-container
    ><ng-template #loading
      ><p *ngIf="!error" class="empty">Carregando estatísticas…</p></ng-template
    >`,
})
export class DashboardComponent {
  api = inject(BaskstatsService);
  error = "";
  data$ = this.api.teamId.pipe(
    switchMap((id) => {
      this.error = "";
      return this.api.dashboard(id).pipe(
        catchError(() => {
          this.error =
            "Não foi possível carregar o painel. Verifique a conexão com a API.";
          return of(null);
        }),
      );
    }),
  );
}
