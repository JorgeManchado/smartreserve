import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { environment } from '../../../../environments/environment';
import { Equipamiento } from '../../../core/models/equipamiento.model';
import { Espacio } from '../../../core/models/espacio.model';
import { EspacioService } from '../../../core/services/espacio.service';
import { EstadoEspacioService } from '../../../core/services/estadoespacio.service';
import { TipoEspacioService } from '../../../core/services/tipoespacio.service';
declare var bootstrap: any;

@Component({
  selector: 'app-espacios',
  templateUrl: './espacios.page.component.html',
  styleUrls: ['./espacios.page.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
})
export class EspaciosPageComponent implements OnInit {
  espacios: Espacio[] = [];
  espaciosFiltrados: Espacio[] = [];
  tiposEspacio: any[] = [];
  estadosEspacio: any[] = [];
  espacioEliminable: Espacio | null = null;

  filtroNombre: string = '';
  filtroForm: FormGroup;
  mostrarFiltros = false;
  paginaActual = 0;
  tamanioPagina = 6;

  filtrosSeleccionados = {
    tipo: null as number | null,
    estado: null as number | null,
    capacidadMin: null as number | null,
    ubicacion: '',
  };

  constructor(
    private fb: FormBuilder,
    private espacioService: EspacioService,
    private tipoEspacioService: TipoEspacioService,
    private estadoEspacioService: EstadoEspacioService,
    private router: Router
  ) {
    this.filtroForm = this.fb.group({
      tipo: [''],
      estado: [''],
    });
  }

  ngOnInit(): void {
    this.espacioService.getEspacios().subscribe((data) => {
      this.espacios = data;
      this.aplicarFiltro();
    });

    this.tipoEspacioService.getTiposEspacio().subscribe((tipos) => {
      this.tiposEspacio = tipos;
    });

    this.estadoEspacioService.getEstadospacio().subscribe((estados) => {
      this.estadosEspacio = estados;
    });
  }

  buscar(): void {
    this.aplicarFiltro();
  }

  limpiarFiltros(): void {
    this.filtroForm.reset({ nombre: '' });
    this.filtrosSeleccionados = {
      tipo: null,
      estado: null,
      capacidadMin: null,
      ubicacion: '',
    };
    this.aplicarFiltro();
  }

  aplicarFiltro(): void {
    const nombre = this.filtroForm.value.nombre?.toLowerCase() || '';
    const { tipo, estado, capacidadMin, ubicacion } = this.filtrosSeleccionados;

    this.espaciosFiltrados = this.espacios.filter((e) => {
      const coincideNombre = e.nombre.toLowerCase().includes(nombre);
      const coincideTipo = !tipo || e.tipo.idTipoEspacio === tipo;
      const coincideEstado = !estado || e.estado.idEstado === estado;
      const coincideCapacidad = !capacidadMin || e.capacidad >= capacidadMin;
      const coincideUbicacion =
        !ubicacion ||
        e.ubicacion.toLowerCase().includes(ubicacion.toLowerCase());

      return (
        coincideNombre &&
        coincideTipo &&
        coincideEstado &&
        coincideCapacidad &&
        coincideUbicacion
      );
    });

    this.paginaActual = 0;
  }

  get espaciosPaginados(): Espacio[] {
    const start = this.paginaActual * this.tamanioPagina;
    return this.espaciosFiltrados.slice(start, start + this.tamanioPagina);
  }

  get rangoInicio(): number {
    return this.paginaActual * this.tamanioPagina;
  }

  get rangoFin(): number {
    return this.rangoInicio + this.tamanioPagina;
  }

  get totalEspacios(): number {
    return this.espaciosFiltrados.length;
  }

  paginaAnterior(): void {
    if (this.paginaActual > 0) {
      this.paginaActual--;
    }
  }

  paginaSiguiente(): void {
    if (this.rangoFin < this.totalEspacios) {
      this.paginaActual++;
    }
  }

  getImagenUrl(nombreImagen: string): string {
    return `${environment.apiUrl}/uploads/${nombreImagen}`;
  }

  seleccionarTipo(tipoId: number) {
    this.filtrosSeleccionados.tipo =
      this.filtrosSeleccionados.tipo === tipoId ? null : tipoId;
  }

  seleccionarEstado(estadoId: number) {
    this.filtrosSeleccionados.estado =
      this.filtrosSeleccionados.estado === estadoId ? null : estadoId;
  }

  abrirModalEliminar(espacio: Espacio): void {
    this.espacioEliminable = espacio;

    const modalElement = document.getElementById('confirmDeleteEspacioModal');
    if (modalElement) {
      const modal = new bootstrap.Modal(modalElement);
      modal.show();
    }
  }

  eliminarEspacioConfirmado(): void {
    if (!this.espacioEliminable) return;

    this.espacioService
      .deleteEspacio(this.espacioEliminable.idEspacio)
      .subscribe({
        next: () => {
          // Elimina localmente
          this.espacios = this.espacios.filter(
            (e) => e.idEspacio !== this.espacioEliminable?.idEspacio
          );
          this.aplicarFiltro();

          this.cerrarModalYLimpiar();
        },
        error: (err) => {
          console.error('Error al eliminar espacio:', err);
          alert('No se pudo eliminar el espacio.');
        },
      });
  }

  cancelarEliminacion(): void {
    this.cerrarModalYLimpiar();
  }

  private cerrarModalYLimpiar(): void {
    const modalElement = document.getElementById('confirmDeleteEspacioModal');
    if (modalElement) {
      const modalInstance = bootstrap.Modal.getInstance(modalElement);
      modalInstance?.hide();
    }

    const backdrop = document.querySelector('.modal-backdrop');
    if (backdrop) backdrop.remove();

    document.body.classList.remove('modal-open');
    document.body.style.removeProperty('padding-right');

    this.espacioEliminable = null;
  }

  getDescripcionEquipamientos(equipamientos: Equipamiento[]): string {
    if (!equipamientos || equipamientos.length === 0) return '';
    return equipamientos.map((e) => e.descripcion).join(', ');
  }
}
