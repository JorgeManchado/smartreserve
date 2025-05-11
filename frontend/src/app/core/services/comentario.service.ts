import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Comentario } from '../models/comentario.model';

@Injectable({ providedIn: 'root' })
export class ComentarioService {
  private apiUrl = `${environment.apiUrl}/api/comentarios`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Comentario[]> {
    return this.http.get<Comentario[]>(this.apiUrl);
  }

  aprobar(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/aprobar/${id}`, {});
  }

  anular(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/anular/${id}`, {});
  }
}
