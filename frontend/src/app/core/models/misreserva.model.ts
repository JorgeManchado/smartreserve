import { Espacio } from './espacio.model';
import { EstadoReserva } from './estadoreserva.model';
import { Usuario } from './usuario.model';

export interface MisReserva {
  idReserva?: number;
  usuario: Usuario;
  espacio: Espacio;
  estado: EstadoReserva;
  fechaInicio: string;
  fechaFin: string;
  sincronizado: boolean;
  ocupantes: number;
  recomendadaia: boolean;
  eventid?: String;
}
