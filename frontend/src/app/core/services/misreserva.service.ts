import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MisReserva } from '../models/misreserva.model';

@Injectable({
  providedIn: 'root',
})
export class MisReservaService {
  private apiUrl = `${environment.apiUrl}/api/misreservas`;

  constructor(private http: HttpClient) {}

  getReservas(): Observable<MisReserva[]> {
    return this.http.get<MisReserva[]>(this.apiUrl);
  }

  getReservaById(id: number): Observable<MisReserva> {
    return this.http.get<MisReserva>(`${this.apiUrl}/${id}`);
  }

  createReserva(reserva: MisReserva): Observable<MisReserva> {
    return this.http.post<MisReserva>(this.apiUrl, reserva);
  }

  updateReserva(id: number, reserva: MisReserva): Observable<MisReserva> {
    return this.http.put<MisReserva>(`${this.apiUrl}/${id}`, reserva);
  }

  deleteReserva(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  cancelReserva(idReserva: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${idReserva}/cancelar`, {});
  }

  confirmarReserva(idReserva: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${idReserva}/confirmar`, {});
  }

  verificarDisponibilidad(body: {
    idEspacio: number;
    inicio: string;
    fin: string;
  }): Observable<boolean> {
    return this.http.post<boolean>(`${this.apiUrl}/disponible`, body);
  }

  getReservasPorUsuario(idUsuario: number): Observable<MisReserva[]> {
    return this.http.get<MisReserva[]>(`${this.apiUrl}/usuario/${idUsuario}`);
  }
}
