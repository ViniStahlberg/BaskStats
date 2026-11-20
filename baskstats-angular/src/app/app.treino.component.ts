
import {
  Component,
  OnInit,
  OnDestroy,
  inject,
  signal
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';

import { BaskstatsService } from './baskstats.service';
import {
  PlanoTreinoAtleta,
  PlanoTreinoResponse
} from './baskstats.model';

@Component({
  selector: 'app-treinos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="treinos-page">

      <!-- CABEÇALHO -->
      <header class="page-header">
        <div class="header-text">
          <span class="eyebrow">BASKSTATS PERFORMANCE</span>
          <h1>Planejamento de treinamento</h1>
          <p>
            Identifique pontos de evolução e direcione os treinamentos
            com base no desempenho das atletas.
          </p>
        </div>

        <div class="header-icon">
          <span>🏀</span>
        </div>
      </header>

      <!-- CONFIGURAÇÃO -->
      <form class="config-card" (ngSubmit)="carregar()">

        <div class="section-heading">
          <div>
            <h2>Configurar análise</h2>
            <p>Defina o fundamento e os parâmetros da avaliação.</p>
          </div>
        </div>

        <div class="filters-grid">

          <label class="field">
            <span>Fundamento analisado</span>

            <select
              [(ngModel)]="foco"
              name="foco"
              (ngModelChange)="onFocoChange($event)"
            >
              <option value="arremesso3">Arremesso de 3 pontos</option>
              <option value="arremesso2">Arremesso de 2 pontos</option>
              <option value="lanceLivre">Lance livre</option>
              <option value="ppj">Pontos por jogo</option>
              <option value="reb">Rebotes</option>
              <option value="ast">Assistências</option>
              <option value="roubos">Roubos de bola</option>
              <option value="turnovers">Turnovers</option>
              <option value="eficiencia">Eficiência</option>
              <option value="minutos">Minutos jogados</option>
            </select>
          </label>

          <label class="field">
            <span>Meta desejada</span>

            <div class="input-with-suffix">
              <input
                type="number"
                [(ngModel)]="meta"
                name="meta"
                step="0.5"
                min="0"
                placeholder="Defina uma meta"
              />

              <span class="suffix">{{ unidade }}</span>
            </div>
          </label>

          <label class="field">
            <span>Desempenho recente</span>

            <select [(ngModel)]="ultimosJogos" name="ultimosJogos">
              <option [ngValue]="3">Últimos 3 jogos</option>
              <option [ngValue]="5">Últimos 5 jogos</option>
              <option [ngValue]="10">Últimos 10 jogos</option>
              <option [ngValue]="15">Últimos 15 jogos</option>
              <option [ngValue]="30">Últimos 30 jogos</option>
            </select>
          </label>

          <label class="field">
            <span>Posição</span>

            <select [(ngModel)]="posicao" name="posicao">
              <option value="TODAS">Todas as posições</option>
              <option value="ARMADORA">Armadora</option>
              <option value="ALA">Ala</option>
              <option value="ALAPIVO">Ala-pivô</option>
              <option value="PIVO">Pivô</option>
            </select>
          </label>

        </div>

        <div class="config-footer">
          <span class="config-hint">
            A análise utiliza os dados registrados nas partidas.
          </span>

          <button
            class="btn-analisar"
            type="submit"
            [disabled]="carregando()"
          >
            <span>{{ carregando() ? 'Analisando...' : 'Analisar desempenho' }}</span>
            <span *ngIf="!carregando()">→</span>
          </button>
        </div>
      </form>

      <!-- LOADING -->
      <div class="loading-box" *ngIf="carregando()">
        <div class="loader"></div>
        <span>Calculando indicadores das atletas...</span>
      </div>

      <!-- ERRO -->
      <div class="error-box" *ngIf="erro()">
        <span class="error-icon">!</span>
        <div>
          <strong>Não foi possível realizar a análise</strong>
          <p>{{ erro() }}</p>
        </div>
      </div>

      <!-- RESULTADO -->
      <ng-container *ngIf="plano() as p">

        <section class="results-section" *ngIf="!carregando()">

          <!-- RESUMO -->
          <div class="summary-grid">

            <article class="summary-card">
              <span class="summary-label">Atletas analisadas</span>
              <strong class="summary-value">{{ p.totalAtletas }}</strong>
              <span class="summary-description">No filtro selecionado</span>
            </article>

            <article class="summary-card highlight">
              <span class="summary-label">Atletas prioritárias</span>
              <strong class="summary-value">{{ p.atletasEmFoco }}</strong>
              <span class="summary-description">Abaixo da meta definida</span>
            </article>

            <article class="summary-card">
              <span class="summary-label">Objetivo</span>
              <strong class="summary-value">
                {{ p.meta }}{{ unidade }}
              </strong>
              <span class="summary-description">{{ nomeFoco }}</span>
            </article>

            <article class="summary-card">
              <span class="summary-label">Amostra recente</span>
              <strong class="summary-value">{{ p.ultimosJogos }}</strong>
              <span class="summary-description">Partidas consideradas</span>
            </article>

          </div>

          <!-- LISTA DE ATLETAS -->
          <div class="athletes-header">
            <div>
              <h2>Prioridades individuais</h2>
              <p>
                Atletas ordenadas de acordo com a distância em relação à meta.
              </p>
            </div>

            <span class="athletes-count">
              {{ atletas().length }} atletas
            </span>
          </div>

          <div class="empty-state" *ngIf="atletas().length === 0">
            <span class="empty-icon">🏀</span>
            <h3>Nenhuma atleta encontrada</h3>
            <p>Altere os filtros para visualizar outros resultados.</p>
          </div>

          <div class="athletes-list" *ngIf="atletas().length > 0">

            <article
              class="athlete-card"
              *ngFor="let a of atletas()"
              [class.meta-atingida]="a.statusMeta === 'ATINGIDA'"
            >

              <!-- IDENTIFICAÇÃO -->
              <div class="athlete-header">

                <div class="athlete-identification">
                  <div class="jersey">
                    {{ a.numero !== null ? a.numero : '—' }}
                  </div>

                  <div class="athlete-name">
                    <h3>{{ a.nome }}</h3>
                    <span>
                      {{ a.posicao }} · {{ a.jogos }} jogos na temporada
                    </span>
                  </div>
                </div>

                <div
                  class="status-badge"
                  [class.status-ok]="a.statusMeta === 'ATINGIDA'"
                >
                  <span class="status-dot"></span>
                  {{ a.statusMeta === 'ATINGIDA' ? 'Meta atingida' : 'Em desenvolvimento' }}
                </div>

              </div>

              <!-- METRICAS -->
              <div class="performance-grid">

                <div class="performance-item">
                  <span class="performance-label">Temporada</span>
                  <strong>{{ formatarValor(a.valorAtual) }}</strong>
                  <small>Desempenho médio</small>
                </div>

                <div class="performance-item recent">
                  <span class="performance-label">Últimos {{ p.ultimosJogos }} jogos</span>
                  <strong>{{ formatarValor(a.valorRecente) }}</strong>
                  <small>Forma recente</small>
                </div>

                <div class="performance-item">
                  <span class="performance-label">Evolução recente</span>
                  <strong
                    [class.delta-positive]="deltaPositivo(a)"
                    [class.delta-negative]="deltaNegativo(a)"
                    [class.delta-neutral]="a.deltaRecente === 0"
                  >
                    {{ a.deltaRecente > 0 ? '+' : '' }}{{ formatarValorDelta(a.deltaRecente) }}
                  </strong>
                  <small>{{ textoEvolucao(a) }}</small>
                </div>

              </div>

              <!-- GAP -->
              <div class="goal-section" *ngIf="a.statusMeta === 'EM_FOCO'">

                <div class="goal-header">
                  <span>Distância para a meta</span>
                  <strong>
                    {{ formatarValor(a.gapParaMeta) }}{{ unidade }}
                  </strong>
                </div>

                <div class="goal-track">
                  <div
                    class="goal-fill"
                    [style.width.%]="progressoMeta(a)"
                  ></div>
                </div>

                <div class="goal-footer">
                  <span>Atual: {{ formatarValor(a.valorAtual) }}{{ unidade }}</span>
                  <span>Meta: {{ p.meta }}{{ unidade }}</span>
                </div>

              </div>

              <div class="goal-achieved" *ngIf="a.statusMeta === 'ATINGIDA'">
                <span>✓</span>
                <p>
                  Objetivo alcançado. Priorize consistência e situações de jogo.
                </p>
              </div>

              <!-- RECOMENDACAO -->
              <div class="recommendation">

                <div class="recommendation-title">
                  <span class="recommendation-icon">✦</span>
                  <strong>Direcionamento de treinamento</strong>
                </div>

                <p>{{ a.recomendacaoTreino }}</p>

              </div>

            </article>

          </div>

        </section>

      </ng-container>

      <!-- PRIMEIRO ACESSO -->
      <div
        class="initial-state"
        *ngIf="!plano() && !carregando() && !erro()"
      >
        <div class="initial-icon">🏀</div>
        <h2>Pronto para analisar o desempenho</h2>
        <p>
          Configure os fundamentos, a meta e o período de análise.
          Os resultados serão calculados a partir das estatísticas reais do time.
        </p>
      </div>

    </section>
  `,

  styles: [`
    :host {
      display: block;
    }

    .treinos-page {
      padding: 1.5rem;
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
      color: #f4f4f5;
    }

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 1rem;
    }

    .eyebrow {
      color: #fb923c;
      font-size: .72rem;
      font-weight: 800;
      letter-spacing: .15em;
    }

    .header-text h1 {
      margin: .4rem 0;
      font-size: 1.65rem;
      font-weight: 800;
      letter-spacing: -.04em;
    }

    .header-text p {
      margin: 0;
      color: #a1a1aa;
      font-size: .9rem;
    }

    .header-icon {
      width: 54px;
      height: 54px;
      border-radius: 15px;
      background: rgba(249, 115, 22, .12);
      border: 1px solid rgba(249, 115, 22, .25);
      display: grid;
      place-items: center;
      font-size: 1.5rem;
      flex-shrink: 0;
    }

    .config-card {
      background: #18181d;
      border: 1px solid rgba(255,255,255,.08);
      border-radius: 15px;
      padding: 1.25rem;
    }

    .section-heading h2 {
      margin: 0;
      font-size: 1.05rem;
      font-weight: 750;
    }

    .section-heading p {
      margin: .35rem 0 1.15rem;
      color: #a1a1aa;
      font-size: .82rem;
    }

    .filters-grid {
      display: grid;
      grid-template-columns: repeat(4, minmax(0, 1fr));
      gap: .9rem;
    }

    .field {
      display: flex;
      flex-direction: column;
      gap: .45rem;
    }

    .field > span {
      font-size: .78rem;
      color: #c4c4cc;
      font-weight: 600;
    }

    .field select,
    .field input {
      width: 100%;
      box-sizing: border-box;
      height: 42px;
      background: #101014;
      color: #f4f4f5;
      border: 1px solid rgba(255,255,255,.12);
      border-radius: 9px;
      padding: 0 .75rem;
      outline: none;
      font-size: .85rem;
      transition: border-color .2s;
    }

    .field select:focus,
    .field input:focus {
      border-color: #f97316;
    }

    .input-with-suffix {
      position: relative;
    }

    .input-with-suffix input {
      padding-right: 3rem;
    }

    .suffix {
      position: absolute;
      right: .75rem;
      top: 50%;
      transform: translateY(-50%);
      color: #a1a1aa;
      font-size: .8rem;
      pointer-events: none;
    }

    .config-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 1rem;
      margin-top: 1.15rem;
      padding-top: 1rem;
      border-top: 1px solid rgba(255,255,255,.07);
    }

    .config-hint {
      font-size: .78rem;
      color: #888891;
    }

    .btn-analisar {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: .8rem;
      background: #ea580c;
      color: white;
      border: 0;
      border-radius: 9px;
      padding: .75rem 1.1rem;
      font-size: .85rem;
      font-weight: 750;
      cursor: pointer;
      transition: background .2s, transform .2s;
    }

    .btn-analisar:hover:not(:disabled) {
      background: #f97316;
      transform: translateY(-1px);
    }

    .btn-analisar:disabled {
      opacity: .6;
      cursor: wait;
    }

    .summary-grid {
      display: grid;
      grid-template-columns: repeat(4, minmax(0, 1fr));
      gap: .9rem;
      margin-bottom: 2rem;
    }

    .summary-card {
      background: #18181d;
      border: 1px solid rgba(255,255,255,.08);
      border-radius: 12px;
      padding: 1rem;
      display: flex;
      flex-direction: column;
      gap: .35rem;
    }

    .summary-card.highlight {
      border-color: rgba(249,115,22,.4);
      background: linear-gradient(
        145deg,
        rgba(249,115,22,.10),
        #18181d 65%
      );
    }

    .summary-label {
      color: #a1a1aa;
      font-size: .76rem;
      font-weight: 600;
    }

    .summary-value {
      font-size: 1.7rem;
      font-weight: 850;
      letter-spacing: -.04em;
    }

    .highlight .summary-value {
      color: #fb923c;
    }

    .summary-description {
      color: #777781;
      font-size: .72rem;
    }

    .athletes-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 1rem;
      margin-bottom: 1rem;
    }

    .athletes-header h2 {
      margin: 0;
      font-size: 1.1rem;
    }

    .athletes-header p {
      color: #a1a1aa;
      margin: .35rem 0 0;
      font-size: .8rem;
    }

    .athletes-count {
      color: #c4c4cc;
      background: rgba(255,255,255,.05);
      border: 1px solid rgba(255,255,255,.08);
      border-radius: 999px;
      padding: .35rem .7rem;
      font-size: .75rem;
      white-space: nowrap;
    }

    .athletes-list {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 1rem;
    }

    .athlete-card {
      background: #18181d;
      border: 1px solid rgba(255,255,255,.08);
      border-left: 3px solid #f97316;
      border-radius: 13px;
      padding: 1rem;
      display: flex;
      flex-direction: column;
      gap: 1rem;
      min-width: 0;
      transition: border-color .2s;
    }

    .athlete-card.meta-atingida {
      border-left-color: #22c55e;
    }

    .athlete-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      gap: .8rem;
    }

    .athlete-identification {
      display: flex;
      align-items: center;
      gap: .75rem;
      min-width: 0;
    }

    .jersey {
      width: 42px;
      height: 42px;
      display: grid;
      place-items: center;
      flex-shrink: 0;
      background: rgba(249,115,22,.12);
      color: #fb923c;
      border: 1px solid rgba(249,115,22,.2);
      border-radius: 10px;
      font-size: 1rem;
      font-weight: 800;
    }

    .athlete-name {
      min-width: 0;
    }

    .athlete-name h3 {
      margin: 0;
      font-size: .95rem;
      font-weight: 750;
      overflow-wrap: anywhere;
    }

    .athlete-name span {
      display: block;
      color: #888891;
      font-size: .73rem;
      margin-top: .25rem;
    }

    .status-badge {
      display: flex;
      align-items: center;
      gap: .4rem;
      flex-shrink: 0;
      color: #fdba74;
      background: rgba(249,115,22,.12);
      padding: .4rem .55rem;
      border-radius: 999px;
      font-size: .68rem;
      font-weight: 700;
    }

    .status-badge.status-ok {
      color: #86efac;
      background: rgba(34,197,94,.12);
    }

    .status-dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: currentColor;
    }

    .performance-grid {
      display: grid;
      grid-template-columns: repeat(3, minmax(0, 1fr));
      background: #111115;
      border: 1px solid rgba(255,255,255,.04);
      border-radius: 10px;
      padding: .75rem .4rem;
    }

    .performance-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: .25rem;
      min-width: 0;
      text-align: center;
    }

    .performance-item + .performance-item {
      border-left: 1px solid rgba(255,255,255,.07);
    }

    .performance-label {
      color: #85858f;
      font-size: .67rem;
      font-weight: 600;
    }

    .performance-item strong {
      font-size: 1.05rem;
      font-weight: 800;
    }

    .performance-item small {
      color: #666671;
      font-size: .65rem;
    }

    .delta-positive {
      color: #4ade80;
    }

    .delta-negative {
      color: #f87171;
    }

    .delta-neutral {
      color: #c4c4cc;
    }

    .goal-section {
      display: flex;
      flex-direction: column;
      gap: .55rem;
    }

    .goal-header,
    .goal-footer {
      display: flex;
      justify-content: space-between;
      gap: .5rem;
    }

    .goal-header {
      color: #b4b4bd;
      font-size: .76rem;
      align-items: center;
    }

    .goal-header strong {
      color: #fb923c;
      font-size: .82rem;
    }

    .goal-track {
      height: 6px;
      border-radius: 99px;
      background: #303038;
      overflow: hidden;
    }

    .goal-fill {
      height: 100%;
      border-radius: inherit;
      background: linear-gradient(90deg, #ea580c, #fb923c);
      transition: width .4s ease;
    }

    .goal-footer {
      color: #777781;
      font-size: .68rem;
    }

    .goal-achieved {
      display: flex;
      align-items: center;
      gap: .5rem;
      padding: .6rem .75rem;
      background: rgba(34,197,94,.08);
      border: 1px solid rgba(34,197,94,.12);
      border-radius: 8px;
    }

    .goal-achieved span {
      color: #4ade80;
      font-weight: 900;
    }

    .goal-achieved p {
      margin: 0;
      color: #86efac;
      font-size: .75rem;
    }

    .recommendation {
      background: rgba(255,255,255,.025);
      border: 1px solid rgba(255,255,255,.06);
      border-radius: 9px;
      padding: .75rem;
    }

    .recommendation-title {
      display: flex;
      align-items: center;
      gap: .45rem;
      color: #fb923c;
      font-size: .76rem;
    }

    .recommendation-icon {
      font-size: .95rem;
    }

    .recommendation p {
      color: #c4c4cc;
      font-size: .76rem;
      line-height: 1.55;
      margin: .5rem 0 0;
    }

    .loading-box {
      display: flex;
      align-items: center;
      gap: .75rem;
      padding: 1.2rem;
      color: #a1a1aa;
      background: #18181d;
      border: 1px solid rgba(255,255,255,.08);
      border-radius: 12px;
      font-size: .85rem;
    }

    .loader {
      width: 18px;
      height: 18px;
      border: 2px solid rgba(249,115,22,.2);
      border-top-color: #f97316;
      border-radius: 50%;
      animation: spin .8s linear infinite;
    }

    @keyframes spin {
      to { transform: rotate(360deg); }
    }

    .error-box {
      display: flex;
      align-items: center;
      gap: .75rem;
      background: rgba(239,68,68,.08);
      border: 1px solid rgba(239,68,68,.2);
      border-radius: 10px;
      padding: 1rem;
      color: #fca5a5;
    }

    .error-icon {
      width: 26px;
      height: 26px;
      display: grid;
      place-items: center;
      border: 1px solid rgba(239,68,68,.35);
      border-radius: 50%;
      font-weight: 800;
      flex-shrink: 0;
    }

    .error-box strong {
      font-size: .85rem;
    }

    .error-box p {
      margin: .3rem 0 0;
      font-size: .76rem;
    }

    .empty-state,
    .initial-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      padding: 3rem 1.5rem;
      background: #18181d;
      border: 1px dashed rgba(255,255,255,.12);
      border-radius: 14px;
    }

    .empty-icon,
    .initial-icon {
      font-size: 2rem;
      margin-bottom: .8rem;
    }

    .empty-state h3,
    .initial-state h2 {
      margin: 0;
      font-size: 1rem;
    }

    .empty-state p,
    .initial-state p {
      max-width: 440px;
      color: #92929c;
      font-size: .82rem;
      line-height: 1.5;
      margin: .5rem 0 0;
    }

    @media (max-width: 1100px) {
      .filters-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
      }

      .summary-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
      }

      .athletes-list {
        grid-template-columns: 1fr;
      }
    }

    @media (max-width: 650px) {
      .treinos-page {
        padding: 1rem;
      }

      .page-header {
        align-items: flex-start;
      }

      .header-text h1 {
        font-size: 1.35rem;
      }

      .header-icon {
        width: 44px;
        height: 44px;
      }

      .filters-grid {
        grid-template-columns: 1fr;
      }

      .config-footer {
        align-items: stretch;
        flex-direction: column;
      }

      .btn-analisar {
        width: 100%;
      }

      .summary-grid {
        gap: .6rem;
      }

      .summary-card {
        padding: .8rem;
      }

      .summary-value {
        font-size: 1.4rem;
      }

      .athlete-header {
        flex-direction: column;
      }

      .status-badge {
        align-self: flex-start;
      }

      .performance-label {
        font-size: .62rem;
      }

      .performance-item strong {
        font-size: .92rem;
      }
    }
  `]
})
export class TreinosComponent implements OnInit, OnDestroy {

  private readonly api = inject(BaskstatsService);
  private teamSubscription?: Subscription;

  timeId = 1;

  foco = 'arremesso3';
  meta: number | null = 35;
  ultimosJogos = 5;
  posicao = 'TODAS';

  plano = signal<PlanoTreinoResponse | null>(null);
  atletas = signal<PlanoTreinoAtleta[]>([]);
  carregando = signal(false);
  erro = signal<string | null>(null);

  private readonly metasPadrao: Record<string, number> = {
    arremesso3: 35,
    arremesso2: 50,
    lanceLivre: 75,
    turnovers: 2,
    eficiencia: 12,
    reb: 6,
    ast: 4,
    ppj: 12,
    roubos: 1,
    minutos: 20
  };

  ngOnInit(): void {
    this.teamSubscription = this.api.teamId.subscribe(id => {
      this.timeId = id;
      this.carregar();
    });
  }

  ngOnDestroy(): void {
    this.teamSubscription?.unsubscribe();
  }

  onFocoChange(novoFoco: string): void {
    this.meta = this.metasPadrao[novoFoco] ?? null;
  }

  carregar(): void {
    if (!this.timeId) return;

    this.carregando.set(true);
    this.erro.set(null);

    this.api.getPlanoTreino(this.timeId, {
      foco: this.foco,
      meta: this.meta,
      ultimosJogos: this.ultimosJogos,
      posicao: this.posicao
    }).subscribe({
      next: res => {
        this.plano.set(res);
        this.atletas.set(res.atletas ?? []);
        this.carregando.set(false);
      },
      error: err => {
        console.error('Erro ao carregar plano de treino:', err);
        this.plano.set(null);
        this.atletas.set([]);
        this.erro.set(
          'Verifique se o time possui partidas e estatísticas cadastradas.'
 
        );
        this.carregando.set(false);
      }
    });
  }

  get nomeFoco(): string {
    const nomes: Record<string, string> = {
      arremesso3: 'Arremesso de 3 pontos',
      arremesso2: 'Arremesso de 2 pontos',
      lanceLivre: 'Lance livre',
      ppj: 'Pontos por jogo',
      reb: 'Rebotes',
      ast: 'Assistências',
      roubos: 'Roubos de bola',
      turnovers: 'Turnovers',
      eficiencia: 'Eficiência',
      minutos: 'Minutos jogados'
    };

    return nomes[this.foco] ?? this.foco;
  }

  get unidade(): string {
    return ['arremesso3', 'arremesso2', 'lanceLivre'].includes(this.foco)
      ? '%'
      : '';
  }

  formatarValor(valor: number): string {
    return Number(valor ?? 0).toLocaleString('pt-BR', {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
  }

  formatarValorDelta(valor: number): string {
    return Number(valor ?? 0).toLocaleString('pt-BR', {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
  }

  deltaPositivo(a: PlanoTreinoAtleta): boolean {
    if (a.deltaRecente === 0) return false;

    // Turnovers: cair é melhorar.
    if (this.foco === 'turnovers') {
      return a.deltaRecente < 0;
    }

    return a.deltaRecente > 0;
  }

  deltaNegativo(a: PlanoTreinoAtleta): boolean {
    if (a.deltaRecente === 0) return false;

    if (this.foco === 'turnovers') {
      return a.deltaRecente > 0;
    }

    return a.deltaRecente < 0;
  }

  textoEvolucao(a: PlanoTreinoAtleta): string {
    if (a.deltaRecente === 0) {
      return 'Sem variação';
    }

    if (this.deltaPositivo(a)) {
      return 'Evolução positiva';
    }

    return 'Queda recente';
  }

  progressoMeta(a: PlanoTreinoAtleta): number {
    const atual = Number(a.valorAtual ?? 0);
    const meta = Number(a.meta ?? 0);

    if (meta <= 0) return 0;

    // Métrica de redução: meta / valor atual
    if (this.foco === 'turnovers') {
      if (atual <= meta) return 100;
      return Math.min(100, (meta / atual) * 100);
    }

    return Math.max(0, Math.min(100, (atual / meta) * 100));
  }
}
