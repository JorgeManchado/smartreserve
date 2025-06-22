import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { AuthStore } from '../../../../core/auth/auth.store';
import { PerfilService } from '../../../../core/services/perfil.service';

@Component({
  selector: 'app-perfil-seguridad',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './perfil-seguridad.component.html',
  styleUrls: ['./perfil-seguridad.component.scss'],
})
export class PerfilSeguridadComponent implements OnInit {
  passwordForm!: FormGroup;
  idUsuario!: number;

  constructor(
    private fb: FormBuilder,
    private perfilService: PerfilService,
    private authStore: AuthStore,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
    });

    this.idUsuario = this.authStore.getIdUsuarioValue()!;
  }

  cambiarPassword() {
    const { currentPassword, newPassword, confirmPassword } =
      this.passwordForm.value;

    if (newPassword !== confirmPassword) {
      this.toastr.info(
        `
          <div class="toast-content">
            <div class="toast-title">Cambio de Contraseña</div>
            <div class="toast-message">
              Las contraseñas no coinciden.
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

    this.perfilService
      .cambiarPassword(this.idUsuario, { currentPassword, newPassword })
      .subscribe({
        next: () => {
          this.toastr.success(
            `
              <div class="toast-content">
                <div class="toast-title">Contraseña actualizada</div>
                <div class="toast-message">
                  La contraseña se ha actualizado correctamente.
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

          this.passwordForm.reset();
        },
        error: (error) => {
          if (
            error.status === 400 &&
            error.error === 'Contraseña actual incorrecta'
          ) {
            this.toastr.info(
              `
                <div class="toast-content">
                  <div class="toast-title">Cambio de Contraseña</div>
                  <div class="toast-message">
                    La contraseña actual es incorrecta
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
          } else {
            this.toastr.error(
              `
                <div class="toast-content">
                  <div class="toast-title">Cambio de Contraseña</div>
                  <div class="toast-message">
                    Ocurrió un error inesperado
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
        },
      });
  }

  cancelar(): void {
    this.passwordForm.reset();
    this.toastr.info(
      `
        <div class="toast-content">
          <div class="toast-title">Cambios Cancelados</div>
          <div class="toast-message">
             Los cambios han sido descartados correctamente.
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
}
