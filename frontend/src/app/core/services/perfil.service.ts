import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PerfilInfoDTO } from '../models/perfil-info.dto';
import { Usuario } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class PerfilService {
  private perfilUrl = `${environment.apiUrl}/api/perfil`;

  constructor(private http: HttpClient) {}

  obtenerPerfil(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.perfilUrl}/${id}`);
  }

  actualizarPerfil(id: number, datos: PerfilInfoDTO): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.perfilUrl}/${id}`, datos);
  }

  cambiarPassword(
    id: number,
    data: {
      currentPassword: string;
      newPassword: string;
    }
  ): Observable<void> {
    return this.http.post<void>(
      `${this.perfilUrl}/cambiar-password/${id}`,
      data
    );
  }
}
