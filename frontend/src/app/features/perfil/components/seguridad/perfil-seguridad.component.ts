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
      this.toastr.error('Las contraseñas no coinciden', 'Error');
      return;
    }

    this.perfilService
      .cambiarPassword(this.idUsuario, { currentPassword, newPassword })
      .subscribe({
        next: () => {
          this.toastr.success('Contraseña actualizada correctamente', 'Éxito');
          this.passwordForm.reset();
        },
        error: (error) => {
          if (
            error.status === 400 &&
            error.error === 'Contraseña actual incorrecta'
          ) {
            this.toastr.error('La contraseña actual es incorrecta', 'Error');
          } else {
            this.toastr.error('Ocurrió un error inesperado', 'Error');
          }
        },
      });
  }

  cancelar(): void {
    this.passwordForm.reset();
    this.toastr.info('Cambios descartados', 'Cancelado');
  }
}
