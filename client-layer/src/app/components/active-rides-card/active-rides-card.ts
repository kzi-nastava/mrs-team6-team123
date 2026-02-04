import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { RideMonitoringResponse } from '../../models/ride-monitoring.model';


@Component({
  selector: 'app-active-rides-card',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './active-rides-card.html',
  styleUrls: ['./active-rides-card.css'],
})
export class ActiveRidesCardComponent {
  @Input() data!: RideMonitoringResponse;

  constructor(private router: Router) {}

  goToTrackRide() {
    this.router.navigate(
      ['/track-ride-page'],
      {
        queryParams: { rideId: this.data.rideId }
      }
    );
  }
}
