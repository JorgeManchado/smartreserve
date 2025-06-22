import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EspacioComentarios } from '../models/espaciocomentarios.model';

@Injectable({ providedIn: 'root' })
export class EspacioComentariosService {
  private apiUrl = `${environment.apiUrl}/api/espacios/comentarios`;

  constructor(private http: HttpClient) {}

  getEspacios(): Observable<EspacioComentarios[]> {
    return this.http.get<EspacioComentarios[]>(this.apiUrl);
  }
}
