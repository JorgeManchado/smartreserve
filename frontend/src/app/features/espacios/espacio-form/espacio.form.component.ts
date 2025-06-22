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
import { environment } from '../../../../environments/environment';
import { Equipamiento } from '../../../core/models/equipamiento.model';
import { EstadoEspacio } from '../../../core/models/estadoespacio.model';
import { TipoEspacio } from '../../../core/models/tipoespacio.model';
import { EquipamientoService } from '../../../core/services/equipamiento.service';
import { EspacioService } from '../../../core/services/espacio.service';
import { EstadoEspacioService } from '../../../core/services/estadoespacio.service';
import { TipoEspacioService } from '../../../core/services/tipoespacio.service';

@Component({
  selector: 'app-espacio-form',
  templateUrl: './espacio.form.component.html',
  imports: [CommonModule, ReactiveFormsModule],
})
export class EspacioFormComponent implements OnInit {
  form!: FormGroup;
  tiposEspacio: TipoEspacio[] = [];
  estadosEspacio: EstadoEspacio[] = [];
  equipamientos: Equipamiento[] = [];
  imagenPreview: string | ArrayBuffer | null = null;
  selectedFile: File | null = null;
  isEditMode = false;
  idEspacio!: number;
  estadoIdSeleccionado: number | null = null;

  constructor(
    private fb: FormBuilder,
    private espacioService: EspacioService,
    private tipoService: TipoEspacioService,
    private estadoService: EstadoEspacioService,
    private equipamientoService: EquipamientoService,
    private route: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nombre: ['', Validators.required],
      capacidad: [1, [Validators.required, Validators.min(1)]],
      ubicacion: ['', Validators.required],
      descripcion: [''],
      idTipoEspacio: ['', Validators.required],
      equipamientos: [[], [Validators.required, this.minSelectedCheckboxes(1)]],
    });

    this.tipoService.getTiposEspacio().subscribe((data) => {
      this.tiposEspacio = data;
    });

    this.estadoService.getEstadospacio().subscribe((data) => {
      this.estadosEspacio = data;

      if (!this.isEditMode) {
        const estadoDisponible = data.find(
          (e) => e.descripcion === 'Disponible'
        );
        console.log(estadoDisponible);
        if (estadoDisponible) {
          this.estadoIdSeleccionado = estadoDisponible.idEstado;
        }
      }
    });

    this.equipamientoService.getEquipamientos().subscribe((data) => {
      this.equipamientos = data;
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.idEspacio = +id;
      this.espacioService.getEspacioById(this.idEspacio).subscribe((data) => {
        this.form.patchValue({
          nombre: data.nombre,
          capacidad: data.capacidad,
          ubicacion: data.ubicacion,
          descripcion: data.descripcion,
          idTipoEspacio: data.tipo?.idTipoEspacio,
          equipamientos: data.equipamientos?.map((e: any) => e.idEquipamiento),
        });

        this.estadoIdSeleccionado = data.estado?.idEstado || null;

        if (data.imagen) {
          this.imagenPreview = `${environment.apiUrl}/uploads/${data.imagen}`;
        }
      });
    }
  }

  minSelectedCheckboxes(min = 1) {
    return (control: any) => {
      const value = control.value;
      return value && value.length >= min ? null : { minSelected: true };
    };
  }

  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = () => (this.imagenPreview = reader.result);
      reader.readAsDataURL(file);
    }
  }

  onToggleEquipamiento(event: any): void {
    const selected = this.form.value.equipamientos || [];
    const value = +event.target.value;
    if (event.target.checked) {
      this.form.patchValue({ equipamientos: [...selected, value] });
    } else {
      this.form.patchValue({
        equipamientos: selected.filter((id: number) => id !== value),
      });
    }
  }

  onSubmit(): void {
    if (this.form.invalid || !this.estadoIdSeleccionado) {
      this.form.markAllAsTouched();
      return;
    }

    const formData = new FormData();
    const {
      nombre,
      capacidad,
      ubicacion,
      descripcion,
      idTipoEspacio,
      equipamientos,
    } = this.form.value;

    formData.append('nombre', nombre);
    formData.append('capacidad', capacidad.toString());
    formData.append('ubicacion', ubicacion);
    formData.append('descripcion', descripcion);
    formData.append('idTipoEspacio', idTipoEspacio.toString());
    formData.append('idEstado', this.estadoIdSeleccionado.toString());

    equipamientos.forEach((id: number) => {
      formData.append('equipamientos', id.toString());
    });

    if (this.selectedFile) {
      formData.append('imagen', this.selectedFile, this.selectedFile.name);
    }

    const request = this.isEditMode
      ? this.espacioService.updateEspacio(this.idEspacio, formData)
      : this.espacioService.createEspacio(formData);

    request.subscribe(() => {
      const mensaje = this.isEditMode
        ? {
            titulo: 'Espacio actualizado',
            texto: `El espacio <strong>"${nombre}"</strong> fue actualizado correctamente.`,
          }
        : {
            titulo: 'Espacio creado',
            texto: `El espacio <strong>"${nombre}"</strong> fue creado correctamente.`,
          };

      this.toastr.success(
        `
    <div class="toast-content">
      <div class="toast-title">${mensaje.titulo}</div>
      <div class="toast-message">${mensaje.texto}</div>
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

      this.router.navigate(['/espacios']);
    });
  }

  cancelar(): void {
    this.router.navigate(['/espacios']);
  }
}
