import { Component, Input } from '@angular/core';
import { TrackRideComponent } from '../../components/track-ride/track-ride/track-ride';
import { MapComponent } from '../../components/map/map';
import { Ride } from '../../components/track-ride/ride-info/ride-info';

@Component({
  selector: 'app-track-ride-page',
  standalone: true,
  imports: [TrackRideComponent, MapComponent],
  templateUrl: './track-ride-page.html',
  styleUrls: ['./track-ride-page.css'],
})
export class TrackRidePageComponent {
  @Input() ride!: Ride;

  /*
  ride = {
    from: 'Belgrade',
    to: 'Novi Sad',
    nextStop: 'Zemun',
    timeLeft: '15 min',
    driverName: 'Marko Markovic',
    startedAt: '10:30 AM',
    price: 1200,
    passengers: ['Jovana J.', 'Ana A.', 'Petar P.'] // dummy lista putnika
  };
  */
}
