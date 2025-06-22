export interface KPI {
  totalReservas: number;
  tasaOcupacion: number;
  usuariosActivos: number;
  horasReservadas: number;
}

export interface UltimaReserva {
  usuario: string;
  sala: string;
  fecha: string;
  duracion: string;
  estado: string;
  color: string;
  bgcolor: string;
}

export interface TipoSalaDistribucion {
  tipo: string;
  valor: number;
}

export interface DashboardResponse {
  kpi: KPI;
  ultimasReservas: UltimaReserva[];
  reservasPeriodo: number[];
  tipoSalaDistribucion: TipoSalaDistribucion[];
}
