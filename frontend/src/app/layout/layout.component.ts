import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthStore } from '../core/auth/auth.store';
import { FooterComponent } from '../shared/footer/footer.component';
import { HeaderComponent } from '../shared/header/header.component';

@Component({
  standalone: true,
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss'],
  imports: [RouterModule, HeaderComponent, FooterComponent],
})
export class LayoutComponent {
  constructor(public auth: AuthStore) {}

  logout() {
    this.auth.logout();
  }
}
