import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { AuthStore } from '../../core/auth/auth.store';
import { Notificacion } from '../../core/models/notificacion.model';
import { NotificacionService } from '../../core/services/notificacion.service';
import { WebSocketService } from '../../core/services/websocket.service';

@Component({
  selector: 'app-notificaciones-bell',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notificaciones-bell.component.html',
  styleUrls: ['./notificaciones-bell.component.scss'],
})
export class NotificacionesBellComponent implements OnInit {
  @Input() idUsuario!: number;

  notificaciones: Notificacion[] = [];
  mostrarLista = false;

  constructor(
    private notificacionService: NotificacionService,
    private webSocketService: WebSocketService,
    private auth: AuthStore
  ) {}

  ngOnInit(): void {
    this.cargarNotificaciones();

    const token = localStorage.getItem('token');

    if (token && this.idUsuario) {
      this.webSocketService.conectar(this.idUsuario, token);
      this.webSocketService.notificaciones$.subscribe((nueva) => {
        const yaExiste = this.notificaciones.some(
          (n) => n.idNotificacion === nueva.idNotificacion
        );

        if (!yaExiste) {
          this.notificaciones.unshift(nueva);
        }
      });
    }
  }

  cargarNotificaciones() {
    if (!this.idUsuario) return;

    this.notificacionService
      .getNotificacionesUsuario(this.idUsuario)
      .subscribe((data) => {
        this.notificaciones = data.filter((n) => !n.leida);
      });
  }

  marcarComoLeida(notificacion: Notificacion) {
    this.notificacionService
      .marcarComoLeida(notificacion.idNotificacion)
      .subscribe(() => {
        this.notificaciones = this.notificaciones.filter(
          (n) => n.idNotificacion !== notificacion.idNotificacion
        );
      });
  }

  marcarTodasComoLeidas() {
    const notisCopia = [...this.notificaciones];
    notisCopia.forEach((n) => this.marcarComoLeida(n));
  }

  toggleLista() {
    this.mostrarLista = !this.mostrarLista;
  }
}
