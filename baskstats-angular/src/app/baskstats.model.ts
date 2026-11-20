export interface Player {
  id: number;
  nome: string;
  pos: string;
  numero: number | null;
  ppj: number;
  reb: number;
  ast: number;
  jogos: number;
  minutos: number | null;
  roubos: number;
  tocos: number | null;
  turnovers: number;
  eficiencia: number;
  arremesso2: number;
  arremesso3: number;
  lanceLivre: number;
}
export interface Dashboard {
  rodadas: string[];
  pontosPorRodada: number[];
  vitorias: number;
  derrotas: number;
  jogadoras: Player[];
  arremesso2: number;
  arremesso3: number;
  lanceLivre: number;
  pontosPorPosicao: { pos: string; media: number }[];
}
export interface Team {
  id: number;
  nome: string;
}
export interface Clip {
  id: number;
  nome: string;
  categoria: string;
  url: string | null;
  inicio: number;
  fim: number;
}
export interface Playlist {
  id: number;
  nome: string;
  clipes: Clip[];
}
export interface Training {
  id: number;
  titulo: string;
  descricao: string;
  jogadoraId: number | null;
  data: string | null;
}
export interface MatchData {
  partida: {
    confronto: string;
    data_hora?: string;
    placar_final?: number;
    adversario_placar?: number;
  };
  dashboard: Dashboard;
  quartos?: {
    numero: number;
    placar: number;
    adversarioPlacar?: number | null;
  }[];
}

export interface PlanoTreinoAtleta {
  id: number;
  nome: string;
  posicao: string;
  numero: number | null;
  jogos: number;
  minutos: number;
  ppj: number;
  reb: number;
  ast: number;
  roubos: number;
  turnovers: number;
  eficiencia: number;
  arremesso2: number;
  arremesso3: number;
  lanceLivre: number;
  metricaFoco: string;
  meta: number;
  valorAtual: number;
  valorRecente: number;
  deltaRecente: number;
  gapParaMeta: number;
  statusMeta: 'ATINGIDA' | 'EM_FOCO';
  recomendacaoTreino: string;
}

export interface PlanoTreinoResponse {
  timeId: number;
  foco: string;
  meta: number;
  ultimosJogos: number;
  totalAtletas: number;
  atletasEmFoco: number;
  atletas: PlanoTreinoAtleta[];
}
