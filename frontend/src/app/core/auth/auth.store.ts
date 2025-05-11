import { Injectable } from '@angular/core';

const TOKEN_KEY = 'token';
const ROLE_KEY = 'rol';
const NAME_KEY = 'name';
const ID_KEY = 'id_usuario';

@Injectable({ providedIn: 'root' })
export class AuthStore {
  constructor() {}

  isAuthenticatedValue(): boolean {
    return !!localStorage.getItem(TOKEN_KEY);
  }

  getRoleValue(): string | null {
    return localStorage.getItem(ROLE_KEY);
  }

  getNameValue(): string | null {
    return localStorage.getItem(NAME_KEY);
  }

  getIdUsuarioValue(): number | null {
    const value = localStorage.getItem(ID_KEY);
    return value ? +value : null;
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  login(token: string): void {
    try {
      const base64 = token.split('.')[1];
      const payload = JSON.parse(decodeURIComponent(escape(atob(base64))));
      const rol = payload.rol || 'Empleado';
      const name = payload.sub;
      const idUsuario = payload.idUsuario;

      console.log('name', name);

      localStorage.setItem(TOKEN_KEY, token);
      localStorage.setItem(ROLE_KEY, rol);
      localStorage.setItem(NAME_KEY, name);
      localStorage.setItem(ID_KEY, idUsuario);
    } catch (err) {
      console.error('Token inválido', err);
      this.logout();
    }
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(ROLE_KEY);
    localStorage.removeItem(NAME_KEY);
    localStorage.removeItem(ID_KEY);
  }
}
