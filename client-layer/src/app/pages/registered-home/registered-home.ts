import { Component } from '@angular/core';
import { MapComponent } from '../../components/map/map';
import { ScheduleRideComponent } from '../../components/schedule-ride/schedule-ride';

@Component({
  selector: 'app-registered-home',
  imports: [MapComponent, ScheduleRideComponent],
  templateUrl: './registered-home.html',
  styleUrl: './registered-home.css',
})
export class RegisteredHome {
}
