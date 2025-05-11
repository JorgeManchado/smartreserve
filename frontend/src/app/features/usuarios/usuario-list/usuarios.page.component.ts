import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Usuario } from '../../../core/models/usuario.model';
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

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.usuarioService.getUsuarios().subscribe((data) => {
      this.usuarios = data;
      this.aplicarFiltro();
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

  eliminarUsuario(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar este usuario?')) {
      this.usuarioService.deleteUsuario(id).subscribe({
        next: () => {
          this.usuarios = this.usuarios.filter((u) => u.idUsuario !== id);
          this.aplicarFiltro();
        },
        error: (err) => {
          console.error('Error al eliminar usuario:', err);
          alert('No se pudo eliminar el usuario.');
        },
      });
    }
  }

  abrirModalConfirmacion(usuario: Usuario) {
    this.usuarioSeleccionado = usuario;
  }

  confirmarEliminacion() {
    if (!this.usuarioSeleccionado) return;

    this.usuarioService
      .deleteUsuario(this.usuarioSeleccionado.idUsuario)
      .subscribe({
        next: () => {
          this.usuarios = this.usuarios.filter(
            (u) => u.idUsuario !== this.usuarioSeleccionado?.idUsuario
          );
          this.aplicarFiltro();

          const modalElement = document.getElementById('confirmDeleteModal');
          if (modalElement) {
            const modalInstance = bootstrap.Modal.getInstance(modalElement);
            modalInstance?.hide();
          }
        },
        error: (err) => {
          console.error('Error al eliminar usuario:', err);
          alert('No se pudo eliminar el usuario.');
        },
      });
  }
}
