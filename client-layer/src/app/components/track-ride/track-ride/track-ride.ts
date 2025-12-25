import { Component, Input } from '@angular/core';
import { RideInfoComponent, Ride } from '../ride-info/ride-info';
import { RideActionsComponent } from '../ride-actions/ride-actions';

@Component({
  selector: 'app-track-ride',
  standalone: true,
  imports: [RideInfoComponent, RideActionsComponent],
  templateUrl: './track-ride.html',
  styleUrls: ['./track-ride.css'],
})
export class TrackRideComponent {
  @Input() ride!: Ride;
}
