import { Routes } from "@angular/router";
import { authGuard } from "./auth.service";
import { ShellComponent } from "./shell.component";
export const routes: Routes = [
  {
    path: "login",
    loadComponent: () =>
      import("./login.component").then((m) => m.LoginComponent),
  },
  {
    path: "",
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      {
        path: "painel",
        loadComponent: () =>
          import("./dashboard.component").then((m) => m.DashboardComponent),
      },
      {
        path: "jogadoras",
        loadComponent: () =>
          import("./players.component").then((m) => m.PlayersComponent),
      },
      {
        path: "jogadoras/:id",
        loadComponent: () =>
          import("./player-detail.component").then(
            (m) => m.PlayerDetailComponent,
          ),
      },
      {
        path: "videos",
        loadComponent: () =>
          import("./videos.component").then((m) => m.VideosComponent),
      },
      {
        path: "videos/clipes/:id",
        loadComponent: () =>
          import("./clip.component").then((m) => m.ClipComponent),
      },
      {
        path: "videos/playlists/:id",
        loadComponent: () =>
          import("./playlist.component").then((m) => m.PlaylistComponent),
      },
      {
        path: "pre-jogo",
        loadComponent: () =>
          import("./pre-jogo.component").then((m) => m.PreGameComponent),
      },
      {
        path: "pos-jogo",
        loadComponent: () =>
          import("./pos-jogo.component").then((m) => m.PostGameComponent),
      },
      {
        path: "treinos",
        loadComponent: () =>
          import("./trainings.component").then((m) => m.TrainingsComponent),
      },
      { path: "", pathMatch: "full", redirectTo: "painel" },
    ],
  },
  { path: "**", redirectTo: "painel" },
];
