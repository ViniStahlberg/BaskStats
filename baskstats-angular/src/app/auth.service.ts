import { inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router, CanActivateFn } from "@angular/router";
import { catchError, map, of, switchMap } from "rxjs";
@Injectable({ providedIn: "root" })
export class AuthService {
  http = inject(HttpClient);
  csrf() {
    return this.http.get("/api/auth/csrf");
  }
  login(email: string, password: string, remember: boolean) {
    return this.csrf().pipe(
      switchMap(() =>
        this.http.post("/api/auth/login", { email, password, remember }),
      ),
    );
  }
  logout() {
    return this.csrf().pipe(
      switchMap(() => this.http.post("/api/auth/logout", {})),
    );
  }
  me() {
    return this.http.get("/api/auth/me");
  }
}
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService),
    router = inject(Router);
  return sessionStorage.getItem("bask-demo") === "true"
    ? true
    : auth.me().pipe(
        map(() => true),
        catchError(() => of(router.createUrlTree(["/login"]))),
      );
};
