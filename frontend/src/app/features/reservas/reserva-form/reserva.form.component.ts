import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Reserva } from '../../../core/models/reserva.model';
import { EspacioService } from '../../../core/services/espacio.service';
import { EstadoReservaService } from '../../../core/services/estadoreserva.service';
import { ReservaService } from '../../../core/services/reserva.service';
import { UsuarioService } from '../../../core/services/usuario.service';

@Component({
  selector: 'app-reserva-form',
  templateUrl: './reserva.form.component.html',
  styleUrls: ['./reserva.form.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
})
export class ReservaFormComponent implements OnInit {
  form: FormGroup;
  modoEdicion = false;
  idReserva?: number;

  usuarios: any[] = [];
  espacios: any[] = [];
  estados: any[] = [];

  constructor(
    private fb: FormBuilder,
    private reservaService: ReservaService,
    private usuarioService: UsuarioService,
    private espacioService: EspacioService,
    private estadoReservaService: EstadoReservaService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      idUsuario: ['', Validators.required],
      idEspacio: ['', Validators.required],
      idEstado: ['', Validators.required],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required],
      sincronizado: [false],
    });
  }

  ngOnInit(): void {
    this.usuarioService
      .getUsuarios()
      .subscribe((data) => (this.usuarios = data));

    console.log(this.usuarios);
    this.espacioService
      .getEspacios()
      .subscribe((data) => (this.espacios = data));
    console.log(this.espacios);
    this.estadoReservaService
      .getEstadosReserva()
      .subscribe((data) => (this.estados = data));
    console.log(this.estados);

    this.route.params.subscribe((params) => {
      if (params['id']) {
        this.modoEdicion = true;
        this.idReserva = +params['id'];
        this.reservaService
          .getReservaById(this.idReserva)
          .subscribe((reserva) => {
            this.form.patchValue(reserva);
          });
      }
    });
  }

  guardar(): void {
    if (this.form.invalid) return;

    const reserva: Reserva = this.form.value;

    if (this.modoEdicion && this.idReserva) {
      this.reservaService
        .updateReserva(this.idReserva, reserva)
        .subscribe(() => {
          this.router.navigate(['/reservas']);
        });
    } else {
      this.reservaService.createReserva(reserva).subscribe(() => {
        this.router.navigate(['/reservas']);
      });
    }
  }
}
