import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { environment } from '../../../../environments/environment';
import { AuthStore } from '../../../core/auth/auth.store';
import { Comentario } from '../../../core/models/comentario.model';
import { MisReserva } from '../../../core/models/misreserva.model';
import { ComentarioService } from '../../../core/services/comentario.service';
import { EstadoReservaService } from '../../../core/services/estadoreserva.service';
import { GoogleAuthService } from '../../../core/services/googleauth.service';
import { MisReservaService } from '../../../core/services/misreserva.service';
import { TipoEspacioService } from '../../../core/services/tipoespacio.service';
import { ConfirmModalComponent } from '../../../shared/components/confirm-modal/confirm-modal.component';

declare var bootstrap: any;

@Component({
  selector: 'app-reservas',
  templateUrl: './misreservas.page.component.html',
  styleUrls: ['./misreservas.page.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    ConfirmModalComponent,
  ],
})
export class MisReservasPageComponent implements OnInit {
  reservas: MisReserva[] = [];
  reservasFiltradas: MisReserva[] = [];
  comentarios: Comentario[] = [];
  tiposEspacio: { idTipoEspacio: number; descripcion: string }[] = [];
  estadosReserva: { idEstado: number; descripcion: string }[] = [];

  paginaActual = 0;
  tamanioPagina = 6;

  // Comentario modal
  valoracionSeleccionada = 0;
  comentarioTexto = '';
  reservaSeleccionada: MisReserva | null = null;
  modalComentario: any;
  comentarioExistente: Comentario | null = null;

  // Filtros
  filtroNombre: string = '';
  filtroEstado: string = '';
  filtroUbicacion: string = '';
  filtroIdReserva: number | null = null;
  filtroTipo: string = '';
  filtroCapacidad: number | null = null;
  filtroFechaDesde: string = '';
  filtroFechaHasta: string = '';
  mostrarFiltros = false;
  filtroNombreUsuario: string = '';

  reservaPendiente: any = null;

  modalConfig = {
    modalId: 'confirmModal',
    type: 'cancel' as 'cancel' | 'confirm',
    title: '',
    message: '',
    confirmText: '',
    cancelText: '',
  };

  modalInstance: any;

  intentoEnviarComentario = false;

  soloLecturaComentario = false;

  cargando: boolean = true;

  constructor(
    private reservaService: MisReservaService,
    private comentarioService: ComentarioService,
    private authStore: AuthStore,
    private router: Router,
    private tipoEspacioService: TipoEspacioService,
    private estadoReservaService: EstadoReservaService,
    private toastr: ToastrService,
    private authService: GoogleAuthService
  ) {}

  ngOnInit(): void {
    const usuarioId = this.authStore.getIdUsuarioValue();

    this.reservaService.getReservas().subscribe((data) => {
      this.reservas = data.filter((r) => r.usuario.idUsuario === usuarioId);
      this.aplicarFiltro();
      this.cargando = false;
    });

    this.comentarioService.listar().subscribe((data) => {
      this.comentarios = data;
    });

    this.tipoEspacioService.getTiposEspacio().subscribe((tipos) => {
      this.tiposEspacio = tipos;
    });

    this.estadoReservaService.getEstadosReserva().subscribe((estados) => {
      this.estadosReserva = estados;
    });

    const modalEl = document.getElementById('comentarioModal');
    if (modalEl) {
      this.modalComentario = new bootstrap.Modal(modalEl);

      modalEl.addEventListener('hidden.bs.modal', () => {
        this.reservaSeleccionada = null;
        this.comentarioTexto = '';
        this.valoracionSeleccionada = 0;
        this.intentoEnviarComentario = false;
        this.comentarioExistente = null;
        this.soloLecturaComentario = false;
      });
    }
  }

  get reservasPaginadas(): MisReserva[] {
    const start = this.paginaActual * this.tamanioPagina;
    return this.reservasFiltradas.slice(start, start + this.tamanioPagina);
  }

  get rangoInicio(): number {
    return this.paginaActual * this.tamanioPagina;
  }

  get rangoFin(): number {
    return this.rangoInicio + this.tamanioPagina;
  }

  get totalReservas(): number {
    return this.reservasFiltradas.length;
  }

  paginaAnterior(): void {
    if (this.paginaActual > 0) this.paginaActual--;
  }

  paginaSiguiente(): void {
    if (this.rangoFin < this.totalReservas) this.paginaActual++;
  }

  getImagenUrl(nombreImagen: string): string {
    return `${environment.apiUrl}/uploads/${nombreImagen}`;
  }

  eliminarReserva(reserva: MisReserva): void {
    this.reservaService.deleteReserva(reserva.idReserva!).subscribe(() => {
      this.reservas = this.reservas.filter(
        (r) => r.idReserva !== reserva.idReserva
      );
      this.aplicarFiltro();
    });
  }

