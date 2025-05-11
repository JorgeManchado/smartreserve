export interface Usuario {
  idUsuario: number;
  nombre: string;
  email: string;
  telefono?: string | null;
  direccion?: string | null;
  ultimoAcceso?: string | null;
  idRol: number;
  rol: string;
  idEstado: number;
  estado: string;
}
