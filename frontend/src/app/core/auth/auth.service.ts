import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { throwError } from 'rxjs/internal/observable/throwError';
import { catchError } from 'rxjs/internal/operators/catchError';
import { tap } from 'rxjs/internal/operators/tap';
import { environment } from '../../../environments/environment';
import { AuthStore } from '../../core/auth/auth.store';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;

  constructor(private http: HttpClient, public auth: AuthStore) {}

  login(email: string, password: string): Observable<{ token: string }> {
    return this.http
      .post<{ token: string }>(`${this.apiUrl}/login`, { email, password })
      .pipe(
        tap((response) => {
          console.log('Login exitoso. Token recibido:', response.token);
        }),
        catchError((error) => {
          console.error('Error en login:', error);

          let mensaje = 'Servidor no disponible.';

          if (error.status === 403 || error.status === 401) {
            if (
              error.error &&
              typeof error.error === 'object' &&
              error.error.message
            ) {
              mensaje = error.error.message;
            }
          }

          return throwError(() => new Error(mensaje));
        })
      );
  }

  logout() {
    this.auth.logout();
  }
}
