import { Component, inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { BaskstatsService } from "./baskstats.service";
import { Clip, Playlist } from "./baskstats.model";
@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `<header class="page-heading">
      <h1>Vídeos e cortes</h1>
      <p>Biblioteca de jogos e treinos gravados.</p>
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
    <div class="clip-carousel" #carousel>
      <a
        class="clip-card"
        *ngFor="let c of clips"
        [routerLink]="['/videos/clipes', c.id]"
        ><div class="clip-preview"><span>▷</span></div>
        <span>{{ c.nome }}</span></a
      >
      <p class="empty" *ngIf="!clips.length">Nenhum clipe disponível.</p>
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
  ngOnInit() {
    this.api.clips().subscribe({
      next: (c) => (this.clips = c),
      error: () => (this.error = "Não foi possível carregar os clipes."),
    });
    this.load();
  }
  load() {
    this.api.playlists().subscribe({
      next: (p) => (this.playlists = p),
      error: () => (this.error = "Não foi possível carregar as playlists."),
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
