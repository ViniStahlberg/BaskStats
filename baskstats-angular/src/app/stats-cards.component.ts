import { Component, Input } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink } from "@angular/router";
import { Dashboard } from "./baskstats.model";
@Component({
  selector: "app-points-card",
  standalone: true,
  imports: [CommonModule],
  template: `<article class="card">
    <h2>Pontos por rodada</h2>
    <div class="chart">
      <svg viewBox="0 0 600 225" role="img" aria-label="Pontos por rodada">
        <line
          *ngFor="let y of [30, 80, 130, 180]"
          x1="30"
          x2="578"
          [attr.y1]="y"
          [attr.y2]="y"
          class="gridline"
        />
        <polygon
          *ngIf="data.pontosPorRodada.length"
          [attr.points]="'30,185 ' + points + ' ' + lastX + ',185'"
          fill="rgba(255,106,0,.19)"
        />
        <polyline
          [attr.points]="points"
          fill="none"
          stroke="var(--orange)"
          stroke-width="2"
        />
        <g *ngFor="let v of data.pontosPorRodada; let i = index">
          <circle [attr.cx]="x(i)" [attr.cy]="y(v)" r="3" fill="var(--orange)">
            <title>{{ data.rodadas[i] }}: {{ v }} pontos</title>
          </circle>
          <text [attr.x]="x(i)" y="211" text-anchor="middle">
            {{ data.rodadas[i] }}
          </text>
        </g>
        <text
          *ngIf="!data.pontosPorRodada.length"
          x="300"
          y="110"
          text-anchor="middle"
        >
          Nenhuma partida disputada
        </text>
      </svg>
    </div>
  </article>`,
})
export class PointsCardComponent {
  @Input({ required: true }) data!: Dashboard;
  x(i: number) {
    return 30 + (i * 548) / Math.max(1, this.data.pontosPorRodada.length - 1);
  }
  y(v: number) {
    const min = Math.min(...this.data.pontosPorRodada);
    const max = Math.max(...this.data.pontosPorRodada);
    return max === min ? 105 : 180 - ((v - min) / (max - min)) * 150;
  }
  get points() {
    return this.data.pontosPorRodada
      .map((v, i) => this.x(i) + "," + this.y(v))
      .join(" ");
  }
  get lastX() {
    return this.x(this.data.pontosPorRodada.length - 1);
  }
}
@Component({
  selector: "app-results-card",
  standalone: true,
  template: `<article class="card">
    <h2>Vitória x derrota</h2>
    <div class="donut-wrap">
      <div
        class="donut"
        [style.background]="
          'conic-gradient(var(--orange) 0% ' +
          pct +
          '%, var(--muted) ' +
          pct +
          '% 100%)'
        "
      >
        <div>
          <strong>{{ pct }}%</strong><span>vitórias</span>
        </div>
      </div>
      <div class="legend">
        <span><i></i>Vitórias ({{ data.vitorias }})</span
        ><span><i class="gray"></i>Derrotas ({{ data.derrotas }})</span>
      </div>
    </div>
  </article>`,
})
export class ResultsCardComponent {
  @Input({ required: true }) data!: Dashboard;
  get pct() {
    return Math.round(
      (100 * this.data.vitorias) /
        (this.data.vitorias + this.data.derrotas || 1),
    );
  }
}
@Component({
  selector: "app-shooting-card",
  standalone: true,
  imports: [CommonModule],
  template: `<article class="card">
    <h2>Aproveitamento de arremessos</h2>
    <div class="shooting">
      <div *ngFor="let b of bars; let i = index" class="bar-column">
        <span>{{ b[1] }}%</span>
        <div
          [style.height.%]="b[1]"
          [style.background]="i === 1 ? '#c64a00' : 'var(--orange)'"
        ></div>
        <small>{{ b[0] }}</small>
      </div>
    </div>
  </article>`,
})
export class ShootingCardComponent {
  @Input({ required: true }) data!: Pick<
    Dashboard,
    "arremesso2" | "arremesso3" | "lanceLivre"
  >;
  get bars() {
    return [
      ["2 pontos", this.data.arremesso2],
      ["3 pontos", this.data.arremesso3],
      ["Lance livre", this.data.lanceLivre],
    ];
  }
}
@Component({
  selector: "app-scorers-card",
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `<article class="card">
    <h2>Maiores pontuadoras</h2>
    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Atleta</th>
            <th>Pos.</th>
            <th>PPJ</th>
            <th>REB</th>
            <th>AST</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let j of data.jogadoras.slice(0, 6)">
            <td>
              <a [routerLink]="['/jogadoras', j.id]">{{ j.nome }}</a>
            </td>
            <td class="accent">{{ j.pos }}</td>
            <td>{{ j.ppj | number: "1.0-1" }}</td>
            <td>{{ j.reb | number: "1.0-1" }}</td>
            <td>{{ j.ast | number: "1.0-1" }}</td>
          </tr>
          <tr *ngIf="!data.jogadoras.length">
            <td colspan="5">Sem estatísticas nesta seleção.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </article>`,
})
export class ScorersCardComponent {
  @Input({ required: true }) data!: Dashboard;
}
@Component({
  selector: "app-positions-card",
  standalone: true,
  imports: [CommonModule],
  template: `<article class="card">
    <h2>Pontos médios por posição</h2>
    <div class="positions">
      <div *ngFor="let p of data.pontosPorPosicao">
        <span>{{ p.pos }}</span>
        <div>
          <i [style.width.%]="(p.media / max) * 88"></i
          ><b>{{ p.media | number: "1.0-1" }}</b>
        </div>
      </div>
      <p *ngIf="!data.pontosPorPosicao.length" class="empty">
        Posições ainda não cadastradas.
      </p>
    </div>
  </article>`,
})
export class PositionsCardComponent {
  @Input({ required: true }) data!: Dashboard;
  get max() {
    return Math.max(1, ...this.data.pontosPorPosicao.map((p) => p.media));
  }
}
@Component({
  selector: "app-stats-grid",
  standalone: true,
  imports: [
    PointsCardComponent,
    ResultsCardComponent,
    ShootingCardComponent,
    ScorersCardComponent,
    PositionsCardComponent,
  ],
  template: `<div class="stats-grid">
    <app-points-card [data]="data" /><app-results-card
      [data]="data"
    /><app-shooting-card [data]="data" /><app-scorers-card
      [data]="data"
    /><app-positions-card [data]="data" />
  </div>`,
})
export class StatsGridComponent {
  @Input({ required: true }) data!: Dashboard;
}
