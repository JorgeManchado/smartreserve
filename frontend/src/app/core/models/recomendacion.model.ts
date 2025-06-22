export interface Recomendacion {
  espacio_id: number;
  nombre_espacio: string;
  probabilidad: number;
}

export interface RecomendacionResponse {
  usuario_id: number;
  nombre_usuario: string;
  dia_semana: number;
  hora: number;
  recomendaciones: Recomendacion[];
}
