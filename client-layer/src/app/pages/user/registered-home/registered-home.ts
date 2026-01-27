import { Component } from '@angular/core';
import { MapComponent } from '../../../components/map/map';
import { ScheduleRideComponent } from '../../../components/schedule-ride/schedule-ride';

@Component({
  selector: 'app-registered-home',
  imports: [MapComponent, ScheduleRideComponent],
  templateUrl: './registered-home.html',
  styleUrl: './registered-home.css',
})
export class RegisteredHome {
  rideForMap?: {
    startLat?: number;
    startLng?: number;
    endLat?: number;
    endLng?: number;
  };

  onLocationsChanged(locations: {
    startLat?: number;
    startLng?: number;
    endLat?: number;
    endLng?: number;
    stops: Array<{ lat?: number; lng?: number }>;
  }) {
    this.rideForMap = locations;
  }
}
