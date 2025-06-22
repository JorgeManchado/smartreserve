import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Espacio } from '../models/espacio.model';

@Injectable({ providedIn: 'root' })
export class EspacioService {
  private apiUrl = `${environment.apiUrl}/api/espacios`;

  constructor(private http: HttpClient) {}

  getEspacios(): Observable<Espacio[]> {
    return this.http.get<Espacio[]>(this.apiUrl);
  }

  getEspacioById(id: number): Observable<Espacio> {
    return this.http.get<Espacio>(`${this.apiUrl}/${id}`);
  }

  createEspacio(formData: FormData): Observable<Espacio> {
    return this.http.post<Espacio>(this.apiUrl, formData);
  }

  updateEspacio(id: number, formData: FormData): Observable<Espacio> {
    return this.http.put<Espacio>(`${this.apiUrl}/${id}`, formData);
  }

  updateEspacioEstado(id: number, idEstado: number) {
    return this.http.post(`${this.apiUrl}/${id}/actualizar-estado`, {
      idEstado,
    });
  }

  deleteEspacio(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
