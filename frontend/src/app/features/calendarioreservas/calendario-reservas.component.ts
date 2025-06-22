import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions } from '@fullcalendar/core';
import esLocale from '@fullcalendar/core/locales/es';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import timeGridPlugin from '@fullcalendar/timegrid';

import { EstadoReserva } from '../../core/models/estadoreserva.model';
import { TipoEspacio } from '../../core/models/tipoespacio.model';

import { Router } from '@angular/router';
import { EstadoReservaService } from '../../core/services/estadoreserva.service';
import { ReservaService } from '../../core/services/reserva.service';
import { TipoEspacioService } from '../../core/services/tipoespacio.service';

@Component({
  selector: 'app-calendario-reservas',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FullCalendarModule, FormsModule],
  templateUrl: './calendario-reservas.component.html',
  styleUrls: ['./calendario-reservas.component.scss'],
})
export class CalendarioReservasComponent implements OnInit {
  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    locale: esLocale,
    timeZone: 'local',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay',
    },
    height: 'auto',
    eventDisplay: 'block',
    dayMaxEventRows: true,
    dayMaxEvents: true,
    events: [],
    eventClassNames: this.asignarClaseEvento.bind(this),
    datesSet: (info) => {
      this.cargarReservas(info.start, info.end);
    },

    eventDidMount: (info) => {
      const { usuario, estado, sala, bgcolor, color } =
        info.event.extendedProps;

      const el = info.el as HTMLElement;
      const viewType = info.view.type;

      el.classList.add('evento-renderizado');
      el.innerHTML = '';

      // Vista mensual

      if (viewType === 'dayGridMonth') {
        const wrapper = document.createElement('div');
        wrapper.style.display = 'block'; // Simpler layout
        wrapper.style.width = '100%';
        wrapper.style.minWidth = '0';

        const text = document.createElement('span');
        text.innerText = info.event.title;
        text.style.color = color || '#1f2937';
        text.style.fontSize = '0.8rem';
        text.style.fontWeight = '500';
        text.style.whiteSpace = 'nowrap';
        text.style.overflow = 'hidden';
        text.style.textOverflow = 'ellipsis';
        text.style.display = 'block';

        wrapper.appendChild(text);
        el.appendChild(wrapper);
      }

      // Vista semanal y diaria
      if (viewType === 'timeGridWeek' || viewType === 'timeGridDay') {
        el.style.backgroundColor = bgcolor || '#0d6efd';
        el.style.color = color || '#fff';
        el.style.padding = '6px 8px';
        el.style.borderRadius = '6px';
        el.style.fontSize = '0.85rem';
        el.style.fontWeight = '500';
        el.style.whiteSpace = 'normal';

        const content = document.createElement('div');
        content.innerText = info.event.title;
        content.style.overflow = 'hidden';
        content.style.textOverflow = 'ellipsis';

        el.appendChild(content);
      }
    },

    dayCellDidMount: (info) => {
      info.el.addEventListener('click', () => {
        this.handleDateClick({ dateStr: info.date.toISOString() });
      });
    },
  };

  filtroEmpleado = '';
  filtroTipo = '';
  filtroEstado = '';

  tiposEspacio: TipoEspacio[] = [];
  estadosReserva: EstadoReserva[] = [];
  eventosOriginales: any[] = [];

  mostrarModal = false;
  reservasDelDia: any[] = [];
  fechaSeleccionada: Date | null = null;

  constructor(
    private reservaService: ReservaService,
    private tipoEspacioService: TipoEspacioService,
    private estadoReservaService: EstadoReservaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const hoy = new Date();
    const primerDia = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
    const ultimoDia = new Date(
      hoy.getFullYear(),
      hoy.getMonth() + 1,
      0,
      23,
      59,
      59
    );

    this.cargarReservas(primerDia, ultimoDia);

    this.tipoEspacioService.getTiposEspacio().subscribe((tipos) => {
      this.tiposEspacio = tipos;
    });

    this.estadoReservaService.getEstadosReserva().subscribe((estados) => {
      this.estadosReserva = estados;
    });
  }

  cargarReservas(desde: Date, hasta: Date): void {
    const inicioStr = desde.toISOString();
    const finStr = hasta.toISOString();

    this.reservaService.getCalendario(inicioStr, finStr).subscribe((data) => {
      this.eventosOriginales = data.map((r) => ({
        title: `#${r.idReserva} - ${r.sala} - ${r.usuario}`,
        start: new Date(r.fechaInicio).toISOString(),
        end: new Date(r.fechaFin).toISOString(),
        allDay: false,
        backgroundColor: r.bgcolor,
        textColor: r.color,
        extendedProps: {
          estado: r.estado,
          sala: r.sala,
          tipo: r.tipo,
          usuario: r.usuario,
          idReserva: r.idReserva,
          bgcolor: r.bgcolor,
          color: r.color,
        },
      }));
      this.aplicarFiltros();
    });
  }

  aplicarFiltros(): void {
    const criterio = this.filtroEmpleado.toLowerCase();
    const tipo = this.filtroTipo;
    const estado = this.filtroEstado;

    const filtrados = this.eventosOriginales.filter((e) => {
      const usuario = e.extendedProps.usuario?.toLowerCase() || '';
      const sala = e.extendedProps.sala?.toLowerCase() || '';
      const reservaId = String(e.extendedProps.idReserva || '');

      const matchNombre =
        !criterio ||
        usuario.includes(criterio) ||
        sala.includes(criterio) ||
        reservaId.includes(criterio);

      const matchTipo = !tipo || e.extendedProps.tipo === tipo;
      const matchEstado = !estado || e.extendedProps.estado === estado;

      return matchNombre && matchTipo && matchEstado;
    });

    this.calendarOptions.events = filtrados;
  }

  asignarClaseEvento(arg: any): string[] {
    const estado = arg.event.extendedProps.estado?.toLowerCase();
    switch (estado) {
      case 'confirmada':
        return ['evento-confirmado'];
      case 'pendiente':
        return ['evento-pendiente'];
      case 'cancelada':
        return ['evento-cancelado'];
      case 'cancelada aut.':
        return ['evento-cancelado-aut'];
      case 'completada':
        return ['evento-completado'];
      default:
        return ['evento-default'];
    }
  }

  getEstadoClase(estado: string): string {
    switch ((estado || '').toLowerCase()) {
      case 'confirmada':
        return 'bg-confirmado';
      case 'pendiente':
        return 'bg-pendiente';
      case 'cancelada':
        return 'bg-cancelado';
      case 'cancelada aut.':
        return 'bg-cancelado-aut';
      case 'completada':
        return 'bg-completado';
      default:
        return 'bg-default';
    }
  }

  // Modal de reservas del día
  handleDateClick(arg: any): void {
    const rawDate = arg.date ?? arg.dateStr;

    const fecha =
      rawDate instanceof Date ? new Date(rawDate) : new Date(String(rawDate));

    if (isNaN(fecha.getTime())) {
      console.error('Fecha seleccionada inválida:', rawDate);
      return;
    }

    fecha.setHours(0, 0, 0, 0);
    this.fechaSeleccionada = fecha;

    this.reservasDelDia = this.eventosOriginales.filter((evento) => {
      const inicio = new Date(evento.start);
      inicio.setHours(0, 0, 0, 0);
      return inicio.getTime() === fecha.getTime();
    });

    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.fechaSeleccionada = null;
    this.reservasDelDia = [];
  }

  crearNuevaReserva(): void {
    if (!this.fechaSeleccionada) return;

    const year = this.fechaSeleccionada.getFullYear();
    const month = String(this.fechaSeleccionada.getMonth() + 1).padStart(
      2,
      '0'
    );
    const day = String(this.fechaSeleccionada.getDate()).padStart(2, '0');
    const fechaLocal = `${year}-${month}-${day}`;

    this.router.navigate(['/misreservas/nueva'], {
      queryParams: {
        returnTo: '/calendario-reservas',
        fecha: fechaLocal,
      },
    });
  }
}
