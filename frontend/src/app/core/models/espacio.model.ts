import { Equipamiento } from './equipamiento.model';
export interface Espacio {
  idEspacio: number;
  nombre: string;
  capacidad: number;
  ubicacion: string;
  descripcion: string;
  imagen: string;

  tipo: {
    idTipoEspacio: number;
    descripcion: string;
  };

  estado: {
    idEstado: number;
    descripcion: string;
  };
  equipamientos?: Equipamiento[];
}
