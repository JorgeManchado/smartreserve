import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RecomendacionResponse } from '../../core/models/recomendacion.model';

@Injectable({
  providedIn: 'root',
})
export class RecomendacionService {
  private apiUrl = `${environment.apiUrl}/api/recomendador`;

  constructor(private http: HttpClient) {}

  getRecomendaciones(
    usuarioId: number,
    diaSemana: number,
    hora: number
  ): Observable<RecomendacionResponse> {
    return this.http.post<RecomendacionResponse>(this.apiUrl, {
      usuarioId,
      diaSemana,
      hora,
    });
  }
}
