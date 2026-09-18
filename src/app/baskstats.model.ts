export interface Jogadora {
  nome: string;
  pos: string;
  ppj: number;
  reb: number;
  ast: number;
}

export interface AtletaDetalhada {
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

export interface PosicaoStat {
  pos: string;
  media: number;
}

export interface DashData {
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

export interface PointDot { x: number; y: number; label: string; val: number; }
export interface PointLabel { x: number; label: string; }
export interface ShootingBar { x: number; y: number; w: number; h: number; color: string; cx: number; vy: number; val: number; label: string; }
export interface PositionBar { pos: string; labelX: number; textY: number; barX: number; y: number; w: number; h: number; valueX: number; media: number; }
export interface StreakItem { result: 'V' | 'D'; color: string; }
export interface StatBar { label: string; labelX: number; textY: number; barX: number; y: number; w: number; h: number; valueX: number; value: number; }