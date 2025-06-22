import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Comentario } from '../../core/models/comentario.model';
import { ComentarioService } from '../../core/services/comentario.service';

@Component({
  selector: 'app-comentario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './comentario.component.html',
  styleUrls: ['./comentario.component.scss'],
})
export class ComentarioComponent implements OnInit {
  comentarios: Comentario[] = [];
  comentariosFiltrados: Comentario[] = [];

  filtroNombre: string = '';
  filtroEstado: string = '';
  filtroValoracionMin: number | null = null;
  filtroTexto: string = '';
  filtroFechaDesde: string = '';
  filtroFechaHasta: string = '';

  mostrarFiltros = false;

  paginaActual = 1;
  elementosPorPagina = 4;
  cargando: boolean = true;

  constructor(
    private comentarioService: ComentarioService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.comentarioService.listar().subscribe((data) => {
      this.comentarios = data;
      this.aplicarFiltro();
      this.cargando = false;
    });
  }

  aprobar(id: number): void {
    this.comentarioService.aprobar(id).subscribe(() => this.cargar());

    this.toastr.success(
      `
        <div class="toast-content">
          <div class="toast-title">Acción realizada</div>
          <div class="toast-message">
            Comentario aprobado correctamente.
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
  }

  anular(id: number): void {
    this.comentarioService.anular(id).subscribe(() => this.cargar());

    this.toastr.success(
      `
        <div class="toast-content">
          <div class="toast-title">Acción realizada</div>
          <div class="toast-message">
            Comentario anulado correctamente.
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
  }

  aplicarFiltro(): void {
    this.comentariosFiltrados = this.comentarios.filter((comentario) => {
      const coincideNombre = comentario.nombreUsuario
        ?.toLowerCase()
        .includes(this.filtroNombre.toLowerCase());

      const coincideEstado =
        !this.filtroEstado ||
        comentario.estadoDescripcion?.toLowerCase() ===
          this.filtroEstado.toLowerCase();

      const coincideValoracion =
        this.filtroValoracionMin === null ||
        comentario.valoracion >= this.filtroValoracionMin;

      const coincideTexto = comentario.texto
        .toLowerCase()
        .includes(this.filtroTexto.toLowerCase());

      const fechaComentario = comentario.fecha
        ? new Date(comentario.fecha)
        : null;
      const desde = this.filtroFechaDesde
        ? new Date(this.filtroFechaDesde)
        : null;
      const hasta = this.filtroFechaHasta
        ? new Date(this.filtroFechaHasta)
        : null;

      const coincideFecha =
        (!desde || (fechaComentario && fechaComentario >= desde)) &&
        (!hasta || (fechaComentario && fechaComentario <= hasta));

      return (
        coincideNombre &&
        coincideEstado &&
        coincideValoracion &&
        coincideTexto &&
        coincideFecha
      );
    });

    this.paginaActual = 1;
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroEstado = '';
    this.filtroValoracionMin = null;
    this.filtroTexto = '';
    this.filtroFechaDesde = '';
    this.filtroFechaHasta = '';
    this.aplicarFiltro();
  }

  cambiarPagina(valor: number): void {
    this.paginaActual += valor;
  }

  get comentariosPaginados(): Comentario[] {
    const inicio = (this.paginaActual - 1) * this.elementosPorPagina;
    return this.comentariosFiltrados.slice(
      inicio,
      inicio + this.elementosPorPagina
    );
  }

  get totalPaginas(): number {
    return Math.ceil(
      this.comentariosFiltrados.length / this.elementosPorPagina
    );
  }

  getHasta(): number {
    return Math.min(
      this.paginaActual * this.elementosPorPagina,
      this.comentariosFiltrados.length
    );
  }
}
