export interface Reserva {
  idReserva: number;
  idUsuario: number;
  nombreUsuario: string;
  telefonoUsuario: string;

  idEspacio: number;
  nombreEspacio: string;
  imagenEspacio: string;

  idEstado: number;
  descripcionEstado: string;

  fechaInicio: string;
  fechaFin: string;

  sincronizado: boolean;
}
