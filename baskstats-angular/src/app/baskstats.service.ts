import { Injectable, inject, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject, catchError, forkJoin, map, of } from "rxjs";
import {
  Dashboard,
  Player,
  Team,
  Clip,
  Playlist,
  Training,
  MatchData,
  PlanoTreinoResponse,
} from "./baskstats.model";
import { dashboard, players } from "./demo";
import { GithubVideosService } from "./github-videos.service";

@Injectable({ providedIn: "root" })
export class BaskstatsService {
  private http = inject(HttpClient);
  private githubVideos = inject(GithubVideosService);
  demo = signal(sessionStorage.getItem("bask-demo") === "true");
  teamId = new BehaviorSubject<number>(
    Number(localStorage.getItem("bask-team")) || 1,
  );

  selectTeam(id: number) {
    localStorage.setItem("bask-team", String(id));
    this.teamId.next(id);
  }

  teams() {
    return this.demo()
      ? of<Team[]>([
          { id: 1, nome: "NBF Feminino" },
          { id: 2, nome: "Equipe adversária" },
        ])
      : this.http.get<Team[]>("/api/times");
  }

  dashboard(id: number) {
    return this.demo()
      ? of(dashboard)
      : this.http.get<Dashboard>(`/api/times/${id}/dashboard`);
  }

  players(id: number) {
    return this.demo()
      ? of(players)
      : this.http.get<Player[]>(`/api/times/${id}/jogadoras`);
  }

  player(id: number) {
    return this.demo()
      ? of(players.find((p) => p.id === id) ?? null)
      : this.http.get<Player>(`/api/jogadoras/${id}/estatisticas`);
  }

  match(id: number, type: string) {
    return this.demo()
      ? of<MatchData>({
          partida: {
            confronto:
              type === "pre-jogo"
                ? "NBF × Equipe adversária"
                : "NBF 78 × 64 Equipe adversária",
          },
          dashboard,
        })
      : this.http.get<MatchData>(`/api/times/${id}/${type}`);
  }

  clips() {
    const base$ = this.demo()
      ? of<Clip[]>(
          [1, 2, 3, 4].map((i) => ({
            id: i,
            nome: `clipe_00${i}_Q1_2262s-2304s`,
            categoria: "Melhores momentos",
            url: null,
            inicio: 0,
            fim: 42,
          })),
        )
      : this.http.get<Clip[]>("/api/clipes");

    return forkJoin({
      locais: base$.pipe(catchError(() => of<Clip[]>([]))),
      remotos: this.githubVideos.listar().pipe(catchError(() => of([]))),
    }).pipe(
      map(({ locais, remotos }) => {
        const maiorId = locais.reduce(
          (max, item) => Math.max(max, Number(item.id) || 0),
          0,
        );
        const urlsExistentes = new Set(
          locais.map((item) => item.url).filter(Boolean),
        );

        const clipsGithub: Clip[] = remotos
          .filter((v) => !urlsExistentes.has(v.url))
          .map((v, index) => ({
            id: maiorId + index + 1,
            nome: v.nome,
            categoria: v.categoria,
            url: v.url,
            inicio: v.inicio,
            fim: v.fim,
          }));

        return [...locais, ...clipsGithub];
      }),
    );
  }

  playlists() {
    return this.demo()
      ? of<Playlist[]>(
          [1, 2, 3].map((id) => ({ id, nome: "Playlist " + id, clipes: [] })),
        )
      : this.http.get<Playlist[]>("/api/playlists");
  }

  savePlaylist(value: { nome: string; clipeIds: number[] }, id?: number) {
    return id
      ? this.http.put<Playlist>("/api/playlists/" + id, value)
      : this.http.post<Playlist>("/api/playlists", value);
  }

  deletePlaylist(id: number) {
    return this.http.delete("/api/playlists/" + id);
  }

  trainings() {
    return this.demo()
      ? of<Training[]>([])
      : this.http.get<Training[]>("/api/treinos");
  }

  saveTraining(value: Omit<Training, "id">, id?: number) {
    return id
      ? this.http.put<Training>("/api/treinos/" + id, value)
      : this.http.post<Training>("/api/treinos", value);
  }

  deleteTraining(id: number) {
    return this.http.delete("/api/treinos/" + id);
  }

  getPlanoTreino(
    timeId: number,
    params: {
      foco?: string;
      meta?: number | null;
      ultimosJogos?: number;
      posicao?: string;
    },
  ) {
    const query = new URLSearchParams();
    if (params.foco) query.set("foco", params.foco);
    if (
      params.meta !== null &&
      params.meta !== undefined &&
      !Number.isNaN(params.meta)
    ) {
      query.set("meta", String(params.meta));
    }
    if (params.ultimosJogos)
      query.set("ultimosJogos", String(params.ultimosJogos));
    if (params.posicao && params.posicao !== "TODAS")
      query.set("posicao", params.posicao);

    const qs = query.toString();
    return this.http.get<PlanoTreinoResponse>(
      `/api/times/${timeId}/treinos/plano${qs ? `?${qs}` : ""}`,
    );
  }
}

export { PlanoTreinoResponse };