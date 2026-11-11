import { Component, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { switchMap, map, catchError, of } from "rxjs";
import { BaskstatsService } from "./baskstats.service";
@Component({
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `<ng-container *ngIf="clip$ | async as clip; else missing"
      ><header class="page-heading">
        <h1>
          <a routerLink="/videos" class="back" aria-label="Voltar aos vídeos"
            >〈</a
          >{{ clip.nome }}
        </h1>
      </header>
      <article class="card video-player">
        <video
          *ngIf="safeUrl(clip.url); else unavailable"
          controls
          [src]="safeUrl(clip.url)"
          [attr.aria-label]="clip.nome"
          (loadedmetadata)="seek($event, clip.inicio)"
          (timeupdate)="stop($event, clip.fim)"
        ></video
        ><ng-template #unavailable
          ><div class="video-unavailable">
            <span>▷</span>
            <p>Vídeo ainda não disponível.</p>
          </div></ng-template
        >
      </article></ng-container
    ><ng-template #missing
      ><p class="empty">Clipe não encontrado ou indisponível.</p></ng-template
    >`,
})
export class ClipComponent {
  api = inject(BaskstatsService);
  route = inject(ActivatedRoute);
  clip$ = this.route.paramMap.pipe(
    switchMap((p) =>
      this.api.clips().pipe(
        map((cs) => cs.find((c) => c.id === Number(p.get("id")))),
        catchError(() => of(null)),
      ),
    ),
  );
  safeUrl(url: string | null) {
    if (!url) return null;
    try {
      const u = new URL(url);
      return ["https:", "http:"].includes(u.protocol) ? u.href : null;
    } catch {
      return null;
    }
  }
  seek(e: Event, start: number) {
    (e.target as HTMLVideoElement).currentTime = start;
  }
  stop(e: Event, end: number) {
    const v = e.target as HTMLVideoElement;
    if (end > 0 && v.currentTime >= end) v.pause();
  }
}
