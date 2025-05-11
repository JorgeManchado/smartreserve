import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DashboardResponse } from '../models/dashboard.model';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private apiUrl = `${environment.apiUrl}/api/dashboard`;

  constructor(private http: HttpClient) {}

  obtenerDatosDashboard(filtros: any): Observable<DashboardResponse> {
    let params = new HttpParams();
    if (filtros.fechaInicio)
      params = params.set('fechaInicio', filtros.fechaInicio);
    if (filtros.fechaFin) params = params.set('fechaFin', filtros.fechaFin);
    if (filtros.tipoSala) params = params.set('tipoSala', filtros.tipoSala);
    if (filtros.estado) params = params.set('estado', filtros.estado);

    return this.http.get<DashboardResponse>(`${this.apiUrl}`, {
      params,
    });
  }
}
