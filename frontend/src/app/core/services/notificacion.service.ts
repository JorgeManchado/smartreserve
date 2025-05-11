import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Notificacion } from '../models/notificacion.model';

@Injectable({
  providedIn: 'root',
})
export class NotificacionService {
  private apiUrl = `${environment.apiUrl}/api/notificaciones`;

  constructor(private http: HttpClient) {}

  getNotificacionesUsuario(idUsuario: number): Observable<Notificacion[]> {
    return this.http.get<Notificacion[]>(`${this.apiUrl}/usuario/${idUsuario}`);
  }

  marcarComoLeida(idNotificacion: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${idNotificacion}/leida`, {});
  }
}
