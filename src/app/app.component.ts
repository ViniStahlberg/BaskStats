import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BaskstatsService } from './baskstats.service';
import { 
  AtletaDetalhada, DashData, PointDot, PointLabel, 
  PositionBar, PosicaoStat, ShootingBar, StatBar, StreakItem 
} from './baskstats.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  private baskService = inject(BaskstatsService);

  screen: 'login' | 'dash' | 'atletas' | 'atleta-detalhe' = 'login';

  email = '';
  password = '';
  remember = false;
  emailInvalid = false;
  passwordInvalid = false;

  userGreeting = 'treinadora';
  avatarInitial = 'H';

  dashData: DashData | null = null;
  atletas: AtletaDetalhada[] = [];
  atletaSelecionada: AtletaDetalhada | null = null;

  pointsGrid: number[] = [];
  pointsLine = '';
  pointsArea = '';
  pointsDots: PointDot[] = [];
  pointsLabels: PointLabel[] = [];

  wlPct = 0;
  wlDashArray = '';

  shootingBars: ShootingBar[] = [];
  positionBars: PositionBar[] = [];

  streakItems: StreakItem[] = [];
  streakWins = 0;

  atletaStatBars: StatBar[] = [];
  atletaShootBars: ShootingBar[] = [];

  onLogin(): void {
    const emailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email.trim());
    const passwordValid = this.password.length >= 6;

    this.emailInvalid = !emailValid;
    this.passwordInvalid = !passwordValid;

    if (!emailValid || !passwordValid) return;

    const name = this.email.trim().split('@')[0];
    this.userGreeting = name;
    this.avatarInitial = name.charAt(0).toUpperCase();

    this.dashData = this.baskService.gerarDados();
    this.calcularGraficos(this.dashData);
    this.atletas = this.baskService.gerarAtletas(10).sort((a, b) => b.ppj - a.ppj);
    this.dashData.jogadoras = this.atletas.slice(0, 5).map(a => ({ nome: a.nome, pos: a.pos, ppj: a.ppj, reb: a.reb, ast: a.ast }));
    this.screen = 'dash';
  }

  onLogout(): void {
    this.email = '';
    this.password = '';
    this.remember = false;
    this.emailInvalid = false;
    this.passwordInvalid = false;
    this.dashData = null;
    this.atletas = [];
    this.atletaSelecionada = null;
    this.screen = 'login';
  }

  abrirAtletas(): void { this.screen = 'atletas'; }
  voltarParaDashboard(): void { this.screen = 'dash'; }

  selecionarAtleta(atleta: AtletaDetalhada): void {
    this.atletaSelecionada = atleta;
    this.calcularGraficosAtleta(atleta);
    this.screen = 'atleta-detalhe';
  }

  voltarParaAtletas(): void {
    this.atletaSelecionada = null;
    this.screen = 'atletas';
  }

  private calcularGraficos(data: DashData): void {
    this.calcularPointsChart(data);
    this.calcularWLChart(data);
    this.calcularShootingChart(data);
    this.calcularPositionsChart(data);
    this.calcularStreakChart(data);
  }

  private calcularPointsChart(data: DashData): void {
    const w = 600, h = 240, padL = 32, padR = 12, padT = 16, padB = 30;
    const vals = data.pontosPorRodada;
    const max = Math.max(...vals), min = Math.min(...vals);
    const range = (max - min) || 1;
    const innerW = w - padL - padR, innerH = h - padT - padB;
    const stepX = innerW / (vals.length - 1);

    const pts = vals.map((v: number, i: number) => ({
      x: padL + i * stepX,
      y: padT + (1 - (v - min) / range) * innerH
    }));

    this.pointsLine = pts.map((p: { x: number; y: number }, i: number) => (i === 0 ? 'M' : 'L') + p.x.toFixed(1) + ',' + p.y.toFixed(1)).join(' ');
    this.pointsArea = this.pointsLine +
      ` L${pts[pts.length - 1].x.toFixed(1)},${(h - padB).toFixed(1)}` +
      ` L${pts[0].x.toFixed(1)},${(h - padB).toFixed(1)} Z`;

    this.pointsGrid = [0, 0.25, 0.5, 0.75, 1].map((f: number) => padT + f * innerH);
    this.pointsDots = pts.map((p: { x: number; y: number }, i: number) => ({ x: p.x, y: p.y, label: data.rodadas[i], val: vals[i] }));
    this.pointsLabels = pts.map((p: { x: number; y: number }) => ({ x: p.x, label: data.rodadas[pts.indexOf(p)] }));
  }

  private calcularWLChart(data: DashData): void {
    const total = data.vitorias + data.derrotas;
    const r = 69, c = 2 * Math.PI * r;
    const winFrac = total ? data.vitorias / total : 0;
    const winDash = winFrac * c;

    this.wlPct = Math.round(winFrac * 100);
    this.wlDashArray = `${winDash.toFixed(1)} ${(c - winDash).toFixed(1)}`;
  }

  private calcularShootingChart(data: DashData): void {
    const cats = [
      { label: '2 pontos', val: data.arremesso2, color: '#F2650C' },
      { label: '3 pontos', val: data.arremesso3, color: '#FF9B4D' },
      { label: 'Lance livre', val: data.lanceLivre, color: '#3FBE72' }
    ];
    const w = 400, h = 200, padB = 26, padT = 24, barW = 66, gap = 34, maxVal = 100;
    const totalW = cats.length * barW + (cats.length - 1) * gap;
    const startX = (w - totalW) / 2;

    this.shootingBars = cats.map((c: { label: string; val: number; color: string }, i: number) => {
      const x = startX + i * (barW + gap);
      const barH = (c.val / maxVal) * (h - padT - padB);
      const y = h - padB - barH;
      return {
        x, y, w: barW, h: barH, color: c.color,
        cx: x + barW / 2, vy: y - 8, val: c.val, label: c.label
      };
    });
  }

  private calcularPositionsChart(data: DashData): void {
    const w = 400, h = 200, padL = 90, padR = 20, padT = 12, padB = 12, maxVal = 20;
    const rows = data.pontosPorPosicao;
    const rowH = (h - padT - padB) / rows.length;
    const barMaxW = w - padL - padR;

    this.positionBars = rows.map((r: PosicaoStat, i: number) => {
      const y = padT + i * rowH + rowH * 0.22;
      const barH = rowH * 0.56;
      const barW = (r.media / maxVal) * barMaxW;
      return {
        pos: r.pos,
        labelX: padL - 10,
        textY: y + barH / 2 + 4,
        barX: padL,
        y, w: barW, h: barH,
        valueX: padL + barW + 8,
        media: r.media
      };
    });
  }

  private calcularStreakChart(data: DashData): void {
    this.streakItems = data.sequenciaJogos.map((r: 'V' | 'D') => ({
      result: r,
      color: r === 'V' ? '#3FBE72' : '#E23636'
    }));
    this.streakWins = this.streakItems.filter((s: StreakItem) => s.result === 'V').length;
  }

  private calcularGraficosAtleta(atleta: AtletaDetalhada): void {
    const w = 400, h = 210, padL = 100, padR = 30, padT = 12, padB = 12;
    const linhas = [
      { label: 'Pontos', value: atleta.ppj, max: 30 },
      { label: 'Rebotes', value: atleta.reb, max: 15 },
      { label: 'Assistências', value: atleta.ast, max: 10 },
      { label: 'Roubos de bola', value: atleta.roubos, max: 6 },
      { label: 'Tocos', value: atleta.tocos, max: 6 }
    ];
    const rowH = (h - padT - padB) / linhas.length;
    const barMaxW = w - padL - padR;

    this.atletaStatBars = linhas.map((l: { label: string; value: number; max: number }, i: number) => {
      const y = padT + i * rowH + rowH * 0.22;
      const barH = rowH * 0.56;
      const barW = Math.min(l.value / l.max, 1) * barMaxW;
      return {
        label: l.label,
        labelX: padL - 10,
        textY: y + barH / 2 + 4,
        barX: padL,
        y, w: barW, h: barH,
        valueX: padL + barW + 8,
        value: l.value
      };
    });

    const cats = [
      { label: '2 pontos', val: atleta.arremesso2, color: '#F2650C' },
      { label: '3 pontos', val: atleta.arremesso3, color: '#FF9B4D' },
      { label: 'Lance livre', val: atleta.lanceLivre, color: '#3FBE72' }
    ];
    const w2 = 400, h2 = 200, padB2 = 26, padT2 = 24, barW2 = 66, gap2 = 34, maxVal2 = 100;
    const totalW2 = cats.length * barW2 + (cats.length - 1) * gap2;
    const startX2 = (w2 - totalW2) / 2;

    this.atletaShootBars = cats.map((c: { label: string; val: number; color: string }, i: number) => {
      const x = startX2 + i * (barW2 + gap2);
      const barH = (c.val / maxVal2) * (h2 - padT2 - padB2);
      const y = h2 - padB2 - barH;
      return {
        x, y, w: barW2, h: barH, color: c.color,
        cx: x + barW2 / 2, vy: y - 8, val: c.val, label: c.label
      };
    });
  }
}