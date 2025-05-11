import { CommonModule } from '@angular/common';
import { AfterViewInit, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import Chart from 'chart.js/auto';
import {
  DashboardResponse,
  KPI,
  TipoSalaDistribucion,
  UltimaReserva,
} from '../../core/models/dashboard.model';
import { EstadoReserva } from '../../core/models/estadoreserva.model';
import { TipoEspacio } from '../../core/models/tipoespacio.model';
import { DashboardService } from '../../core/services/dashboard.service';
import { EstadoReservaService } from '../../core/services/estadoreserva.service';
import { TipoEspacioService } from '../../core/services/tipoespacio.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  imports: [CommonModule, FormsModule],
})
export class DashboardComponent implements AfterViewInit {
  kpi: KPI = {
    totalReservas: 0,
    tasaOcupacion: 0,
    usuariosActivos: 0,
    horasReservadas: 0,
  };
  ultimasReservas: UltimaReserva[] = [];
  reservasPeriodo: number[] = [];
  diasSemana: string[] = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
  tipoSalaDistribucion: TipoSalaDistribucion[] = [];

  // Filtros
  filtro: {
    fechaInicio: string;
    fechaFin: string;
    tipoSala: string | null;
    estado: string | null;
  } = {
    fechaInicio: '',
    fechaFin: '',
    tipoSala: null,
    estado: null,
  };

  tiposSala: string[] = [];
  tiposEspacio: TipoEspacio[] = [];
  estadosReserva: EstadoReserva[] = [];

  barChart: Chart | null = null;
  doughnutChart: Chart | null = null;

  constructor(
    private dashboardService: DashboardService,
    private tipoEspacioService: TipoEspacioService,
    private estadoReservaService: EstadoReservaService
  ) {}

  ngAfterViewInit(): void {
    this.tipoEspacioService.getTiposEspacio().subscribe({
      next: (tipos) => {
        this.tiposEspacio = tipos;
      },
      error: (err) => {
        console.error('Error al cargar tipos de espacio', err);
      },
    });

    this.estadoReservaService.getEstadosReserva().subscribe({
      next: (estados) => {
        this.estadosReserva = estados;
      },
      error: (err) => {
        console.error('Error al cargar los estados de la reserva', err);
      },
    });

    this.cargarDashboard();
  }

  aplicarFiltros(): void {
    this.cargarDashboard();
  }

  limpiarFiltros(): void {
    this.filtro = {
      fechaInicio: '',
      fechaFin: '',
      tipoSala: null,
      estado: null,
    };
    this.cargarDashboard();
  }

  cargarDashboard(): void {
    this.dashboardService.obtenerDatosDashboard(this.filtro).subscribe({
      next: (data: DashboardResponse) => {
        this.kpi = data.kpi;
        this.ultimasReservas = data.ultimasReservas;
        this.reservasPeriodo = data.reservasPeriodo;
        this.tipoSalaDistribucion = data.tipoSalaDistribucion;
        this.tiposSala = this.tipoSalaDistribucion.map((t) => t.tipo);
        this.cargarGraficos();
      },
      error: (error) => {
        console.error('Error al cargar datos del dashboard', error);
      },
    });
  }

  cargarGraficos(): void {
    if (this.barChart) {
      this.barChart.destroy();
    }
    if (this.doughnutChart) {
      this.doughnutChart.destroy();
    }

    // Bar Chart
    const canvasBar = document.getElementById(
      'reservasPeriodoChart'
    ) as HTMLCanvasElement;
    const ctxBar = canvasBar?.getContext('2d');
    if (ctxBar) {
      this.barChart = new Chart(ctxBar, {
        type: 'bar',
        data: {
          labels: this.diasSemana,
          datasets: [
            {
              label: 'Reservas',
              data: this.reservasPeriodo,
              backgroundColor: '#4f46e5',
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: { beginAtZero: true },
          },
        },
      });
    }

    // Doughnut Chart
    const canvasPie = document.getElementById(
      'tipoSalaChart'
    ) as HTMLCanvasElement;
    const ctxPie = canvasPie?.getContext('2d');
    if (ctxPie) {
      this.doughnutChart = new Chart(ctxPie, {
        type: 'doughnut',
        data: {
          labels: this.tipoSalaDistribucion.map((t) => t.tipo),
          datasets: [
            {
              data: this.tipoSalaDistribucion.map((t) => t.valor),
              backgroundColor: ['#6366f1', '#10b981', '#f59e0b', '#f43f5e'],
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: '70%',
        },
      });
    }
  }
}
