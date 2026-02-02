// ride-actions.ts

import { CommonModule } from '@angular/common';
import { Component, computed, EventEmitter, Input, Output } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { ReportDriverComponent } from '../../report-driver/report-driver';
import { MatDialog } from '@angular/material/dialog';
import { TrackRideResponse } from '../../../models/track-ride.model';
import { StopRideDialogComponent } from '../../stop-ride/stop-ride';
import { PanicDialogComponent } from '../../panic-button/panic-dialog/panic-dialog';
import { RideService } from '../../../services/ride.service';
import { CancelRideDialogComponent } from '../../cancel-ride/cancel-ride';

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
    private dialog: MatDialog,
    public rideService: RideService
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

  private getCurrentUserId(): number {
    return this.auth.getCurrentUserId() || 0;
  }

  private calculateDistanceTraveled(): number {

    return 5.5;
  }

  private getCurrentCoordinates(): string {
    return '45.2550, 19.8450'; 
  }

  onPanic() {
  let userId: number;
  
  if (this.isDriver) {
    userId = this.ride?.driverId || 0;
  } else {
    userId = this.auth.getCurrentUserId() || 0;
  }

  console.log('🚨 PANIC - Using userId:', userId, 'for rideId:', this.ride?.rideId);

  const dialogRef = this.dialog.open(PanicDialogComponent, {
    width: '600px',
    maxWidth: '95vw',
    disableClose: true,
    data: {
      rideInfo: {
        rideId: this.ride?.rideId || 0,
        userId: userId,
        driverName: this.ride?.info?.driver || 'Unknown Driver',
        vehicleInfo: 'Vehicle',
        currentLocation: this.getCurrentCoordinates(),
        timeElapsed: this.ride?.info?.duration || 0
      }
    }
  });

  dialogRef.afterClosed().subscribe(result => {
    if (result?.activated) {
      console.log('🚨 PANIC was activated');
      this.panicClicked.emit();
    }
  });
}

  onStopRide() {
    const dialogRef = this.dialog.open(StopRideDialogComponent, {
      width: '700px',
      maxWidth: '90vw',
      data: {
        currentRide: {
          id: this.ride?.rideId,
          passengerName: this.ride?.info?.passengers?.[0] || 'Passenger',
          originalDestination: this.ride?.info?.to,
          timeElapsed: this.ride?.info?.duration || 0,
          distanceTraveled: this.calculateDistanceTraveled(),
          originalPrice: this.ride?.info?.price || 0,
          currentLocation: this.getCurrentCoordinates()
        }
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.stopped) {
        console.log('✅ Ride stopped at:', result.newDestination);
        console.log('💰 New price:', result.newPrice);
        this.stopRideClicked.emit();
      }
    });
  }

  onReport() {
    const dialogRef = this.dialog.open(ReportDriverComponent, {
      width: '400px',
      maxWidth: '95vw',
      data: this.ride
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('📝 Report submitted:', result);
        this.reportClicked.emit();
      }
    });
  }

  onCancel() {
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

  onGoInactive() {
    const confirmed = confirm('Are you sure you want to go inactive? You will not receive new ride requests.');
    
    if (confirmed) {
      console.log('🔴 Driver going inactive');
      this.goInactiveClicked.emit();
    }
  }


  onFinish() {
    const confirmed = confirm('Are you sure you want to finish this ride?');
    
    if (!confirmed) {
      return;
    }

    this.rideService.finishRide(this.ride.rideId).subscribe({
      next: () => {
        console.log('✅ Ride finished successfully');
        window.alert('Ride finished successfully!');
        this.finishClicked.emit();
      },
      error: (err) => {
        console.error('❌ Error finishing ride:', err);
        const message = typeof err.error === 'string' ? err.error : 'Failed to finish ride';
        window.alert(message);
      }
    });
  }

  get canFinishRide(): boolean {
    console.log(this.ride.info.status);
    return this.ride?.info?.status === 'ARRIVED';
  }
}
