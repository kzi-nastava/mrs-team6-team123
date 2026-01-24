import { Component, Input, OnInit } from '@angular/core';
import { TrackRideComponent } from '../../components/track-ride/track-ride/track-ride';
import { MapComponent } from '../../components/map/map';
import { PanicButtonComponent } from '../../components/panic-button/panic-button';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { CancelRideDialogComponent } from '../../components/cancel-ride/cancel-ride';
import { StopRideDialogComponent } from '../../components/stop-ride/stop-ride';
import { TrackRideResponse } from '../../models/track-ride.model';
import { RideService } from '../../services/ride.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-track-ride-page',
  standalone: true,
  imports: [
    TrackRideComponent, 
    MapComponent, 
    PanicButtonComponent,
    MatButtonModule,
    CommonModule
  ],
  templateUrl: './track-ride-page.html',
  styleUrls: ['./track-ride-page.css'],
})
export class TrackRidePageComponent implements OnInit{
  ride?: TrackRideResponse;
  rideId: number = 1;

  constructor(
    private dialog: MatDialog,
    private rideService: RideService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadRide();
  }

  loadRide(): void {
    this.rideService.trackRide(this.rideId).subscribe({
      next: (data) => {
        this.ride = data;
        console.log('Ride data:', this.ride);
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
          scheduledTime: /*this.ride.scheduledTime ||*/ new Date().toISOString(),
          driverName: this.ride?.info.driver
        }
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.cancelled) {
        console.log('Ride cancelled with reason:', result.reason);
        // Ovde bi se izvršile akcije nakon otkazivanja
      }
    });
  }

  openStopDialog() {
    const dialogRef = this.dialog.open(StopRideDialogComponent, {
      width: '700px',
      maxWidth: '90vw',
      data: {
        currentRide: {
          id: this.ride?.rideId,
          passengerName: this.ride?.info.passengers[0] || 'Ana Anić',
          originalDestination: this.ride?.info.to,
          timeElapsed: 15,
          distanceTraveled: 8,
          originalPrice: this.ride?.info.price
        }
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.stopped) {
        console.log('Ride stopped at:', result.newDestination);
        console.log('New price:', result.newPrice);
        // Ovde bi se izvršile akcije nakon zaustavljanja
      }
    });
  }
}