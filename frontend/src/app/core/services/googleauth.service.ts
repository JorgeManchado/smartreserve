import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GoogleAuthService {
  constructor(private http: HttpClient) {}

  checkTokenStatus(): Observable<boolean> {
    return this.http.get<boolean>(
      `${environment.apiUrl}/api/google/token-status`
    );
  }
}
