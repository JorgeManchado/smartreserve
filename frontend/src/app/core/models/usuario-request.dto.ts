export interface UsuarioRequestDTO {
  nombre: string;
  email: string;
  password: string;
  telefono?: string;
  direccion?: string;
  idRol: number;
  idEstado: number;
}
