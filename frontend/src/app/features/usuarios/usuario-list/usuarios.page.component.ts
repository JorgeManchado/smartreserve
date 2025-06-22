import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Usuario } from '../../../core/models/usuario.model';
import { MisReservaService } from '../../../core/services/misreserva.service';
import { UsuarioService } from '../../../core/services/usuario.service';

declare var bootstrap: any;
@Component({
  selector: 'app-usuarios',
  templateUrl: './usuarios.page.component.html',
  styleUrls: ['./usuarios.page.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
})
export class UsuariosPageComponent implements OnInit {
  usuarios: Usuario[] = [];
  usuariosFiltrados: Usuario[] = [];
  usuariosPaginados: Usuario[] = [];
  usuarioSeleccionado: Usuario | null = null;

  tieneReservasUsuario: boolean = false;
  mostrarModalInactivar: boolean = false;

  filtro = '';
  paginaActual = 0;
  tamanioPagina = 10;
  mostrarFiltros = false;

  filtrosAvanzados = {
    correo: '',
    roles: [] as string[],
    estados: [] as string[],
    fechaDesde: '',
    fechaHasta: '',
  };

  cargando: boolean = true;

  constructor(
    private usuarioService: UsuarioService,
    private misReservaService: MisReservaService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.usuarioService.getUsuarios().subscribe((data) => {
      this.usuarios = data;
      this.aplicarFiltro();
      this.cargando = false;
    });
  }

  toggleFiltros() {
    this.mostrarFiltros = !this.mostrarFiltros;
  }

  toggleRol(rol: string) {
    const index = this.filtrosAvanzados.roles.indexOf(rol);
    if (index === -1) {
      this.filtrosAvanzados.roles.push(rol);
    } else {
      this.filtrosAvanzados.roles.splice(index, 1);
    }
  }

  toggleEstado(estado: string) {
    const index = this.filtrosAvanzados.estados.indexOf(estado);
    if (index === -1) {
      this.filtrosAvanzados.estados.push(estado);
    } else {
      this.filtrosAvanzados.estados.splice(index, 1);
    }
  }

  aplicarFiltro() {
    const term = this.filtro.toLowerCase();

    this.usuariosFiltrados = this.usuarios.filter((u) => {
      const coincideNombre = u.nombre.toLowerCase().includes(term);
      const coincideCorreo = u.email
        .toLowerCase()
        .includes(this.filtrosAvanzados.correo.toLowerCase());
      const coincideRol =
        this.filtrosAvanzados.roles.length === 0 ||
        this.filtrosAvanzados.roles.includes(u.rol);
      const coincideEstado =
        this.filtrosAvanzados.estados.length === 0 ||
        this.filtrosAvanzados.estados.includes(u.estado);

      const desde = this.filtrosAvanzados.fechaDesde
        ? new Date(this.filtrosAvanzados.fechaDesde)
        : null;
      const hasta = this.filtrosAvanzados.fechaHasta
        ? new Date(this.filtrosAvanzados.fechaHasta)
        : null;
      const acceso = u.ultimoAcceso ? new Date(u.ultimoAcceso) : null;

      const coincideFecha =
        (!desde || (acceso && acceso >= desde)) &&
        (!hasta || (acceso && acceso <= hasta));

      return (
        coincideNombre &&
        coincideCorreo &&
        coincideRol &&
        coincideEstado &&
        coincideFecha
      );
    });

    this.paginaActual = 0;
    this.actualizarPaginado();
  }

  limpiarFiltros() {
    this.filtrosAvanzados = {
      correo: '',
      roles: [],
      estados: [],
      fechaDesde: '',
      fechaHasta: '',
    };
    this.aplicarFiltro();
  }

  actualizarPaginado() {
    const start = this.paginaActual * this.tamanioPagina;
    const end = start + this.tamanioPagina;
    this.usuariosPaginados = this.usuariosFiltrados.slice(start, end);
  }

  paginaSiguiente() {
    this.paginaActual++;
    this.actualizarPaginado();
  }

  paginaAnterior() {
    if (this.paginaActual > 0) {
      this.paginaActual--;
      this.actualizarPaginado();
    }
  }

  get rangoActual(): string {
    if (!this.usuariosFiltrados?.length) return '0 a 0';
    const inicio = this.paginaActual * this.tamanioPagina + 1;
    const fin = Math.min(
      (this.paginaActual + 1) * this.tamanioPagina,
      this.usuariosFiltrados.length
    );
    return `${inicio} a ${fin}`;
  }

  eliminarUsuario(usuario: Usuario): void {
    this.misReservaService
      .getReservasPorUsuario(usuario.idUsuario)
      .subscribe((reservas) => {
        if (reservas.length > 0) {
          const confirmarInactivar = confirm(
            `El usuario "${usuario.nombre}" tiene reservas registradas.\n¿Desea inactivarlo?`
          );

          if (confirmarInactivar) {
            this.usuarioService
              .inactivarUsuario(usuario.idUsuario)
              .subscribe(() => {
                usuario.estado = 'Inactivo';
                this.aplicarFiltro();
              });
          }
        } else {
          const confirmarEliminar = confirm(
            `¿Está seguro de que desea eliminar al usuario "${usuario.nombre}"?`
          );

          if (confirmarEliminar) {
            this.usuarioService
              .deleteUsuario(usuario.idUsuario)
              .subscribe(() => {
                this.usuarios = this.usuarios.filter(
                  (u) => u.idUsuario !== usuario.idUsuario
                );
                this.aplicarFiltro();
              });
          }
        }
      });
  }

  abrirModalConfirmacion(usuario: Usuario) {
    if (usuario.estado === 'Inactivo') {
      this.toastr.info(
        `              
          <div class="toast-content">
            <div class="toast-title">Eliminar Usuario</div>
            <div class="toast-message">
              No se puede eliminar al usuario <strong>"${usuario.nombre}"</strong> porque ya está inactivo.
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

    this.usuarioSeleccionado = usuario;
    this.misReservaService
      .getReservasPorUsuario(usuario.idUsuario)
      .subscribe((reservas) => {
        this.tieneReservasUsuario = reservas.length > 0;

        const modalElement = document.getElementById('confirmDeleteModal');
        if (modalElement) {
          const modal = new bootstrap.Modal(modalElement);
          modal.show();
        }
      });
  }

  confirmarEliminacion(): void {
    if (!this.usuarioSeleccionado) return;

    const usuario = this.usuarioSeleccionado;

    this.misReservaService
      .getReservasPorUsuario(usuario.idUsuario)
      .subscribe((reservas) => {
        const tieneReservas = reservas.length > 0;
        const modalEl = document.getElementById('confirmDeleteModal');
        const modalInstance = bootstrap.Modal.getInstance(modalEl!);

        if (tieneReservas) {
          const confirmarInactivar = confirm(
            `El usuario "${usuario.nombre}" tiene reservas registradas.\n¿Desea inactivarlo?`
          );

          if (confirmarInactivar) {
            this.usuarioService
              .inactivarUsuario(usuario.idUsuario)
              .subscribe(() => {
                usuario.estado = 'Inactivo';
                this.aplicarFiltro();
                modalInstance?.hide();
              });
          }
        } else {
          const confirmarEliminar = confirm(
            `¿Desea eliminar al usuario "${usuario.nombre}"?`
          );

          if (confirmarEliminar) {
            this.usuarioService
              .deleteUsuario(usuario.idUsuario)
              .subscribe(() => {
                this.usuarios = this.usuarios.filter(
                  (u) => u.idUsuario !== usuario.idUsuario
                );
                this.aplicarFiltro();
                modalInstance?.hide();
              });
          }
        }
      });
  }

  procesarConfirmacionUsuario(): void {
    if (!this.usuarioSeleccionado) return;

    const modalElement = document.getElementById('confirmDeleteModal');
    const modalInstance = bootstrap.Modal.getInstance(modalElement!);

    if (this.tieneReservasUsuario) {
      this.usuarioService
        .inactivarUsuario(this.usuarioSeleccionado.idUsuario)
        .subscribe(() => {
          this.usuarioSeleccionado!.estado = 'Inactivo';
          this.aplicarFiltro();
          modalInstance?.hide();
        });
    } else {
      this.usuarioService
        .deleteUsuario(this.usuarioSeleccionado.idUsuario)
        .subscribe(() => {
          this.usuarios = this.usuarios.filter(
            (u) => u.idUsuario !== this.usuarioSeleccionado?.idUsuario
          );
          this.aplicarFiltro();
          modalInstance?.hide();
        });
    }
  }
}
