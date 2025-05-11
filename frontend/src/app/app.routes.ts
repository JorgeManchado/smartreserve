import { Routes } from '@angular/router';
import { adminGuard } from './core/auth/admin.guard';
import { empleadoGuard } from './core/auth/empleado.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./layout/layout.component').then((m) => m.LayoutComponent),
    children: [
      {
        path: 'dashboard',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then(
            (m) => m.DashboardComponent
          ),
      },
      {
        path: 'usuarios',
        canActivate: [adminGuard],
        loadComponent: () =>
          import(
            './features/usuarios/usuario-list/usuarios.page.component'
          ).then((m) => m.UsuariosPageComponent),
      },
      {
        path: 'comentarios',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/comentarios/comentario.component').then(
            (m) => m.ComentarioComponent
          ),
      },
      {
        path: 'usuarios/nuevo',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/usuarios/usuario-form/usuario-form.componet').then(
            (m) => m.UsuarioFormComponent
          ),
      },
      {
        path: 'usuarios/editar/:id',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/usuarios/usuario-form/usuario-form.componet').then(
            (m) => m.UsuarioFormComponent
          ),
      },

      {
        path: 'espacios',
        canActivate: [adminGuard],
        loadComponent: () =>
          import(
            './features/espacios/espacio-list/espacios.page.component'
          ).then((m) => m.EspaciosPageComponent),
      },
      {
        path: 'espacios/nuevo',
        canActivate: [adminGuard],
        loadComponent: () =>
          import(
            './features/espacios/espacio-form/espacio.form.component'
          ).then((m) => m.EspacioFormComponent),
      },
      {
        path: 'espacios/editar/:id',
        canActivate: [adminGuard],
        loadComponent: () =>
          import(
            './features/espacios/espacio-form/espacio.form.component'
          ).then((m) => m.EspacioFormComponent),
      },

      {
        path: 'perfil',
        canActivate: [empleadoGuard],
        loadComponent: () =>
          import('./features/perfil/pages/perfil.component').then(
            (m) => m.PerfilComponent
          ),
      },

      {
        path: 'reservas',
        canActivate: [empleadoGuard],
        loadComponent: () =>
          import(
            './features/reservas/reserva-list/reservas.page.component'
          ).then((m) => m.ReservasPageComponent),
      },
      {
        path: 'reservas/nueva',
        canActivate: [empleadoGuard],
        loadComponent: () =>
          import(
            './features/reservas/reserva-form/reserva.form.component'
          ).then((m) => m.ReservaFormComponent),
      },
      {
        path: 'reservas/editar/:id',
        canActivate: [empleadoGuard],
        loadComponent: () =>
          import(
            './features/reservas/reserva-form/reserva.form.component'
          ).then((m) => m.ReservaFormComponent),
      },

      {
        path: 'misreservas',
        canActivate: [empleadoGuard],
        loadComponent: () =>
          import('./features/auth/reservas/reservas.page').then(
            (m) => m.ReservasPage
          ),
      },
      {
        path: '',
        redirectTo: '/login',
        pathMatch: 'full',
      },
    ],
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.page').then((m) => m.LoginPage),
  },
];
