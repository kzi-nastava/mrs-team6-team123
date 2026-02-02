import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { PassengerRideHistoryDTO } from '../../../services/passenger-ride-history.service';
import { ViewRouteComponent } from '../view-route/view-route';
import { RateRideComponent } from '../../rate-ride/rate-ride';

@Component({
  selector: 'app-passenger-ride-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './passenger-ride-table.html',
  styleUrls: ['./passenger-ride-table.css'],
})
export class PassengerRideTableComponent {
  @Input() rides: PassengerRideHistoryDTO[] = [];
  @Output() viewDetails = new EventEmitter<PassengerRideHistoryDTO>();

  constructor(private dialog: MatDialog) {}

  showRoute(ride: PassengerRideHistoryDTO): void {
    this.dialog.open(ViewRouteComponent, {
      width: '800px',
      maxWidth: '95vw',
      data: {
        ride: {
          rideId: ride.rideId,
          startLocation: ride.startLocation,
          endLocation: ride.endLocation,
          startLat: ride.startLat,
          startLng: ride.startLng,
          endLat: ride.endLat,
          endLng: ride.endLng,
          date: ride.date,
          startedAt: ride.startedAt,
          endedAt: ride.endedAt,
          price: ride.price,
          passengers: [],
          reports: ride.inconsistencyReports || []
        }
      }
    });
  }

  formatRating(rating: number): string {
    if (!rating || rating === 0) return 'Not rated';
    return `${rating} ⭐`;
  }

  canRateRide(ride: PassengerRideHistoryDTO): boolean {
    if (ride.rideDriverRating && ride.rideDriverRating > 0) {
      return false;
    }

    const endDate = new Date(ride.date);
    const now = new Date();

    const diffMs = now.getTime() - endDate.getTime();
    const diffDays = diffMs / (1000 * 60 * 60 * 24);

    return diffDays <= 3;
  }

  openRateRideDialog(ride: PassengerRideHistoryDTO) {
    this.dialog.open(RateRideComponent, {
      width: '400px',
      disableClose: true,
      data: {
        rideId: ride.rideId
      }
    });
  }
}