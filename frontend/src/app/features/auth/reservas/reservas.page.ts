import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-reservas',
  templateUrl: './reservas.page.html',
  styleUrls: ['./reservas.page.scss'],
  imports: [CommonModule],
})
export class ReservasPage implements OnInit {
  reservas: any[] = [];

  ngOnInit(): void {
    // 🧪 Datos mock por ahora
    this.reservas = [
      {
        id: 1,
        cliente: 'Juan Pérez',
        fecha: '2025-04-10',
        hora: '18:00',
        estado: 'Confirmada',
      },
      {
        id: 2,
        cliente: 'Ana López',
        fecha: '2025-04-11',
        hora: '19:30',
        estado: 'Pendiente',
      },
    ];
  }
}
