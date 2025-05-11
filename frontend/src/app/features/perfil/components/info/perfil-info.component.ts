import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { AuthStore } from '../../../../core/auth/auth.store';
import { PerfilService } from '../../../../core/services/perfil.service';

@Component({
  selector: 'app-perfil-info',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './perfil-info.component.html',
  styleUrls: ['./perfil-info.component.scss'],
})
export class PerfilInfoComponent implements OnInit {
  infoForm!: FormGroup;
  idUsuario!: number;
  private datosOriginales: any;

  constructor(
    private fb: FormBuilder,
    private perfilService: PerfilService,
    private authStore: AuthStore,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.infoForm = this.fb.group({
      nombre: [''],
      email: [''],
      telefono: [''],
      direccion: [''],
    });

    this.idUsuario = this.authStore.getIdUsuarioValue()!;
    if (this.idUsuario) {
      this.perfilService.obtenerPerfil(this.idUsuario).subscribe((usuario) => {
        const { nombre, email, telefono, direccion } = usuario;
        this.infoForm.patchValue({ nombre, email, telefono, direccion });
        this.datosOriginales = { nombre, email, telefono, direccion };
      });
    }
  }

  guardar(): void {
    if (this.infoForm.valid) {
      const datos = this.infoForm.value;
      this.perfilService
        .actualizarPerfil(this.idUsuario, datos)
        .subscribe(() => {
          this.toastr.success('Perfil actualizado correctamente', 'Éxito');
        });
    }
  }

  cancelar(): void {
    if (this.datosOriginales) {
      this.infoForm.patchValue(this.datosOriginales);
      this.toastr.info('Cambios descartados', 'Cancelado');
    }
  }
}
