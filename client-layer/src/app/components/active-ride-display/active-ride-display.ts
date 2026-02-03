import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DriverAssignedRide } from '../../services/driver.service';

@Component({
  selector: 'app-active-ride-display',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './active-ride-display.html',
  styleUrls: ['./active-ride-display.css']
})
export class ActiveRideDisplayComponent {
  @Input() ride?: DriverAssignedRide;
}
