import { Component, Input } from '@angular/core';
import { ActiveRidesCardComponent } from '../../components/active-rides-card/active-rides-card';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [ActiveRidesCardComponent, CommonModule],
  templateUrl: './admin-home.html',
  styleUrls: ['./admin-home.css'],
})
export class AdminHomeComponent {
  @Input() rides: any[] = [];
}
