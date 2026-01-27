import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { TrackRideComponent } from '../../components/track-ride/track-ride/track-ride';
import { MapComponent } from '../../components/map/map';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { CancelRideDialogComponent } from '../../components/cancel-ride/cancel-ride';
import { StopRideDialogComponent } from '../../components/stop-ride/stop-ride';
import { TrackRideResponse } from '../../models/track-ride.model';
import { RideService } from '../../services/ride.service';

@Component({
  selector: 'app-track-ride-page',
  standalone: true,
  imports: [
    TrackRideComponent, 
    MapComponent, 
    MatButtonModule,
    CommonModule
  ],
  templateUrl: './track-ride-page.html',
  styleUrls: ['./track-ride-page.css'],
})
export class TrackRidePageComponent implements OnInit{
  ride!: TrackRideResponse;
  rideId: number = 2;

  constructor(
    private dialog: MatDialog,
    private rideService: RideService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadRide();
  }

  loadRide(): void {
    this.rideService.trackRide(this.rideId).subscribe({
      next: (data) => {
        this.ride = data;
        console.log('Ride data:', this.ride);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching ride:', err);
      }
    });
  }

openCancelDialog() {
  const dialogRef = this.dialog.open(CancelRideDialogComponent, {
    width: '650px',
    maxWidth: '90vw',
    data: {
      rideDetails: {
        id: this.ride?.rideId,
        startLocation: this.ride?.info.from,
        destination: this.ride?.info.to,
        scheduledTime: new Date().toISOString(),
        driverName: this.ride?.info.driver,
        userId: this.getCurrentUserId()
      }
    }
  });

  dialogRef.afterClosed().subscribe(result => {
    if (result?.cancelled) {
      console.log('Ride cancelled with reason:', result.reason);
    }
  });
}

private getCurrentUserId(): number {
  const storedUser = localStorage.getItem('current_user');
  if (storedUser) {
    const user = JSON.parse(storedUser);
    return user.userId || 0;
  }
  return 0;
}

openStopDialog() {
  const dialogRef = this.dialog.open(StopRideDialogComponent, {
    width: '700px',
    maxWidth: '90vw',
    data: {
      currentRide: {
        id: this.ride?.rideId,
        passengerName: this.ride?.info.passengers[0] || 'Passenger',
        originalDestination: this.ride?.info.to,
        timeElapsed: this.ride?.info.duration || 0,
        distanceTraveled: this.calculateDistanceTraveled(),
        originalPrice: this.ride?.info.price || 0,
        currentLocation: this.getCurrentCoordinates() 
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

private calculateDistanceTraveled(): number {
  return 5.5;
}

private getCurrentCoordinates(): string {
  return '45.2550, 19.8450';
}
}