import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { EstadoUsuario } from '../../../core/models/estadousuario.model';
import { Rol } from '../../../core/models/rol.model';
import { UsuarioRequestDTO } from '../../../core/models/usuario-request.dto';
import { EstadoUsuarioService } from '../../../core/services/estadousuario.service';
import { RolService } from '../../../core/services/rol.service';
import { UsuarioService } from '../../../core/services/usuario.service';

@Component({
  selector: 'app-usuario-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './usuario-form.component.html',
})
export class UsuarioFormComponent implements OnInit {
  form!: FormGroup;
  usuarioId?: number;
  isEditMode = false;
  roles: Rol[] = [];
  estadosUsuario: EstadoUsuario[] = [];
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private estadoUsuarioService: EstadoUsuarioService,
    private rolService: RolService,
    private route: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.usuarioId = Number(this.route.snapshot.paramMap.get('id'));
    this.isEditMode = !!this.usuarioId;

    this.rolService.getRoles().subscribe((data) => {
      console.log('roles', data);
      this.roles = data;
    });

    this.estadoUsuarioService.getEstadosUsuario().subscribe((data) => {
      console.log('estadousuario', data);
      this.estadosUsuario = data;
    });

    this.initForm();

    if (this.isEditMode) {
      this.usuarioService
        .getUsuarioById(this.usuarioId!)
        .subscribe((usuario) => {
          this.form.patchValue(usuario);
          this.form.get('password')?.clearValidators();
          this.form.get('password')?.updateValueAndValidity();
        });
    }
  }

  initForm() {
    this.form = this.fb.group({
      nombre: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telefono: [''],
      direccion: [''],
      idRol: [this.isEditMode ? null : 2, Validators.required],
      idEstado: [this.isEditMode ? null : 1, Validators.required],
      password: ['', this.isEditMode ? [] : [Validators.required]],
    });
  }

  onSubmit() {
    if (this.form.invalid) return;

    const usuario: UsuarioRequestDTO = this.form.value;
    this.errorMessage = null;

    if (this.isEditMode) {
      this.usuarioService.updateUsuario(this.usuarioId!, usuario).subscribe({
        next: () => {
          this.toastr.success(
            `
            <div class="toast-content">
              <div class="toast-title">Usuario actualizado</div>
              <div class="toast-message">
                El usuario <strong>"${usuario.nombre}"</strong> fue actualizado correctamente.
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

          this.router.navigate(['/usuarios']);
        },
        error: (err) => {
          if (err.status === 409) {
            this.errorMessage = err.error;
          } else {
            this.errorMessage = 'Ocurrió un error al actualizar el usuario.';
          }
          console.error(err);
        },
      });
    } else {
      this.usuarioService.createUsuario(usuario).subscribe({
        next: () => {
          this.toastr.success(
            `
            <div class="toast-content">
              <div class="toast-title">Usuario creado</div>
              <div class="toast-message">
                El usuario <strong>"${usuario.nombre}"</strong> fue creado correctamente.
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

          this.router.navigate(['/usuarios']);
        },
        error: (err) => {
          if (err.status === 409) {
            this.errorMessage = err.error;
          } else {
            this.errorMessage = 'Ocurrió un error al crear el usuario.';
          }
          console.error(err);
        },
      });
    }
  }

  cancelar() {
    this.router.navigate(['/usuarios']);
  }
}
