// cancel-ride.ts

import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { RideCancelService, CancelRideRequest } from '../../services/ride-cancel.service';

interface RideDetails {
  id: number;
  startLocation: string;
  destination: string;
  scheduledTime: string;
  driverName: string;
  userId: number;
}

@Component({
  selector: 'app-cancel-ride-dialog',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule
  ],
  templateUrl: './cancel-ride.html',
  styleUrls: ['./cancel-ride.css'],
})
export class CancelRideDialogComponent implements OnInit {
  cancellationReason = '';
  showSuccess = false;
  isLoading = false;
  errorMessage = '';
  rideDetails: RideDetails;

  constructor(
    public dialogRef: MatDialogRef<CancelRideDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { rideDetails: RideDetails },
    private rideCancelService: RideCancelService
  ) {
    this.rideDetails = data.rideDetails;
  }

  ngOnInit() {
    console.log('Cancel ride dialog opened for ride:', this.rideDetails);
  }

  cancelRide() {
    if (!this.cancellationReason.trim()) {
      this.errorMessage = 'Please provide a reason for cancellation';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const request: CancelRideRequest = {
      userId: this.rideDetails.userId,
      reason: this.cancellationReason.trim()
    };

    this.rideCancelService.cancelRide(this.rideDetails.id, request).subscribe({
      next: (response) => {
        console.log('✅ Ride cancelled successfully:', response);
        this.isLoading = false;
        this.showSuccess = true;

        setTimeout(() => {
          this.dialogRef.close({ 
            cancelled: true, 
            reason: this.cancellationReason,
            response: response 
          });
        }, 2000);
      },
      error: (error) => {
        console.error('❌ Failed to cancel ride:', error);
        this.isLoading = false;
     
        this.errorMessage = error.error || 'Failed to cancel ride. Please try again.';
      }
    });
  }

  onClose() {
    if (this.isLoading) {
      return;
    }
    this.dialogRef.close({ cancelled: false });
  }
}