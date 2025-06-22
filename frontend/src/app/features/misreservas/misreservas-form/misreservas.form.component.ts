import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AuthStore } from '../../../core/auth/auth.store';
import { MisReserva } from '../../../core/models/misreserva.model';
import { Recomendacion } from '../../../core/models/recomendacion.model';
import { EspacioService } from '../../../core/services/espacio.service';
import { EstadoReservaService } from '../../../core/services/estadoreserva.service';
import { GoogleAuthService } from '../../../core/services/googleauth.service';
import { MisReservaService } from '../../../core/services/misreserva.service';
import { RecomendacionService } from '../../../core/services/recomendacion.service';
import { UsuarioService } from '../../../core/services/usuario.service';

declare var bootstrap: any;

@Component({
  selector: 'app-reserva-form',
  templateUrl: './misreservas.form.component.html',
  styleUrls: ['./misreservas.form.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
})
export class MisReservasFormComponent implements OnInit {
  form: FormGroup;
  modoEdicion = false;
  idReserva?: number;

  returnTo: string = '/misreservas';
  fechaDesdeCalendario: Date | null = null;

  usuarios: any[] = [];
  espacios: any[] = [];
  estados: any[] = [];
  recomendaciones: Recomendacion[] = [];
  mostrarRecomendaciones = false;
  cargandoRecomendaciones = false;

  nombreDiaSemana: string = '';
  espacioSeleccionado: any = null;
  usuarioNombre: string = '';

  constructor(
    private fb: FormBuilder,
    private reservaService: MisReservaService,
    private recomendacionService: RecomendacionService,
    private usuarioService: UsuarioService,
    private espacioService: EspacioService,
    private estadoReservaService: EstadoReservaService,
    private authStore: AuthStore,
    private route: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService,
    private googleAuthService: GoogleAuthService
  ) {
    this.form = this.fb.group({
      idUsuario: ['', Validators.required],
      idEspacio: ['', Validators.required],
      idEstado: [{ value: 2, disabled: true }, Validators.required], // Por defecto "Pendiente"
      fechaInicio: ['', Validators.required],
      horaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required],
      horaFin: ['', Validators.required],
      numeroOcupantes: ['', [Validators.required, Validators.min(1)]],
      sincronizado: [false],
      recomendadoia: [{ value: false, disabled: true }],
    });
  }

  ngOnInit(): void {
    const usuarioId = this.authStore.getIdUsuarioValue();
    if (usuarioId) {
      this.form.patchValue({ idUsuario: usuarioId });
    }

    this.route.queryParams.subscribe((params) => {
      if (params['returnTo']) {
        this.returnTo = params['returnTo'];
      }

      if (params['fecha']) {
        const fecha = new Date(params['fecha']);
        if (!isNaN(fecha.getTime())) {
          this.fechaDesdeCalendario = fecha;
          this.preRellenarFechas(fecha);
        }
      }
    });

    this.usuarioService.getUsuarios().subscribe((data) => {
      this.usuarios = data;
      this.usuarioNombre =
        this.usuarios.find((u) => u.idUsuario === usuarioId)?.nombre || '';
    });

    this.espacioService.getEspacios().subscribe((data) => {
      this.espacios = data.filter((e) => e.estado.descripcion === 'Disponible');

      // Si viene de "reservar de nuevo"
      const espacioId = history.state?.espacioId;
      if (espacioId) {
        this.form.patchValue({ idEspacio: espacioId });

        this.form.get('recomendadoIa')?.enable();
        this.form.get('recomendadoIa')?.setValue(false); // no recomendado por IA
        this.form.get('recomendadoIa')?.disable();

        this.actualizarEspacioSeleccionado();
      } else {
        // Solo si NO viene de "reservar de nuevo"
        this.obtenerRecomendacionesAlta();
      }
    });

    this.estadoReservaService.getEstadosReserva().subscribe((data) => {
      this.estados = data;
    });

    this.form.get('fechaInicio')?.valueChanges.subscribe((fecha) => {
      if (fecha) {
        this.form.patchValue({ fechaFin: fecha });
      }
    });

    this.form.get('idEspacio')?.valueChanges.subscribe((idSeleccionado) => {
      this.actualizarEspacioSeleccionado();

      const esRecomendado = this.recomendaciones.some(
        (rec) => rec.espacio_id === idSeleccionado
      );

      if (!esRecomendado) {
        this.form.get('recomendadoIa')?.enable();
        this.form.get('recomendadoIa')?.setValue(false);
        this.form.get('recomendadoIa')?.disable();
      }
    });

    // Modo edición si existe ID en ruta
    this.route.params.subscribe((params) => {
      if (params['id']) {
        this.modoEdicion = true;
        this.idReserva = +params['id'];
        this.reservaService
          .getReservaById(this.idReserva)
          .subscribe((reserva) => {
            this.form.patchValue({
              ...reserva,
              horaInicio: reserva.fechaInicio.slice(11, 16),
              horaFin: reserva.fechaFin.slice(11, 16),
              fechaInicio: reserva.fechaInicio.slice(0, 10),
              fechaFin: reserva.fechaFin.slice(0, 10),
              numeroOcupantes: reserva.ocupantes,
              idEstado: reserva.estado.idEstado,
            });

            this.form.get('idEstado')?.enable();
            this.actualizarEspacioSeleccionado();
          });
      }
    });
  }

  actualizarEspacioSeleccionado(): void {
    const id = +this.form.get('idEspacio')?.value;
    this.espacioSeleccionado =
      this.espacios.find((e) => e.idEspacio === id) || null;
  }

  obtenerRecomendacionesAlta(): void {
    const usuarioId = this.authStore.getIdUsuarioValue();
    if (!usuarioId) return;

    const fecha = new Date();
    const diaSemana = fecha.getDay();
    const hora = -1;

    const dias = [
      'domingo',
      'lunes',
      'martes',
      'miércoles',
      'jueves',
      'viernes',
      'sábado',
    ];
    this.nombreDiaSemana = dias[diaSemana];

    this.cargandoRecomendaciones = true;
    this.recomendacionService
      .getRecomendaciones(usuarioId, diaSemana, hora)
      .subscribe({
        next: (res) => {
          this.recomendaciones = res.recomendaciones;
          this.mostrarRecomendaciones = this.recomendaciones.length > 0;
          this.cargandoRecomendaciones = false;
        },
        error: () => {
          this.cargandoRecomendaciones = false;
          this.recomendaciones = [];
          this.mostrarRecomendaciones = false;
        },
      });
  }

  aplicarRecomendacion(rec: Recomendacion): void {
    this.form.patchValue({
      idEspacio: rec.espacio_id,
    });

    this.form.get('recomendadoia')?.enable();
    this.form.get('recomendadoia')?.setValue(true);
    this.form.get('recomendadoia')?.disable();

    this.espacioSeleccionado =
      this.espacios.find((e) => e.idEspacio === rec.espacio_id) || null;
  }

  private validarFechas(): boolean {
    const fechaHoy = new Date();

    const fechaInicioStr = this.form.get('fechaInicio')?.value;
    const horaInicioStr = this.form.get('horaInicio')?.value;
    const fechaFinStr = this.form.get('fechaFin')?.value;
    const horaFinStr = this.form.get('horaFin')?.value;

    if (!fechaInicioStr || !horaInicioStr || !fechaFinStr || !horaFinStr) {
      return false;
    }

    const fechaInicio = new Date(`${fechaInicioStr}T${horaInicioStr}`);
    const fechaFin = new Date(`${fechaFinStr}T${horaFinStr}`);

    if (fechaInicio < fechaHoy) {
      this.toastr.info(
        `              
          <div class="toast-content">
            <div class="toast-title">Fecha inválida</div>
            <div class="toast-message">
              La fecha y hora de inicio no pueden ser anteriores al momento actual.
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

      return false;
    }

    if (fechaFin <= fechaInicio) {
      this.toastr.info(
        `              
          <div class="toast-content">
            <div class="toast-title">Fecha inválida</div>
            <div class="toast-message">
             La fecha y hora de finalización deben ser posteriores a la de inicio.
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

      return false;
    }

    return true;
  }

  guardar(): void {
    if (this.form.invalid || !this.validarFechas()) return;

    const formValue = this.form.getRawValue();
    const procesar = () => this.procesarReserva();

    if (formValue.sincronizado) {
      this.googleAuthService.checkTokenStatus().subscribe((tokenPresente) => {
        if (tokenPresente) {
          procesar();
        } else {
          const popup = window.open(
            `${environment.apiUrl}/api/google/auth`,
            '_blank'
          );

          const espera = setInterval(() => {
            if (popup?.closed) {
              clearInterval(espera);
              this.googleAuthService.checkTokenStatus().subscribe((valido) => {
                if (valido) {
                  procesar();
                } else {
                  this.toastr.warning(
                    `
                    <div class="toast-content">
                      <div class="toast-title">Autenticación Google Calendar</div>
                      <div class="toast-message">
                        No se pudo autenticar con Google. La reserva se ha guardado sin sincronización.
                      </div>
                    </div>
                  `,
                    '',
                    {
                      enableHtml: true,
                      toastClass: 'ngx-toastr custom-toast toast-warning',
                      closeButton: true,
                      timeOut: 3000,
                    }
                  );
                  procesar(); // continuar incluso si no hay autenticación
                }
              });
            }
          }, 1000);
        }
      });
    } else {
      procesar();
    }
  }

  procesarReserva(): void {
    const formValue = this.form.getRawValue();
    const idUsuario = formValue.idUsuario;
    const idEspacio = +formValue.idEspacio;
    const idEstado = formValue.idEstado;

    const espacio = this.espacios.find((e) => e.idEspacio === idEspacio);
    if (espacio && formValue.numeroOcupantes > espacio.capacidad) {
      this.toastr.info(
        `
          <div class="toast-content">
            <div class="toast-title">Capacidad del Espacio</div>
            <div class="toast-message">
               Solo se permiten hasta <strong>${espacio.capacidad}</strong> personas en este espacio.
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

    const fechaInicio = this.combinarFechaYHora(
      formValue.fechaInicio,
      formValue.horaInicio
    );
    const fechaFin = this.combinarFechaYHora(
      formValue.fechaFin,
      formValue.horaFin
    );

    this.reservaService
      .verificarDisponibilidad({
        idEspacio,
        inicio: fechaInicio,
        fin: fechaFin,
      })
      .subscribe({
        next: (disponible) => {
          if (!disponible) {
            this.toastr.info(
              `
                <div class="toast-content">
                  <div class="toast-title">Reserva</div>
                  <div class="toast-message">
                    Ya existe una reserva en ese espacio para ese horario. Elige otro momento disponible.
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

          forkJoin({
            usuario: this.usuarioService.getUsuarioById(idUsuario),
            espacio: this.espacioService.getEspacioById(idEspacio),
            estado: this.estadoReservaService.getEstadoReservaById(idEstado),
          }).subscribe(({ usuario, espacio, estado }) => {
            const reserva: MisReserva = {
              ...(this.modoEdicion ? { idReserva: this.idReserva } : {}),
              usuario,
              espacio,
              estado,
              fechaInicio,
              fechaFin,
              sincronizado: formValue.sincronizado,
              recomendadaia: formValue.recomendadoia,
              ocupantes: formValue.numeroOcupantes,
            };

            const obs = this.modoEdicion
              ? this.reservaService.updateReserva(this.idReserva!, reserva)
              : this.reservaService.createReserva(reserva);

            obs.subscribe(() => {
              this.toastr.success(
                `
                  <div class="toast-content">
                    <div class="toast-title">Reserva</div>
                    <div class="toast-message">
                      Reserva registrada correctamente.
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
              this.router.navigateByUrl(this.returnTo);
            });
          });
        },
        error: () => {
          this.toastr.error(
            `
              <div class="toast-content">
                <div class="toast-title">Reserva</div>
                <div class="toast-message">
                  Error al verificar disponibilidad.
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
        },
      });
  }

  private combinarFechaYHora(fecha: string, hora: string): string {
    return `${fecha}T${hora}`;
  }

  getImagenUrl(nombreImagen: string): string {
    return `${environment.apiUrl}/uploads/${nombreImagen}`;
  }

  getDescripcionEstado(idEstado: number): string {
    const estado = this.estados.find((e) => e.idEstado === idEstado);
    return estado ? estado.descripcion : 'Pendiente';
  }

  cancelarReserva(): void {
    this.router.navigateByUrl(this.returnTo);
  }

  preRellenarFechas(fecha: Date): void {
    const fechaStr = fecha.toISOString().split('T')[0]; // formato yyyy-MM-dd

    this.form.patchValue({
      fechaInicio: fechaStr,
      //horaInicio: '09:00',
      fechaFin: fechaStr,
      //horaFin: '10:00',
    });
  }
}