  tieneComentarios(reservaId: number): boolean {
    return this.comentarios.some((c) => c.idReserva === reservaId);
  }

  abrirComentarioModal(reserva: MisReserva): void {
    this.intentoEnviarComentario = false;
    this.reservaSeleccionada = reserva;

    const existente = this.comentarios.find(
      (c) => c.idReserva === reserva.idReserva!
    );
    if (existente) {
      this.comentarioExistente = existente;
      this.comentarioTexto = existente.texto;
      this.valoracionSeleccionada = existente.valoracion;
      this.soloLecturaComentario = true;
    } else {
      this.comentarioExistente = null;
      this.comentarioTexto = '';
      this.valoracionSeleccionada = 0;
      this.soloLecturaComentario = false;
    }

    this.modalComentario.show();
  }

  seleccionarValoracion(valor: number): void {
    this.valoracionSeleccionada = valor;
  }

  guardarComentario(): void {
    this.intentoEnviarComentario = true;

    if (
      !this.reservaSeleccionada ||
      !this.comentarioTexto.trim() ||
      this.valoracionSeleccionada < 1
    )
      return;

    const comentario: Comentario = {
      idReserva: this.reservaSeleccionada.idReserva!,
      texto: this.comentarioTexto.trim(),
      valoracion: this.valoracionSeleccionada,
    };

    if (this.comentarioExistente?.idComentario) {
      this.comentarioService
        .actualizarComentario(this.comentarioExistente.idComentario, comentario)
        .subscribe(() => {
          const i = this.comentarios.findIndex(
            (c) => c.idComentario === this.comentarioExistente?.idComentario
          );
          if (i !== -1)
            this.comentarios[i] = {
              ...this.comentarioExistente,
              ...comentario,
            };
          this.modalComentario.hide();
        });
    } else {
      this.comentarioService.crearComentario(comentario).subscribe((nuevo) => {
        this.toastr.success(
          `              
            <div class="toast-content">
              <div class="toast-title">Comentario Creado</div>
              <div class="toast-message">
                El comentario se ha creado correctamente.
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

        this.cargarDatosIniciales();

        this.comentarios.push(nuevo);
        this.modalComentario.hide();
      });
    }
  }

  obtenerCantidadComentarios(idReserva: number): number {
    return this.comentarios.filter((c) => c.idReserva === idReserva).length;
  }

  aplicarFiltro(): void {
    const nombre = this.filtroNombre.toLowerCase();
    const nombreUsuario = this.filtroNombreUsuario?.toLowerCase() || '';
    const estado = this.filtroEstado.toLowerCase();
    const ubicacion = this.filtroUbicacion.toLowerCase();
    const tipo = this.filtroTipo.toLowerCase();
    const desde = this.filtroFechaDesde
      ? new Date(this.filtroFechaDesde)
      : null;
    const hasta = this.filtroFechaHasta
      ? new Date(this.filtroFechaHasta)
      : null;

    this.reservasFiltradas = this.reservas.filter((r) => {
      const coincideNombre = r.espacio.nombre.toLowerCase().includes(nombre);
      const coincideNombreUsuario = r.usuario.nombre
        .toLowerCase()
        .includes(nombreUsuario);
      const coincideEstado =
        !estado || r.estado.descripcion.toLowerCase().includes(estado);
      const coincideUbicacion =
        !ubicacion || r.espacio.ubicacion.toLowerCase().includes(ubicacion);
      const coincideIdReserva =
        !this.filtroIdReserva || r.idReserva === this.filtroIdReserva;
      const coincideTipo =
        !tipo || r.espacio.tipo.descripcion.toLowerCase().includes(tipo);
      const coincideCapacidad =
        !this.filtroCapacidad || r.espacio.capacidad >= this.filtroCapacidad;

      const fechaReserva = new Date(r.fechaInicio);
      const coincideFechaDesde = !desde || fechaReserva >= desde;
      const coincideFechaHasta = !hasta || fechaReserva <= hasta;

      return (
        coincideNombre &&
        coincideNombreUsuario &&
        coincideEstado &&
        coincideUbicacion &&
        coincideIdReserva &&
        coincideTipo &&
        coincideCapacidad &&
        coincideFechaDesde &&
        coincideFechaHasta
      );
    });

    this.paginaActual = 0;
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroEstado = '';
    this.filtroUbicacion = '';
    this.filtroIdReserva = null;
    this.filtroTipo = '';
    this.filtroCapacidad = null;
    this.filtroFechaDesde = '';
    this.filtroFechaHasta = '';
    this.aplicarFiltro();
  }

  abrirModalCancelar(reserva: any): void {
    this.reservaPendiente = reserva;

    const modal = new bootstrap.Modal(
      document.getElementById('confirmCancelReservaModal')!
    );
    modal.show();
  }

  cancelarReservaConfirmada(): void {
    if (!this.reservaPendiente?.idReserva) return;

    const idReserva = this.reservaPendiente.idReserva;

    // Si está sincronizada, comprobar que el usuario esté autenticado en Google
    if (this.reservaPendiente.sincronizado) {
      this.authService.checkTokenStatus().subscribe((tokenPresente) => {
        if (tokenPresente) {
          this.realizarCancelacion(idReserva);
        } else {
          const popup = window.open(
            `${environment.apiUrl}/api/google/auth`,
            '_blank'
          );

          const espera = setInterval(() => {
            if (popup?.closed) {
              clearInterval(espera);
              this.realizarCancelacion(idReserva);
            }
          }, 1000);
        }
      });
    } else {
      this.realizarCancelacion(idReserva);
    }
  }

  private realizarCancelacion(idReserva: number): void {
    this.reservaService.cancelReserva(idReserva).subscribe({
      next: () => {
        this.reservaPendiente = null;
        this.modalInstance?.hide();

        this.toastr.success(
          `
          <div class="toast-content">
            <div class="toast-title">Reserva cancelada</div>
            <div class="toast-message">Tu reserva ha sido Cancelada correctamente.</div>
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

        this.cargarDatosIniciales();
      },
      error: (err) => {
        console.error('Error al cancelar la reserva', err);
        alert('No se pudo cancelar la reserva.');
      },
    });
  }

  cargarDatosIniciales(): void {
    const usuarioId = this.authStore.getIdUsuarioValue();

    this.reservaService.getReservas().subscribe((data) => {
      this.reservas = data.filter((r) => r.usuario.idUsuario === usuarioId);

      this.aplicarFiltro();
    });

    this.comentarioService.listar().subscribe((data) => {
      this.comentarios = data;
    });

    this.tipoEspacioService.getTiposEspacio().subscribe((tipos) => {
      this.tiposEspacio = tipos;
    });

    this.estadoReservaService.getEstadosReserva().subscribe((estados) => {
      this.estadosReserva = estados;
      console.log('estados de reservas ', estados);
    });
  }

  esHoy(fecha: string): boolean {
    const hoy = new Date();
    const fechaReserva = new Date(fecha);

    return (
      hoy.getFullYear() === fechaReserva.getFullYear() &&
      hoy.getMonth() === fechaReserva.getMonth() &&
      hoy.getDate() === fechaReserva.getDate()
    );
  }

  confirmarReserva(reserva: MisReserva): void {
    if (!this.esHoy(reserva.fechaInicio)) {
      this.toastr.info(
        `              
          <div class="toast-content">
            <div class="toast-title">No es posible confirmar</div>
            <div class="toast-message">
              Solo puedes confirmar una reserva el <strong>mismo día</strong>.
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

    this.reservaPendiente = reserva;

    this.modalConfig = {
      modalId: 'confirmModal',
      type: 'confirm',
      title: 'Confirmar reserva',
      message: '¿Estás seguro de que deseas confirmar la reserva?',
      confirmText: 'Sí, confirmar',
      cancelText: 'Cancelar',
    };

    this.modalInstance = new bootstrap.Modal(
      document.getElementById(this.modalConfig.modalId)!
    );
    this.modalInstance.show();
  }

  confirmarReservaConfirmada(): void {
    if (!this.reservaPendiente?.idReserva) return;

    this.reservaService
      .confirmarReserva(this.reservaPendiente.idReserva)
      .subscribe({
        next: () => {
          this.reservaPendiente = null;

          this.toastr.success(
            `              
              <div class="toast-content">
                <div class="toast-title">Reserva confirmada</div>
                <div class="toast-message">
                  Tu reserva ha sido confirmada correctamente.
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

          this.cargarDatosIniciales();

          if (this.modalInstance) {
            this.modalInstance.hide();
          }
        },
        error: (err) => {
          console.error('Error al confirmar la reserva', err);

          this.toastr.error(
            `              
              <div class="toast-content">
                <div class="toast-title">Error al confirmar la reserva</div>
                <div class="toast-message">
                  No se pudo confirmar la reserva.
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

  openConfirmModal(tipo: 'cancel' | 'confirm', reserva: any) {
    this.reservaPendiente = reserva;

    this.modalConfig = {
      modalId: 'confirmModal',
      type: tipo,
      title: tipo === 'cancel' ? 'Cancelar reserva' : 'Confirmar reserva',
      message:
        tipo === 'cancel'
          ? '¿Estás seguro de que deseas cancelar la reserva'
          : '¿Estás seguro de que deseas confirmar la reserva',
      confirmText: tipo === 'cancel' ? 'Sí, cancelar' : 'Sí, confirmar',
      cancelText: tipo === 'cancel' ? 'No, volver' : 'Cancelar',
    };

    this.modalInstance = new bootstrap.Modal(
      document.getElementById(this.modalConfig.modalId)!
    );
    this.modalInstance.show();
  }
}
