import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../environments/environment';
import { EspacioComentarios } from '../../core/models/espaciocomentarios.model';
import { EspacioComentariosService } from '../../core/services/espaciocomentarios.service';

declare var bootstrap: any;

@Component({
  selector: 'app-espacios-comentarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './espacioscomentarios.component.html',
  styleUrls: ['./espacioscomentarios.component.scss'],
})
export class EspaciosComentariosComponent implements OnInit {
  espaciosComentarios: EspacioComentarios[] = [];
  espaciosFiltrados: EspacioComentarios[] = [];

  filtroNombre: string = '';
  paginaActual = 0;
  tamanioPagina = 8;

  espacioSeleccionado:
    | (EspacioComentarios & { comentariosFormateados?: any[] })
    | null = null;
  modalDetalles: any;

  Math = Math;

  cargando: boolean = true;

  constructor(private espacioComentariosService: EspacioComentariosService) {}

  ngOnInit(): void {
    this.espacioComentariosService.getEspacios().subscribe((data) => {
      this.espaciosComentarios = data;
      this.aplicarFiltro();
      this.cargando = false;

      const modalEl = document.getElementById('detallesModal');
      if (modalEl) {
        this.modalDetalles = new bootstrap.Modal(modalEl);
      }
    });
  }

  get espaciosPaginados(): EspacioComentarios[] {
    const inicio = this.paginaActual * this.tamanioPagina;
    return this.espaciosFiltrados.slice(inicio, inicio + this.tamanioPagina);
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

  aplicarFiltro(): void {
    const nombre = this.filtroNombre.toLowerCase();
    this.espaciosFiltrados = this.espaciosComentarios.filter((e) =>
      e.nombre.toLowerCase().includes(nombre)
    );
    this.paginaActual = 0;
  }

  limpiarFiltro(): void {
    this.filtroNombre = '';
    this.aplicarFiltro();
  }

  verDetalles(espacio: EspacioComentarios): void {
    this.espacioSeleccionado = {
      ...espacio,
      comentariosFormateados:
        espacio.comentarios?.map((comentario) => ({
          ...comentario,
          fechaRelativa: comentario.fecha
            ? this.formatearFechaRelativa(comentario.fecha)
            : 'Fecha no disponible',
        })) ?? [],
    };

    if (this.modalDetalles) {
      this.modalDetalles.show();
    }
  }

  getImagenUrl(nombreImagen: string): string {
    return `${environment.apiUrl}/uploads/${nombreImagen}`;
  }

  private formatearFechaRelativa(fechaIso: string): string {
    const fecha = new Date(fechaIso);
    const ahora = new Date();
    const diffMs = ahora.getTime() - fecha.getTime();
    const dias = Math.floor(diffMs / (1000 * 60 * 60 * 24));

    if (dias === 0) return 'Hoy';
    if (dias === 1) return 'Hace 1 día';
    return `Hace ${dias} días`;
  }

  calcularDiasPasados(fecha: string | undefined): string {
    if (!fecha) return '';

    const fechaComentario = new Date(fecha);
    const hoy = new Date();

    const inicioHoy = new Date(
      hoy.getFullYear(),
      hoy.getMonth(),
      hoy.getDate()
    );
    const inicioComentario = new Date(
      fechaComentario.getFullYear(),
      fechaComentario.getMonth(),
      fechaComentario.getDate()
    );

    const diffMs = inicioHoy.getTime() - inicioComentario.getTime();
    const dias = Math.floor(diffMs / (1000 * 60 * 60 * 24));

    if (dias === 0) return 'Hoy';
    if (dias === 1) return 'Hace 1 día';
    return `Hace ${dias} días`;
  }

  formatearTextoResena(cantidad: number): string {
    return cantidad === 1 ? '1 reseña' : `${cantidad} reseñas`;
  }
}
