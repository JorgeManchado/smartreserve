// src/app/core/services/rol.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TipoEspacio } from '../models/tipoespacio.model';

@Injectable({ providedIn: 'root' })
export class TipoEspacioService {
  private apiUrl = `${environment.apiUrl}/api/tiposespacios`;

  constructor(private http: HttpClient) {}

  getTiposEspacio(): Observable<TipoEspacio[]> {
    return this.http.get<TipoEspacio[]>(this.apiUrl);
  }

  getTipoEspacioById(id: number): Observable<TipoEspacio> {
    return this.http.get<TipoEspacio>(`${this.apiUrl}/${id}`);
  }
}
