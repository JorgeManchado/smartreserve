import { CommonModule, NgClass } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AuthStore } from '../../../../core/auth/auth.store';
import { Notificacion } from '../../../../core/models/notificacion.model';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { TimeAgoPipe } from '../../../../shared/pipes/time-ago.pipe';

@Component({
  selector: 'app-perfil-notificaciones',
  standalone: true,
  imports: [CommonModule, NgClass, TimeAgoPipe],
  templateUrl: './perfil-notificaciones.component.html',
  styleUrls: ['./perfil-notificaciones.component.scss'],
})
export class PerfilNotificacionesComponent implements OnInit {
  notificaciones: Notificacion[] = [];
  paginaActual = 1;
  notificacionesPorPagina = 6;

  constructor(
    private notificacionService: NotificacionService,
    private authStore: AuthStore
  ) {}

  ngOnInit(): void {
    const idUsuario = this.authStore.getIdUsuarioValue();
    if (idUsuario) {
      this.notificacionService
        .getNotificacionesUsuario(idUsuario)
        .subscribe((data) => (this.notificaciones = data));
    }
  }

  get notificacionesPaginadas(): Notificacion[] {
    const inicio = (this.paginaActual - 1) * this.notificacionesPorPagina;
    const fin = inicio + this.notificacionesPorPagina;
    return this.notificaciones.slice(inicio, fin);
  }

  totalPaginas(): number {
    return Math.ceil(this.notificaciones.length / this.notificacionesPorPagina);
  }

  paginaSiguiente(): void {
    if (this.paginaActual < this.totalPaginas()) {
      this.paginaActual++;
    }
  }

  paginaAnterior(): void {
    if (this.paginaActual > 1) {
      this.paginaActual--;
    }
  }
  get total(): number {
    return this.notificaciones.length;
  }

  get inicio(): number {
    return (this.paginaActual - 1) * this.notificacionesPorPagina;
  }

  get fin(): number {
    return this.inicio + this.notificacionesPaginadas.length;
  }
}
