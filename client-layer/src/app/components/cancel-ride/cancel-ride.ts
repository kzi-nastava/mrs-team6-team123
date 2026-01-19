import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

interface RideDetails {
  id: string;
  startLocation: string;
  destination: string;
  scheduledTime: string;
  driverName: string;
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
  rideDetails: RideDetails;

  constructor(
    public dialogRef: MatDialogRef<CancelRideDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { rideDetails: RideDetails }
  ) {
    this.rideDetails = data.rideDetails;
  }

  ngOnInit() {
    console.log('Cancel ride dialog opened for ride:', this.rideDetails);
  }

  cancelRide() {
    if (!this.cancellationReason.trim()) {
      alert('Please provide a reason for cancellation');
      return;
    }

    // Provera da li je vožnja bar 10 minuta u budućnosti
    const scheduledTime = new Date(this.rideDetails.scheduledTime);
    const now = new Date();
    const timeDiff = (scheduledTime.getTime() - now.getTime()) / 1000 / 60; // u minutima

    if (timeDiff < 10) {
      alert('You can only cancel a ride at least 10 minutes before its scheduled time');
      return;
    }

    console.log('CANCEL RIDE', {
      rideId: this.rideDetails.id,
      reason: this.cancellationReason
    });

    // Simulacija uspešnog otkazivanja
    this.showSuccess = true;

    // Zatvori dialog nakon 2 sekunde
    setTimeout(() => {
      this.dialogRef.close({ cancelled: true, reason: this.cancellationReason });
    }, 2000);
  }

  onClose() {
    this.dialogRef.close({ cancelled: false });
  }
}