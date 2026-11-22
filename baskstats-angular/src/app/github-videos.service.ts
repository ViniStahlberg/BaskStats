import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { forkJoin, map, Observable, shareReplay } from "rxjs";

export type CategoriaGithub =
  | "Ataque"
  | "Defesa"
  | "Finalizacoes"
  | "Rebote_Ofensivo";

export interface VideoGithubItem {
  nome: string;
  categoria: CategoriaGithub;
  url: string;
  inicio: number;
  fim: number;
}

interface GithubContentEntry {
  name: string;
  path: string;
  type: "file" | "dir";
  download_url: string | null;
}

@Injectable({ providedIn: "root" })
export class GithubVideosService {
  private http = inject(HttpClient);

  private readonly owner = "Brunoo00";
  private readonly repo = "BaskStatsClipes";
  private readonly branch = "main";
  private readonly baseFolder = "1620";

  readonly categorias: CategoriaGithub[] = [
    "Ataque",
    "Defesa",
    "Finalizacoes",
    "Rebote_Ofensivo",
  ];

  private cache$?: Observable<VideoGithubItem[]>;

  listar(): Observable<VideoGithubItem[]> {
    if (!this.cache$) {
      const requisicoes = this.categorias.map((categoria) => {
        const pastaEncoded = encodeURIComponent(categoria);
        const url = `https://api.github.com/repos/${this.owner}/${this.repo}/contents/${this.baseFolder}/${pastaEncoded}?ref=${this.branch}`;

        return this.http.get<GithubContentEntry[]>(url).pipe(
          map((entries) =>
            entries
              .filter(
                (entry) =>
                  entry.type === "file" &&
                  /\.(mp4|webm|mov|ogg|m4v)$/i.test(entry.name),
              )
              .map((entry) => ({
                nome: entry.name
                  .replace(/\.[^/.]+$/, "")
                  .replace(/[_-]+/g, " ")
                  .trim(),
                categoria,
                url:
                  entry.download_url ??
                  `https://raw.githubusercontent.com/${this.owner}/${this.repo}/${this.branch}/${entry.path}`,
                inicio: 0,
                fim: 0,
              })),
          ),
        );
      });

      this.cache$ = forkJoin(requisicoes).pipe(
        map((grupos) => grupos.flat()),
        shareReplay(1),
      );
    }

    return this.cache$;
  }
}