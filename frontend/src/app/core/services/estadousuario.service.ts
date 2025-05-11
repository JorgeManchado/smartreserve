// src/app/core/services/rol.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EstadoUsuario } from '../models/estadousuario.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EstadoUsuarioService {
  private apiUrl = `${environment.apiUrl}/api/estadosusuario`;

  constructor(private http: HttpClient) {}

  getEstadosUsuario(): Observable<EstadoUsuario[]> {
    return this.http.get<EstadoUsuario[]>(this.apiUrl);
  }

  getEstadoUsuarioById(id: number): Observable<EstadoUsuario> {
    return this.http.get<EstadoUsuario>(`${this.apiUrl}/${id}`);
  }
}
