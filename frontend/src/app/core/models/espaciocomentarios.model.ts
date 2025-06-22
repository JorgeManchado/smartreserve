import { Comentario } from './comentario.model';

export interface EspacioComentarios {
  idEspacio: number;
  nombre: string;
  imagen: string;
  valoracionPromedio: number;
  cantidadResenas: number;
  comentarios: Comentario[];
}
