export interface Comentario {
  idComentario?: number;
  texto: string;
  valoracion: number;
  fecha?: string;

  estadoDescripcion?: string;
  idEstado?: number;

  nombreUsuario?: string;
  idUsuario?: number;

  idReserva: number;
}
