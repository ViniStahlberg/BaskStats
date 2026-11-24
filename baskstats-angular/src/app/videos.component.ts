import { Component, inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink } from "@angular/router";
import { catchError, of } from "rxjs";
import { BaskstatsService } from "./baskstats.service";
import { Clip, Playlist } from "./baskstats.model";

@Component({
  standalone: true,
  imports: [CommonModule, RouterLink],
  styles: [
    `
      .clip-preview-solid {
        position: relative;
        background: linear-gradient(160deg, #0d1b33 0%, #070e1c 100%);
        border-bottom: 1px solid rgba(255, 255, 255, 0.08);
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        padding: 12px;
        min-height: 150px;
      }

      .clip-preview-video {
        position: absolute;
        inset: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;
        opacity: 0.45;
        pointer-events: none;
      }

      .clip-preview-top {
        position: relative;
        z-index: 1;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 8px;
      }

      .clip-badge {
        font-size: 0.72rem;
        font-weight: 600;
        letter-spacing: 0.03em;
        padding: 3px 8px;
        border-radius: 999px;
        background: rgba(249, 115, 22, 0.2);
        color: #fdba74;
        border: 1px solid rgba(249, 115, 22, 0.45);
      }

      .clip-tempo {
        font-size: 0.72rem;
        color: #cbd5e1;
        background: rgba(2, 6, 23, 0.75);
        padding: 2px 7px;
        border-radius: 6px;
      }

      .clip-play-center {
        position: relative;
        z-index: 1;
        align-self: center;
        width: 44px;
        height: 44px;
        border-radius: 999px;
        background: rgba(15, 23, 42, 0.9);
        border: 1px solid rgba(255, 255, 255, 0.18);
        display: grid;
        place-items: center;
        font-size: 1rem;
      }

      .clip-meta-footer {
        position: relative;
        z-index: 1;
        font-size: 0.75rem;
        color: #94a3b8;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    `,
  ],
  template: `<header class="page-heading">
      <h1>Vídeos e cortes</h1>
    </header>
    <p class="error" *ngIf="error" role="alert">{{ error }}</p>

    <div class="section-actions">
      <span>Clipes recentes</span>
      <div>
        <button
          (click)="carousel.scrollBy({ left: -340, behavior: 'smooth' })"
          aria-label="Clipes anteriores"
        >
          ←</button
        ><button
          (click)="carousel.scrollBy({ left: 340, behavior: 'smooth' })"
          aria-label="Próximos clipes"
        >
          →
        </button>
      </div>
    </div>
    <div
      class="clip-filters"
      role="group"
      aria-label="Filtrar clipes por categoria"
      *ngIf="categorias.length"
    >
      <button
        type="button"
        [class.active]="!categoria"
        [attr.aria-pressed]="!categoria"
        (click)="categoria = null"
      >
        Todos ({{ clips.length }})
      </button>
      <button
        type="button"
        *ngFor="let cat of categorias"
        [class.active]="categoria === cat"
        [attr.aria-pressed]="categoria === cat"
        (click)="categoria = cat"
      >
        {{ rotulo(cat) }} ({{ total(cat) }})
      </button>
    </div>
    <div class="clip-carousel" #carousel>
      <a
        class="clip-card"
        *ngFor="let c of filtrados"
        [routerLink]="['/videos/clipes', c.id]"
      >
        <div class="clip-preview clip-preview-solid">
          <video
            *ngIf="c.url"
            class="clip-preview-video"
            [src]="c.url"
            preload="metadata"
            muted
            playsinline
          ></video>
          <div class="clip-preview-top">
            <span class="clip-badge">{{ rotulo(c.categoria || "Lance") }}</span>
            <span class="clip-tempo" *ngIf="extrairTrecho(c.nome) as trecho">
              {{ trecho }}
            </span>
          </div>
          <div class="clip-play-center">
            <span>▷</span>
          </div>
          <div class="clip-meta-footer">Análise de lance · Clique para abrir</div>
        </div>
        <small class="clip-tag" *ngIf="c.categoria">{{
          rotulo(c.categoria)
        }}</small>
        <span>{{ c.nome }}</span>
      </a>
      <p class="empty" *ngIf="!filtrados.length">Nenhum clipe disponível.</p>
    </div>
    <div class="section-actions">
      <h2>Playlists salvas</h2>
    </div>
    <div class="playlist-grid">
      <article *ngFor="let p of playlists">
        <a [routerLink]="['/videos/playlists', p.id]" class="playlist-tile"
          ><div class="playlist-mosaic">
            <span *ngFor="let i of [0, 1, 2, 3]">{{
              p.clipes[i] ? "▷" : ""
            }}</span>
          </div>
          <strong>{{ p.nome }}</strong
          ><small>{{ p.clipes.length }} clipes</small></a
        >
      </article>
    </div>`,
})
export class VideosComponent implements OnInit {
  api = inject(BaskstatsService);
  clips: Clip[] = [];
  playlists: Playlist[] = [];
  error = "";
  categoria: string | null = null;
  categorias: string[] = [];

  private rotulos: Record<string, string> = {
    Ataque: "Ataque",
    Defesa: "Defesa",
    Finalizacoes: "Finalizações",
    Rebote_Ofensivo: "Rebote ofensivo",
  };

  get filtrados() {
    return this.categoria
      ? this.clips.filter((c) => c.categoria === this.categoria)
      : this.clips;
  }

  rotulo(cat: string) {
    return this.rotulos[cat] ?? cat.replace(/_/g, " ");
  }

  total(cat: string) {
    return this.clips.filter((c) => c.categoria === cat).length;
  }

  extrairTrecho(nome: string): string | null {
    const match = nome.match(/(Q[1-4]\s+\d+s-\d+s)/i);
    return match ? match[1] : null;
  }

  ngOnInit() {
    this.api.clips().subscribe({
      next: (c) => {
        this.clips = c;
        this.categorias = [
          ...new Set(c.map((x) => x.categoria).filter((x) => !!x)),
        ].sort();
      },
      error: () => (this.error = "Não foi possível carregar os clipes."),
    });
    this.load();
  }

  load() {
    this.api
      .playlists()
      .pipe(catchError(() => of([] as Playlist[])))
      .subscribe({
        next: (p) => (this.playlists = p),
      });
  }
}
