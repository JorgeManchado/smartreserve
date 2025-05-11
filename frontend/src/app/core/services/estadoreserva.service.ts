import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EstadoReserva } from '../models/estadoreserva.model';

@Injectable({ providedIn: 'root' })
export class EstadoReservaService {
  private apiUrl = `${environment.apiUrl}/api/estadosreservas`;

  constructor(private http: HttpClient) {}

  getEstadosReserva(): Observable<EstadoReserva[]> {
    return this.http.get<EstadoReserva[]>(this.apiUrl);
  }

  getEstadoReservaById(id: number): Observable<EstadoReserva> {
    return this.http.get<EstadoReserva>(`${this.apiUrl}/${id}`);
  }
}
