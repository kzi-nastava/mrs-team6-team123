// stop-ride.ts

import { Component, Inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { RideStopService, StopRideRequest } from '../../services/ride-stop.service';

interface CurrentRide {
  id: number;
  passengerName: string;
  originalDestination: string;
  timeElapsed: number;
  distanceTraveled: number;
  originalPrice: number;
  currentLocation?: string;
}

@Component({
  selector: 'app-stop-ride-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule
  ],
  templateUrl: './stop-ride.html',
  styleUrls: ['./stop-ride.css'],
})
export class StopRideDialogComponent implements OnInit {
  showConfirmation = false;
  isLoading = false;
  errorMessage = '';
  
  currentRide: CurrentRide;
  currentLocation = '';
  
  baseFare = 0;
  distanceFare = 0;
  newTotalPrice = 0;
  
  finalPrice = 0;
  finalLocation = '';

  private readonly PRICE_PER_KM = 120;

  constructor(
    public dialogRef: MatDialogRef<StopRideDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { currentRide: CurrentRide },
    private rideStopService: RideStopService,
    private cdr: ChangeDetectorRef
  ) {
    this.currentRide = data.currentRide;
  }

  ngOnInit() {
    console.log('Stop ride dialog opened for ride:', this.currentRide);
    this.currentLocation = this.currentRide.currentLocation || this.getCurrentLocation();
    this.calculatePriceBreakdown();
  }

  private getCurrentLocation(): string {
    return '45.2550, 19.8450';
  }

  private calculatePriceBreakdown() {
    const originalDistance = this.currentRide.originalPrice / this.PRICE_PER_KM;
    this.baseFare = Math.max(0, this.currentRide.originalPrice - (originalDistance * this.PRICE_PER_KM));
    
    if (this.baseFare <= 0) {
      this.baseFare = 300;
    }
    
    this.distanceFare = Math.round(this.currentRide.distanceTraveled * this.PRICE_PER_KM);
    this.newTotalPrice = this.baseFare + this.distanceFare;
  }

  continueRide() {
    this.dialogRef.close({ stopped: false });
  }

  stopRide() {
    if (!this.currentLocation) {
      this.errorMessage = 'Cannot determine current location. Please try again.';
      this.cdr.detectChanges();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    const request: StopRideRequest = {
      currentLocation: this.currentLocation,
      stoppedAt: new Date().toISOString()
    };

    console.log('🚀 Sending stop ride request:', request);

    this.rideStopService.stopRide(this.currentRide.id, request).subscribe({
      next: (response) => {
        console.log('✅ Ride stopped successfully:', response);
        this.isLoading = false;
        
        this.finalPrice = response.recalculatedPrice;
        this.finalLocation = response.stoppedLocation;
        this.newTotalPrice = response.recalculatedPrice;
        
        this.showConfirmation = true;
        this.cdr.detectChanges();

        setTimeout(() => {
          this.dialogRef.close({ 
            stopped: true, 
            newDestination: response.stoppedLocation,
            newPrice: response.recalculatedPrice,
            response: response
          });
        }, 3000);
      },
      error: (error) => {
        console.error('❌ Failed to stop ride:', error);
        this.isLoading = false;
        
        if (typeof error.error === 'string') {
          this.errorMessage = error.error;
        } else if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Failed to stop ride. Please try again.';
        }
        
        this.cdr.detectChanges();
      }
    });
  }

  refreshLocation() {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          this.currentLocation = `${position.coords.latitude}, ${position.coords.longitude}`;
          console.log('📍 Location updated:', this.currentLocation);
          this.cdr.detectChanges();
        },
        (error) => {
          console.error('❌ Geolocation error:', error);
          this.errorMessage = 'Could not get your location. Using last known position.';
          this.cdr.detectChanges();
        }
      );
    }
  }
}