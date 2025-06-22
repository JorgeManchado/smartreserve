import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { environment } from '../../../../environments/environment';
import { Equipamiento } from '../../../core/models/equipamiento.model';
import { Espacio } from '../../../core/models/espacio.model';
import { MisReserva } from '../../../core/models/misreserva.model';
import { EspacioService } from '../../../core/services/espacio.service';
import { EstadoEspacioService } from '../../../core/services/estadoespacio.service';
import { MisReservaService } from '../../../core/services/misreserva.service';
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
  reservas: MisReserva[] = [];
  espacios: Espacio[] = [];
  espaciosFiltrados: Espacio[] = [];
  tiposEspacio: any[] = [];
  estadosEspacio: any[] = [];

  espacioEliminable: Espacio | null = null;
  estadoCambioPendiente: { espacio: Espacio; nuevoEstado: any } | null = null;
  espacioCambioEstadoPendiente: Espacio | null = null;
  reservasActivasPendientes: MisReserva[] = [];

  filtroForm: FormGroup;
  paginaActual = 0;
  tamanioPagina = 6;

  cargando: boolean = true;

  constructor(
    private fb: FormBuilder,
    private espacioService: EspacioService,
    private tipoEspacioService: TipoEspacioService,
    private estadoEspacioService: EstadoEspacioService,
    private router: Router,
    private toastr: ToastrService,
    private reservaService: MisReservaService
  ) {
    this.filtroForm = this.fb.group({
      nombre: [''],
      tipo: [null],
      estado: [null],
      capacidadMin: [null],
      ubicacion: [''],
    });
  }

  ngOnInit(): void {
    this.espacioService.getEspacios().subscribe((data) => {
      this.espacios = data;
      this.aplicarFiltro();
      this.cargando = false;
    });

    this.tipoEspacioService.getTiposEspacio().subscribe((tipos) => {
      this.tiposEspacio = tipos;
    });

    this.estadoEspacioService.getEstadospacio().subscribe((estados) => {
      this.estadosEspacio = estados;
    });

    this.filtroForm.valueChanges.subscribe(() => {
      this.aplicarFiltro();
    });
  }

  limpiarFiltros(): void {
    this.filtroForm.reset({
      nombre: '',
      tipo: null,
      estado: null,
      capacidadMin: null,
      ubicacion: '',
    });
    this.aplicarFiltro();
  }

  aplicarFiltro(): void {
    const { nombre, tipo, estado, capacidadMin, ubicacion } =
      this.filtroForm.value;

    this.espaciosFiltrados = this.espacios.filter((e) => {
      const coincideNombre = e.nombre
        .toLowerCase()
        .includes((nombre || '').toLowerCase());
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
    if (this.paginaActual > 0) this.paginaActual--;
  }

  paginaSiguiente(): void {
    if (this.rangoFin < this.totalEspacios) this.paginaActual++;
  }

  getImagenUrl(nombreImagen: string): string {
    return `${environment.apiUrl}/uploads/${nombreImagen}`;
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

          this.toastr.error(
            `              
              <div class="toast-content">
                <div class="toast-title">Error al eliminar el espacio</div>
                <div class="toast-message">
                No se pudo eliminar el espacio.
                </div>
              </div>
            `,
            '',
            {
              enableHtml: true,
              toastClass: 'ngx-toastr custom-toast toast-info',
              closeButton: true,
              timeOut: 3000,
            }
          );
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

  abrirModalCambioEstado(espacio: Espacio): void {
    const nuevoEstado =
      espacio.estado.descripcion === 'Disponible'
        ? this.estadosEspacio.find((e) => e.descripcion === 'Mantenimiento')
        : this.estadosEspacio.find((e) => e.descripcion === 'Disponible');

    if (nuevoEstado) {
      this.estadoCambioPendiente = { espacio, nuevoEstado };

      const modal = new bootstrap.Modal(
        document.getElementById('confirmEstadoEspacioModal')!
      );
      modal.show();
    }
  }

  cambiarEstadoConfirmado(): void {
    if (!this.estadoCambioPendiente) return;

    const { espacio, nuevoEstado } = this.estadoCambioPendiente;

    this.espacioService
      .updateEspacioEstado(espacio.idEspacio, nuevoEstado.idEstado)
      .subscribe(() => {
        espacio.estado = nuevoEstado;
        this.estadoCambioPendiente = null;

        const modalEl = document.getElementById('confirmEstadoEspacioModal');
        if (modalEl) {
          const modal = bootstrap.Modal.getInstance(modalEl);
          modal?.hide();
        }
      });
  }

  editarEspacio(espacio: any): void {
    if (espacio.estado.descripcion !== 'Mantenimiento') {
      this.toastr.info(
        `              
          <div class="toast-content">
            <div class="toast-title">Editar Espacio</div>
            <div class="toast-message">
               El espacio <strong>"${espacio.nombre}"</strong> solo puede editarse si su estado es <strong>Mantenimiento</strong>.
            </div>
          </div>
        `,
        '',
        {
          enableHtml: true,
          toastClass: 'ngx-toastr custom-toast toast-info',
          closeButton: true,
          timeOut: 3000,
        }
      );

      return;
    }

    this.router.navigate(['/espacios/editar', espacio.idEspacio]);
  }

  verificarCambioAMantenimiento(espacio: Espacio): void {
    if (espacio.estado.descripcion !== 'Disponible') {
      this.cambiarEstadoEspacio(espacio, 'Disponible');
      return;
    }

    this.reservaService.getReservas().subscribe((data) => {
      const reservasDelEspacio = data.filter(
        (r) => r.espacio.idEspacio === espacio.idEspacio
      );

      const activas = reservasDelEspacio.filter((res) =>
        ['Pendiente', 'Confirmada'].includes(res.estado.descripcion)
      );

      if (activas.length > 0) {
        this.espacioCambioEstadoPendiente = espacio;
        this.reservasActivasPendientes = activas;

        const modalEl = document.getElementById('confirmCambioEstadoModal');
        if (modalEl) {
          const modal = new bootstrap.Modal(modalEl);
          modal.show();
        }
      } else {
        this.cambiarEstadoEspacio(espacio, 'Mantenimiento');
      }
    });
  }

  cambiarEstadoEspacio(espacio: any, nuevoEstadoDescripcion: string): void {
    // Busca el objeto de estado completo por su descripción (ej. "Mantenimiento", "Disponible")
    const nuevoEstado = this.estadosEspacio.find(
      (estado) => estado.descripcion === nuevoEstadoDescripcion
    );

    if (!nuevoEstado) {
      console.error('Estado no encontrado:', nuevoEstadoDescripcion);
      return;
    }

    this.espacioService
      .updateEspacioEstado(espacio.idEspacio, nuevoEstado.idEstado)
      .subscribe({
        next: () => {
          espacio.estado = nuevoEstado;

          this.toastr.success(
            `              
              <div class="toast-content">
                <div class="toast-title">Estado actualizado</div>
                <div class="toast-message">
                 El estado del espacio se actualizó correctamente.
                </div>
              </div>
            `,
            '',
            {
              enableHtml: true,
              toastClass: 'ngx-toastr custom-toast toast-info',
              closeButton: true,
              timeOut: 4000,
            }
          );
        },
        error: (err) => {
          console.error('Error actualizando estado:', err);
          this.toastr.error(
            `              
              <div class="toast-content">
                <div class="toast-title">Actualizar estado</div>
                <div class="toast-message">
                  Error al actualizar el estado.
                </div>
              </div>
            `,
            '',
            {
              enableHtml: true,
              toastClass: 'ngx-toastr custom-toast toast-info',
              closeButton: true,
              timeOut: 4000,
            }
          );
        },
      });
  }

  intentarEliminarEspacio(espacio: any): void {
    if (espacio.estado.descripcion !== 'Mantenimiento') {
      this.toastr.info(
        `              
          <div class="toast-content">
            <div class="toast-title">Eliminar Espacio</div>
            <div class="toast-message">             
             El espacio <strong>"${espacio.nombre}"</strong> solo puede eliminarse si su estado es <strong>Mantenimiento</strong>.
            </div>
          </div>
        `,
        '',
        {
          enableHtml: true,
          toastClass: 'ngx-toastr custom-toast toast-info',
          closeButton: true,
          timeOut: 4000,
        }
      );

      return;
    }

    // Si está en mantenimiento, proceder a verificar eliminación
    this.verificarEliminacionEspacio(espacio);
  }

  verificarEliminacionEspacio(espacio: any): void {
    this.reservaService.getReservas().subscribe((reservas: any[]) => {
      const reservasDelEspacio = reservas.filter(
        (r) => r.espacio.idEspacio === espacio.idEspacio
      );

      if (reservasDelEspacio.length > 0) {
        this.toastr.info(
          `
            <div class="toast-content">
              <div class="toast-title">Eliminar Espacio</div>
              <div class="toast-message">
                 No se puede eliminar el espacio <strong>"${espacio.nombre}"</strong> porque tiene reservas registradas.
              </div>
            </div>
            `,
          '',
          {
            enableHtml: true,
            toastClass: 'ngx-toastr custom-toast toast-info',
            closeButton: true,
            timeOut: 4000,
          }
        );
      } else {
        // Asignamos el espacio y abrimos el modal
        this.espacioEliminable = espacio;

        const modalElement = document.getElementById(
          'confirmDeleteEspacioModal'
        );
        if (modalElement) {
          const modal = new bootstrap.Modal(modalElement);
          modal.show();
        }
      }
    });
  }

  confirmarCancelacionReservasActivas(): void {
    if (!this.espacioCambioEstadoPendiente) return;

    const espacio = this.espacioCambioEstadoPendiente;

    // Cancela todas las reservas activas
    const observables = this.reservasActivasPendientes.map((reserva) =>
      this.reservaService.cancelReserva(reserva.idReserva!)
    );

    // Esperamos a que todas se cancelen
    Promise.all(observables.map((o) => o.toPromise()))
      .then(() => {
        this.cambiarEstadoEspacio(espacio, 'Mantenimiento');
      })
      .finally(() => {
        this.cerrarModalCambioEstado();
      });
  }

  cancelarCambioEstado(): void {
    this.cerrarModalCambioEstado();
  }

  private cerrarModalCambioEstado(): void {
    const modalEl = document.getElementById('confirmCambioEstadoModal');
    if (modalEl) {
      const modal = bootstrap.Modal.getInstance(modalEl);
      modal?.hide();
    }

    this.espacioCambioEstadoPendiente = null;
    this.reservasActivasPendientes = [];
  }
}
