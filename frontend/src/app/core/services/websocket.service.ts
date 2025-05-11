import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { Subject } from 'rxjs';
import SockJS from 'sockjs-client';
import { environment } from '../../../environments/environment';
import { Notificacion } from '../models/notificacion.model';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private stompClient!: Client;
  private notificacionSubject = new Subject<Notificacion>();

  notificaciones$ = this.notificacionSubject.asObservable();

  conectar(idUsuario: number, token: string): void {
    console.log(token);
    this.stompClient = new Client({
      // Creamos la conexión SockJS
      webSocketFactory: () =>
        new SockJS(
          `${environment.apiUrl}/ws?token=${encodeURIComponent(token)}`
        ),
      connectHeaders: {},

      // Mostramos logs en consola
      debug: (str) => console.log('[STOMP]', str),

      // Reintento de conexión automático tras caída
      reconnectDelay: 5000,
    });

    // Cuando se conecta exitosamente
    this.stompClient.onConnect = () => {
      console.log('Conectado al WebSocket');

      // Suscripción al canal personalizado por ID de usuario
      this.stompClient.subscribe(
        `/topic/notificaciones/${idUsuario}`,
        (mensaje: IMessage) => {
          const notificacion: Notificacion = JSON.parse(mensaje.body);
          this.notificacionSubject.next(notificacion);
        }
      );
    };

    // Activamos conexión
    this.stompClient.activate();
  }

  desconectar(): void {
    if (this.stompClient && this.stompClient.active) {
      this.stompClient.deactivate();
      console.log('WebSocket desconectado');
    }
  }
}
