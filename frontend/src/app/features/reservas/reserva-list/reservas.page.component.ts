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
import { Reserva } from '../../../core/models/reserva.model';
import { EspacioService } from '../../../core/services/espacio.service';
import { EstadoReservaService } from '../../../core/services/estadoreserva.service'; // opcional
import { ReservaService } from '../../../core/services/reserva.service';
import { UsuarioService } from '../../../core/services/usuario.service';

declare var bootstrap: any;

@Component({
  selector: 'app-reservas',
  templateUrl: './reservas.page.component.html',
  styleUrls: ['./reservas.page.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
})
export class ReservasPageComponent implements OnInit {
  modalInstance: any;
  reservas: Reserva[] = [];
  reservasFiltradas: Reserva[] = [];
  estadosReserva: any[] = [];
  espacios: any[] = [];
  usuarios: any[] = [];

  filtroNombre = '';
  filtroForm: FormGroup;
  mostrarFiltros = false;
  paginaActual = 0;
  tamanioPagina = 6;

  filtrosSeleccionados = {
    estado: null as number | null,
    sala: null as number | null,
    fechaInicio: '',
    fechaFin: '',
  };

  reservaEliminable: Reserva | null = null;

  constructor(
    private fb: FormBuilder,
    private reservaService: ReservaService,
    private estadoReservaService: EstadoReservaService,
    private espacioService: EspacioService,
    private usuarioService: UsuarioService,
    private router: Router
  ) {
    this.filtroForm = this.fb.group({
      estado: [''],
      sala: [''],
      fechaInicio: [''],
      fechaFin: [''],
    });
  }

  ngOnInit(): void {
    this.reservaService.getReservas().subscribe((data) => {
      this.reservas = data;
      this.aplicarFiltro();
    });

    this.estadoReservaService.getEstadosReserva().subscribe((estados) => {
      this.estadosReserva = estados;
    });

    this.espacioService.getEspacios().subscribe((espacios) => {
      this.espacios = espacios;
    });

    this.usuarioService.getUsuarios().subscribe((usuarios) => {
      this.usuarios = usuarios;
    });
  }

  aplicarFiltro(): void {
    const nombre = this.filtroNombre?.toLowerCase() || '';
    const { estado, sala, fechaInicio, fechaFin } = this.filtrosSeleccionados;

    this.reservasFiltradas = this.reservas.filter((reserva) => {
      const coincideNombre = reserva.nombreUsuario
        ?.toLowerCase()
        .includes(nombre);
      const coincideEstado = !estado || reserva.idEstado === estado;
      const coincideSala = !sala || reserva.idEspacio === sala;

      const fechaReserva = new Date(reserva.fechaInicio);
      const desde = fechaInicio ? new Date(fechaInicio) : null;
      const hasta = fechaFin ? new Date(fechaFin) : null;
      const coincideFechas =
        (!desde || fechaReserva >= desde) && (!hasta || fechaReserva <= hasta);

      return coincideNombre && coincideEstado && coincideSala && coincideFechas;
    });

    this.paginaActual = 0;
  }

  limpiarFiltros(): void {
    this.filtroForm.reset();
    this.filtroNombre = '';
    this.filtrosSeleccionados = {
      estado: null,
      sala: null,
      fechaInicio: '',
      fechaFin: '',
    };
    this.aplicarFiltro();
  }

  paginaAnterior(): void {
    if (this.paginaActual > 0) {
      this.paginaActual--;
    }
  }

  paginaSiguiente(): void {
    if (this.rangoFin < this.totalReservas) {
      this.paginaActual++;
    }
  }

  get reservasPaginadas(): Reserva[] {
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

  abrirModalEliminar(reserva: Reserva): void {
    this.reservaEliminable = reserva;
    const modalElement = document.getElementById('confirmDeleteReservaModal');
    if (modalElement) {
      this.modalInstance = new bootstrap.Modal(modalElement);
      this.modalInstance.show();
    }
  }

  eliminarReservaConfirmada(): void {
    if (!this.reservaEliminable) return;

    this.reservaService
      .deleteReserva(this.reservaEliminable.idReserva)
      .subscribe(() => {
        this.reservas = this.reservas.filter(
          (r) => r.idReserva !== this.reservaEliminable?.idReserva
        );
        this.aplicarFiltro();
        this.reservaEliminable = null;
        this.cerrarModalYLimpiar();
      });
  }

  cancelarEliminacion(): void {
    this.reservaEliminable = null;
    this.cerrarModalYLimpiar();
  }

  cerrarModalYLimpiar(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
      this.modalInstance = null;
    }

    const backdrop = document.querySelector('.modal-backdrop');
    if (backdrop) backdrop.remove();

    document.body.classList.remove('modal-open');
    document.body.style.removeProperty('padding-right');
  }

  getDuracionEnHoras(reserva: Reserva): string {
    const inicio = new Date(reserva.fechaInicio);
    const fin = new Date(reserva.fechaFin);
    const horas = (fin.getTime() - inicio.getTime()) / (1000 * 60 * 60);
    return horas.toFixed(1);
  }

  getImagenUrl(nombreImagen: string): string {
    return `${environment.apiUrl}/uploads/${nombreImagen}`;
  }
}
