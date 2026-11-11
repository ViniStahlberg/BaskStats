import { Component, inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { BaskstatsService } from "./baskstats.service";
import { Training, Player } from "./baskstats.model";
@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `<header class="page-heading">
      <h1>Treinos</h1>
      <p>Adicione ou edite treinos individuais de cada atleta.</p>
    </header>
    <button class="new-training" *ngIf="!api.demo()" (click)="start()">
      + Adicionar treino
    </button>
    <p *ngIf="error" class="error" role="alert">{{ error }}</p>
    <form class="edit-form card" *ngIf="editing" (ngSubmit)="save()">
      <label
        >Título<input
          [(ngModel)]="draft.titulo"
          name="title"
          required
          maxlength="150" /></label
      ><label
        >Atleta<select [(ngModel)]="draft.jogadoraId" name="player">
          <option [ngValue]="null">Equipe inteira</option>
          <option *ngFor="let p of players" [ngValue]="p.id">
            {{ p.nome }}
          </option>
        </select></label
      ><label
        >Data<input type="date" [(ngModel)]="draft.data" name="date" /></label
      ><label
        >Descrição<textarea
          [(ngModel)]="draft.descricao"
          name="description"
          maxlength="10000"
        ></textarea></label
      ><button class="primary" [disabled]="!draft.titulo.trim() || saving">
        Salvar</button
      ><button type="button" (click)="editing = false">Cancelar</button>
    </form>
    <div class="stats-grid training-grid">
      <article
        class="card training-card"
        *ngFor="let t of slots; let i = index"
        [attr.aria-label]="t?.titulo || 'Espaço para treino ' + (i + 1)"
      >
        <ng-container *ngIf="t"
          ><h2>{{ t.titulo }}</h2>
          <p>{{ t.descricao }}</p>
          <small>{{ t.data | date: "dd/MM/yyyy" }}</small>
          <div class="item-actions">
            <button (click)="edit(t)">Editar</button
            ><button (click)="remove(t.id)">Excluir</button>
          </div></ng-container
        >
      </article>
    </div>`,
})
export class TrainingsComponent implements OnInit {
  api = inject(BaskstatsService);
  trainings: Training[] = [];
  players: Player[] = [];
  error = "";
  editing = false;
  saving = false;
  editId: number | undefined;
  draft: Omit<Training, "id"> = {
    titulo: "",
    descricao: "",
    jogadoraId: null,
    data: null,
  };
  get slots() {
    return [
      ...this.trainings,
      ...Array(Math.max(0, 5 - this.trainings.length)).fill(null),
    ] as (Training | null)[];
  }
  ngOnInit() {
    this.load();
  }
  load() {
    this.api.trainings().subscribe({
      next: (t) => (this.trainings = t),
      error: () => (this.error = "Não foi possível carregar os treinos."),
    });
  }
  start() {
    this.editId = undefined;
    this.draft = { titulo: "", descricao: "", jogadoraId: null, data: null };
    this.editing = true;
    this.api.players(this.api.teamId.value).subscribe({
      next: (p) => (this.players = p),
      error: () => (this.error = "Não foi possível carregar as atletas."),
    });
  }
  edit(t: Training) {
    this.start();
    this.editId = t.id;
    this.draft = {
      titulo: t.titulo,
      descricao: t.descricao,
      jogadoraId: t.jogadoraId,
      data: t.data,
    };
  }
  save() {
    this.saving = true;
    this.api
      .saveTraining(
        { ...this.draft, data: this.draft.data || null },
        this.editId,
      )
      .subscribe({
        next: () => {
          this.saving = false;
          this.editing = false;
          this.load();
        },
        error: () => {
          this.saving = false;
          this.error = "Não foi possível salvar o treino.";
        },
      });
  }
  remove(id: number) {
    if (confirm("Excluir este treino?"))
      this.api.deleteTraining(id).subscribe({
        next: () => this.load(),
        error: () => (this.error = "Não foi possível excluir o treino."),
      });
  }
}
