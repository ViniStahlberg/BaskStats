import { Component, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { switchMap, map, catchError, of } from "rxjs";
import { BaskstatsService } from "./baskstats.service";
@Component({
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `<ng-container *ngIf="playlist$ | async as p; else missing"
      ><header class="page-heading">
        <h1>
          <a routerLink="/videos" class="back" aria-label="Voltar aos vídeos"
            >〈</a
          >{{ p.nome }}
        </h1>
        <p>Playlist salva de vídeos</p>
      </header>
      <div class="clip-carousel">
        <a
          class="clip-card"
          *ngFor="let c of p.clipes"
          [routerLink]="['/videos/clipes', c.id]"
          ><div class="clip-preview"><span>▷</span></div>
          <span>{{ c.nome }}</span></a
        >
        <p class="empty" *ngIf="!p.clipes.length">
          Esta playlist ainda não possui clipes.
        </p>
      </div></ng-container
    ><ng-template #missing
      ><p class="empty">
        Playlist não encontrada ou indisponível.
      </p></ng-template
    >`,
})
export class PlaylistComponent {
  api = inject(BaskstatsService);
  route = inject(ActivatedRoute);
  playlist$ = this.route.paramMap.pipe(
    switchMap((p) =>
      this.api.playlists().pipe(
        map((ps) => ps.find((x) => x.id === Number(p.get("id")))),
        catchError(() => of(null)),
      ),
    ),
  );
}
