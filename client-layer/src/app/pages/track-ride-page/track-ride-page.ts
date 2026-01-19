import { Component, Input } from '@angular/core';
import { TrackRideComponent } from '../../components/track-ride/track-ride/track-ride';
import { MapComponent } from '../../components/map/map';
import { PanicButtonComponent } from '../../components/panic-button/panic-button';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { CancelRideDialogComponent } from '../../components/cancel-ride/cancel-ride';
import { StopRideDialogComponent } from '../../components/stop-ride/stop-ride';

interface Ride {
  id: string;
  from: string;
  to: string;
  nextStop: string;
  timeLeft: string;
  driverName: string;
  startedAt: string;
  price: number;
  passengers: string[];
  status: 'scheduled' | 'active' | 'completed';
  vehicleInfo: string;
  scheduledTime?: string;
}

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
export class TrackRidePageComponent {
  @Input() ride!: Ride;
  @Input() userRole: 'passenger' | 'driver' = 'passenger';

  // Simulirani podaci za demonstraciju
  rideInfo = {
    driverName: 'Marko Marković',
    vehicleInfo: 'Toyota Corolla - NS 123 AB',
    currentLocation: 'Bulevar Cara Lazara 45, Novi Sad',
    timeElapsed: 12
  };

  constructor(private dialog: MatDialog) {
    // Inicijalizuj simulirane podatke ako nisu prosleđeni
    if (!this.ride) {
      this.ride = {
        id: '123',
        from: 'Bulevar oslobođenja 46, Novi Sad',
        to: 'Trg slobode 1, Novi Sad',
        nextStop: 'Zmaj Jovina 5',
        timeLeft: '8 min',
        driverName: 'Marko Marković',
        startedAt: '10:30 AM',
        price: 1200,
        passengers: ['Jovana J.', 'Ana A.', 'Petar P.'],
        status: 'active',
        vehicleInfo: 'Toyota Corolla - NS 123 AB',
        scheduledTime: '2026-01-19 15:30'
      };
    }
  }

  openCancelDialog() {
    const dialogRef = this.dialog.open(CancelRideDialogComponent, {
      width: '650px',
      maxWidth: '90vw',
      data: {
        rideDetails: {
          id: this.ride.id,
          startLocation: this.ride.from,
          destination: this.ride.to,
          scheduledTime: this.ride.scheduledTime || new Date().toISOString(),
          driverName: this.ride.driverName
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
          id: this.ride.id,
          passengerName: this.ride.passengers[0] || 'Ana Anić',
          originalDestination: this.ride.to,
          timeElapsed: 15,
          distanceTraveled: 8,
          originalPrice: this.ride.price
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