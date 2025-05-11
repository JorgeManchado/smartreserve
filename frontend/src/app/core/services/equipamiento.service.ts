// src/app/core/services/rol.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Equipamiento } from '../models/equipamiento.model';

@Injectable({ providedIn: 'root' })
export class EquipamientoService {
  private apiUrl = `${environment.apiUrl}/api/equipamientos`;

  constructor(private http: HttpClient) {}

  getEquipamientos(): Observable<Equipamiento[]> {
    return this.http.get<Equipamiento[]>(this.apiUrl);
  }

  getEquipamientoById(id: number): Observable<Equipamiento> {
    return this.http.get<Equipamiento>(`${this.apiUrl}/${id}`);
  }
}
