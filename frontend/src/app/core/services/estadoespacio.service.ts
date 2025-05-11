// src/app/core/services/rol.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EstadoEspacio } from '../models/estadoespacio.model';

@Injectable({ providedIn: 'root' })
export class EstadoEspacioService {
  private apiUrl = `${environment.apiUrl}/api/estadosespacios`;

  constructor(private http: HttpClient) {}

  getEstadospacio(): Observable<EstadoEspacio[]> {
    return this.http.get<EstadoEspacio[]>(this.apiUrl);
  }

  getTipoEspacioById(id: number): Observable<EstadoEspacio> {
    return this.http.get<EstadoEspacio>(`${this.apiUrl}/${id}`);
  }
}
