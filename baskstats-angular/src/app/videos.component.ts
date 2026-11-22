import { Component, inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { catchError, of } from "rxjs";
import { BaskstatsService } from "./baskstats.service";
import { Clip, Playlist } from "./baskstats.model";
import { CategoriaGithub } from "./github-videos.service";

export type FiltroCategoria = "todos" | CategoriaGithub;

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
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

    <div
      class="category-filters"
      role="group"
      aria-label="Filtrar clipes por categoria"
    >
      <button
        type="button"
        *ngFor="let f of filtros"
        [class.active]="categoriaAtiva === f.valor"
        (click)="categoriaAtiva = f.valor"
      >
        {{ f.label }}
      </button>
    </div>

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
    <div class="clip-carousel" #carousel>
      <a
        class="clip-card"
        *ngFor="let c of clipsFiltrados"
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
            <span class="clip-badge">{{ formatarCategoria(c.categoria) }}</span>
            <span class="clip-tempo" *ngIf="extrairTrecho(c.nome) as trecho">
              {{ trecho }}
            </span>
          </div>
          <div class="clip-play-center">
            <span>▷</span>
          </div>
          <div class="clip-meta-footer">Análise de lance · Clique para abrir</div>
        </div>
        <span>{{ c.nome }}</span>
      </a>
      <p class="empty" *ngIf="!clipsFiltrados.length">
        Nenhum clipe disponível para esta categoria.
      </p>
    </div>
    <div class="section-actions">
      <h2>Playlists salvas</h2>
      <button
        *ngIf="!api.demo()"
        (click)="editing = true; editId = undefined; name = ''; selected = []"
      >
        + Nova playlist
      </button>
    </div>
    <form class="edit-form card" *ngIf="editing" (ngSubmit)="save()">
      <label
        >Nome<input name="name" [(ngModel)]="name" required maxlength="150"
      /></label>
      <fieldset>
        <legend>Clipes</legend>
        <label *ngFor="let c of clips"
          ><input
            type="checkbox"
            [checked]="selected.includes(c.id)"
            (change)="toggle(c.id)"
          />{{ c.nome }}</label
        >
      </fieldset>
      <button class="primary" [disabled]="!name.trim() || saving">Salvar</button
      ><button type="button" (click)="editing = false">Cancelar</button>
    </form>
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
        <div class="item-actions" *ngIf="!api.demo()">
          <button (click)="edit(p)">Editar</button
          ><button (click)="remove(p.id)">Excluir</button>
        </div>
      </article>
    </div>`,
})
export class VideosComponent implements OnInit {
  api = inject(BaskstatsService);
  clips: Clip[] = [];
  playlists: Playlist[] = [];
  error = "";
  editing = false;
  saving = false;
  editId: number | undefined;
  name = "";
  selected: number[] = [];

  categoriaAtiva: FiltroCategoria = "todos";

  readonly filtros: { label: string; valor: FiltroCategoria }[] = [
    { label: "Todos", valor: "todos" },
    { label: "Ataque", valor: "Ataque" },
    { label: "Defesa", valor: "Defesa" },
    { label: "Finalizações", valor: "Finalizacoes" },
    { label: "Rebote ofensivo", valor: "Rebote_Ofensivo" },
  ];

  get clipsFiltrados(): Clip[] {
    if (this.categoriaAtiva === "todos") {
      return this.clips;
    }
    return this.clips.filter(
      (c) =>
        (c.categoria ?? "").toLowerCase() === this.categoriaAtiva.toLowerCase(),
    );
  }

  formatarCategoria(categoria?: string): string {
    if (!categoria) return "Lance";
    return categoria.replace(/_/g, " ");
  }

  extrairTrecho(nome: string): string | null {
    const match = nome.match(/(Q[1-4]\s+\d+s-\d+s)/i);
    return match ? match[1] : null;
  }

  ngOnInit() {
    this.api.clips().subscribe({
      next: (c) => {
        this.clips = c;
        if (!c.length) {
          this.error = "Não foi possível carregar os clipes.";
        }
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

  toggle(id: number) {
    this.selected = this.selected.includes(id)
      ? this.selected.filter((i) => i !== id)
      : [...this.selected, id];
  }

  edit(p: Playlist) {
    this.editId = p.id;
    this.name = p.nome;
    this.selected = p.clipes.map((c) => c.id);
    this.editing = true;
  }

  save() {
    this.saving = true;
    this.api
      .savePlaylist(
        { nome: this.name.trim(), clipeIds: this.selected },
        this.editId,
      )
      .subscribe({
        next: () => {
          this.editing = false;
          this.saving = false;
          this.load();
        },
        error: () => {
          this.saving = false;
          this.error = "Não foi possível salvar a playlist.";
        },
      });
  }

  remove(id: number) {
    if (confirm("Excluir esta playlist?"))
      this.api.deletePlaylist(id).subscribe({
        next: () => this.load(),
        error: () => (this.error = "Não foi possível excluir a playlist."),
      });
  }
}