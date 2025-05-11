import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AuthStore } from '../../../core/auth/auth.store';
import { PerfilInfoComponent } from '../components/info/perfil-info.component';
import { PerfilNotificacionesComponent } from '../components/notificaciones/perfil-notificaciones.component';
import { PerfilSeguridadComponent } from '../components/seguridad/perfil-seguridad.component';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [
    CommonModule,
    PerfilInfoComponent,
    PerfilSeguridadComponent,
    PerfilNotificacionesComponent,
  ],
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.scss'],
})
export class PerfilComponent implements OnInit {
  activeTab: 'info' | 'seguridad' | 'notificaciones' = 'info';
  nombre: string = '';
  rol: string = '';

  constructor(private authStore: AuthStore) {}

  ngOnInit(): void {
    this.nombre = this.authStore.getNameValue() || 'Usuario';
    this.rol = this.authStore.getRoleValue() || 'Rol no definido';
  }

  changeTab(tab: 'info' | 'seguridad' | 'notificaciones') {
    this.activeTab = tab;
  }
}
