import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Jogadora {
  nome: string;
  pos: string;
  ppj: number;
  reb: number;
  ast: number;
}

interface AtletaDetalhada {
  id: number;
  nome: string;
  pos: string;
  numero: number;
  jogos: number;
  minutos: number;
  ppj: number;
  reb: number;
  ast: number;
  roubos: number;
  tocos: number;
  turnovers: number;
  arremesso2: number;
  arremesso3: number;
  lanceLivre: number;
  eficiencia: number;
}

interface PosicaoStat {
  pos: string;
  media: number;
}

interface DashData {
  rodadas: string[];
  pontosPorRodada: number[];
  vitorias: number;
  derrotas: number;
  jogadoras: Jogadora[];
  arremesso2: number;
  arremesso3: number;
  lanceLivre: number;
  roubosBola: number;
  tocos: number;
  pontosPorPosicao: PosicaoStat[];
  sequenciaJogos: ('V' | 'D')[];
}

interface PointDot { x: number; y: number; label: string; val: number; }
interface PointLabel { x: number; label: string; }
interface ShootingBar { x: number; y: number; w: number; h: number; color: string; cx: number; vy: number; val: number; label: string; }
interface PositionBar { pos: string; labelX: number; textY: number; barX: number; y: number; w: number; h: number; valueX: number; media: number; }
interface StreakItem { result: 'V' | 'D'; color: string; }
interface StatBar { label: string; labelX: number; textY: number; barX: number; y: number; w: number; h: number; valueX: number; value: number; }

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
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

  // geometria pré-calculada dos gráficos (SVG puro, sem lib externa)
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

  // gráficos da tela de estatísticas individuais
  atletaStatBars: StatBar[] = [];
  atletaShootBars: ShootingBar[] = [];

  private readonly PRIMEIRO_NOMES = ['Ana','Beatriz','Camila','Débora','Emanuelle','Fernanda','Gabriela','Helena','Isabela','Juliana','Kamila','Larissa'];
  private readonly SOBRENOMES = ['Silva','Santos','Oliveira','Souza','Costa','Pereira','Almeida','Rodrigues','Ferreira','Lima'];
  private readonly POSICOES = ['Armadora','Ala','Ala-Pivô','Pivô'];

  onLogin(): void {
    const emailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email.trim());
    const passwordValid = this.password.length >= 6;

    this.emailInvalid = !emailValid;
    this.passwordInvalid = !passwordValid;

    if (!emailValid || !passwordValid) return;

    const name = this.email.trim().split('@')[0];
    this.userGreeting = name;
    this.avatarInitial = name.charAt(0).toUpperCase();

    this.dashData = this.gerarDados();
    this.calcularGraficos(this.dashData);
    this.atletas = this.gerarAtletas(10).sort((a, b) => b.ppj - a.ppj);
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

  abrirAtletas(): void {
    this.screen = 'atletas';
  }

  voltarParaDashboard(): void {
    this.screen = 'dash';
  }

  selecionarAtleta(atleta: AtletaDetalhada): void {
    this.atletaSelecionada = atleta;
    this.calcularGraficosAtleta(atleta);
    this.screen = 'atleta-detalhe';
  }

  voltarParaAtletas(): void {
    this.atletaSelecionada = null;
    this.screen = 'atletas';
  }

  /* ---------- geração de dados fake ---------- */

  private rand(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  private nomeAleatorio(): string {
    const p = this.PRIMEIRO_NOMES[this.rand(0, this.PRIMEIRO_NOMES.length - 1)];
    const s = this.SOBRENOMES[this.rand(0, this.SOBRENOMES.length - 1)];
    return `${p} ${s}`;
  }

  private gerarDados(): DashData {
    const rodadas = Array.from({ length: 10 }, (_, i) => `R${i + 1}`);
    const pontosPorRodada = rodadas.map(() => this.rand(58, 92));

    const vitorias = this.rand(10, 18);
    const derrotas = this.rand(4, 12);

    const jogadoras: Jogadora[] = Array.from({ length: 5 }, () => ({
      nome: this.nomeAleatorio(),
      pos: this.POSICOES[this.rand(0, this.POSICOES.length - 1)],
      ppj: this.rand(9, 22),
      reb: this.rand(2, 11),
      ast: this.rand(1, 8)
    })).sort((a, b) => b.ppj - a.ppj);

    const arremesso2 = this.rand(42, 58);
    const arremesso3 = this.rand(28, 40);
    const lanceLivre = this.rand(65, 85);

    const roubosBola = this.rand(5, 12);
    const tocos = this.rand(2, 8);

    const pontosPorPosicao: PosicaoStat[] = this.POSICOES.map(pos => ({
      pos,
      media: this.rand(6, 16)
    }));

    const sequenciaJogos: ('V' | 'D')[] = Array.from({ length: 10 }, () => (Math.random() < 0.6 ? 'V' : 'D'));

    return {
      rodadas, pontosPorRodada, vitorias, derrotas, jogadoras,
      arremesso2, arremesso3, lanceLivre,
      roubosBola, tocos, pontosPorPosicao, sequenciaJogos
    };
  }

  private gerarAtletas(qtd: number): AtletaDetalhada[] {
    const numerosUsados = new Set<number>();
    return Array.from({ length: qtd }, (_, i) => {
      let numero = this.rand(0, 99);
      while (numerosUsados.has(numero)) numero = this.rand(0, 99);
      numerosUsados.add(numero);

      const jogos = this.rand(12, 22);
      const ppj = this.rand(4, 22);
      const reb = this.rand(1, 11);
      const ast = this.rand(0, 8);
      const roubos = this.rand(0, 4);
      const tocos = this.rand(0, 3);
      const turnovers = this.rand(1, 5);

      return {
        id: i + 1,
        nome: this.nomeAleatorio(),
        pos: this.POSICOES[this.rand(0, this.POSICOES.length - 1)],
        numero,
        jogos,
        minutos: this.rand(14, 34),
        ppj, reb, ast, roubos, tocos, turnovers,
        arremesso2: this.rand(38, 60),
        arremesso3: this.rand(20, 42),
        lanceLivre: this.rand(60, 90),
        eficiencia: Math.round(ppj + reb + ast + roubos + tocos - turnovers)
      };
    });
  }

  /* ---------- geometria dos gráficos SVG ---------- */

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

    const pts = vals.map((v, i) => ({
      x: padL + i * stepX,
      y: padT + (1 - (v - min) / range) * innerH
    }));

    this.pointsLine = pts.map((p, i) => (i === 0 ? 'M' : 'L') + p.x.toFixed(1) + ',' + p.y.toFixed(1)).join(' ');
    this.pointsArea = this.pointsLine +
      ` L${pts[pts.length - 1].x.toFixed(1)},${(h - padB).toFixed(1)}` +
      ` L${pts[0].x.toFixed(1)},${(h - padB).toFixed(1)} Z`;

    this.pointsGrid = [0, 0.25, 0.5, 0.75, 1].map(f => padT + f * innerH);

    this.pointsDots = pts.map((p, i) => ({ x: p.x, y: p.y, label: data.rodadas[i], val: vals[i] }));
    this.pointsLabels = pts.map(p => ({ x: p.x, label: data.rodadas[pts.indexOf(p)] }));
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

    this.shootingBars = cats.map((c, i) => {
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

    this.positionBars = rows.map((r, i) => {
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
    this.streakItems = data.sequenciaJogos.map(r => ({
      result: r,
      color: r === 'V' ? '#3FBE72' : '#E23636'
    }));
    this.streakWins = this.streakItems.filter(s => s.result === 'V').length;
  }

  private calcularGraficosAtleta(atleta: AtletaDetalhada): void {
    // barras horizontais com as principais estatísticas por jogo
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

    this.atletaStatBars = linhas.map((l, i) => {
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

    // aproveitamento de arremessos da atleta
    const cats = [
      { label: '2 pontos', val: atleta.arremesso2, color: '#F2650C' },
      { label: '3 pontos', val: atleta.arremesso3, color: '#FF9B4D' },
      { label: 'Lance livre', val: atleta.lanceLivre, color: '#3FBE72' }
    ];
    const w2 = 400, h2 = 200, padB2 = 26, padT2 = 24, barW2 = 66, gap2 = 34, maxVal2 = 100;
    const totalW2 = cats.length * barW2 + (cats.length - 1) * gap2;
    const startX2 = (w2 - totalW2) / 2;

    this.atletaShootBars = cats.map((c, i) => {
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
