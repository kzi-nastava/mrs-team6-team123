import { CommonModule } from '@angular/common';
import { Component, computed, EventEmitter, Input, Output } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { ReportDriverComponent } from '../../report-driver/report-driver';
import { MatDialog } from '@angular/material/dialog';
import { TrackRideResponse } from '../../../models/track-ride.model';
import { StopRideDialogComponent } from '../../stop-ride/stop-ride';

@Component({
  selector: 'app-ride-actions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ride-actions.html',
  styleUrls: ['./ride-actions.css'],
})
export class RideActionsComponent {
  @Input() ride!: TrackRideResponse;

  constructor(
    public auth: AuthService,
    private dialog: MatDialog
  ) {}

  userType = computed(() => this.auth.getUserType());

  get isAdmin() {
    return this.userType() === 'ADMIN';
  }

  get isDriver() {
    return this.userType() === 'DRIVER';
  }

  get isPassenger() {
    return this.userType() === 'PASSENGER';
  }

  @Output() panicClicked = new EventEmitter<void>();
  @Output() stopRideClicked = new EventEmitter<void>();
  @Output() reportClicked = new EventEmitter<void>();
  @Output() goInactiveClicked = new EventEmitter<void>();
  @Output() finishClicked = new EventEmitter<void>();

  onPanic() {
    this.panicClicked.emit();
  }

  // Helper metode - dodaj ih ako ih nemaš
private calculateDistanceTraveled(): number {
  // U produkciji bi računao na osnovu GPS tracking-a
  // Za sada vraćamo mock vrednost
  return 5.5; // km
}

private getCurrentCoordinates(): string {
  // U produkciji bi koristio GPS
  // Za sada vraćamo mock koordinate (Novi Sad centar)
  return '45.2550, 19.8450';
}

  onStopRide() {
    const dialogRef = this.dialog.open(StopRideDialogComponent, {
        width: '700px',
        maxWidth: '90vw',
        data: {
          currentRide: {
            id: this.ride?.rideId,
            passengerName: this.ride?.info.passengers[0] || 'Passenger',
            originalDestination: this.ride?.info.to,
            timeElapsed: this.ride?.info.duration || 0,
            distanceTraveled: this.calculateDistanceTraveled(), // implementiraj ili koristi mock
            originalPrice: this.ride?.info.price || 0,
            currentLocation: this.getCurrentCoordinates() // implementiraj ili koristi mock
          }
        }
      });
    
      dialogRef.afterClosed().subscribe(result => {
        if (result?.stopped) {
          console.log('Ride stopped at:', result.newDestination);
          console.log('New price:', result.newPrice);
        }
      });
  }

  onReport() {
    const dialogRef = this.dialog.open(ReportDriverComponent, {
      width: '350px',
      height: '350px',
      data: this.ride 
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Report submitted:', result);
      }
    });
  }

  onGoInactive() {
    this.goInactiveClicked.emit();
  }

  onFinish() {
    this.finishClicked.emit();
  }
}
