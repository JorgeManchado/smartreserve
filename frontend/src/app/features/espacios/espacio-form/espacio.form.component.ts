import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
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

  constructor(
    private fb: FormBuilder,
    private espacioService: EspacioService,
    private tipoService: TipoEspacioService,
    private estadoService: EstadoEspacioService,
    private equipamientoService: EquipamientoService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nombre: ['', Validators.required],
      capacidad: [1, [Validators.required, Validators.min(1)]],
      ubicacion: ['', Validators.required],
      descripcion: [''],
      idTipoEspacio: ['', Validators.required],
      idEstado: ['', Validators.required],
      equipamientos: [[]],
    });

    this.tipoService
      .getTiposEspacio()
      .subscribe((data) => (this.tiposEspacio = data));
    this.estadoService
      .getEstadospacio()
      .subscribe((data) => (this.estadosEspacio = data));
    this.equipamientoService
      .getEquipamientos()
      .subscribe((data) => (this.equipamientos = data));

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
          idEstado: data.estado?.idEstado,
          equipamientos: data.equipamientos?.map((e: any) => e.idEquipamiento),
        });

        if (data.imagen) {
          this.imagenPreview = `${environment.apiUrl}/uploads/${data.imagen}`;
        }
      });
    }
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
    if (this.form.invalid) return;

    const formData = new FormData();

    const {
      nombre,
      capacidad,
      ubicacion,
      descripcion,
      idTipoEspacio,
      idEstado,
      equipamientos,
    } = this.form.value;

    formData.append('nombre', nombre);
    formData.append('capacidad', capacidad.toString());
    formData.append('ubicacion', ubicacion);
    formData.append('descripcion', descripcion);
    formData.append('idTipoEspacio', idTipoEspacio.toString());
    formData.append('idEstado', idEstado.toString());

    equipamientos.forEach((id: number) => {
      formData.append('equipamientos', id.toString());
    });

    if (this.selectedFile) {
      formData.append('imagen', this.selectedFile, this.selectedFile.name);
    }

    const request = this.isEditMode
      ? this.espacioService.updateEspacio(this.idEspacio, formData)
      : this.espacioService.createEspacio(formData);

    request.subscribe(() => this.router.navigate(['/espacios']));
  }

  cancelar(): void {
    this.router.navigate(['/espacios']);
  }
}
