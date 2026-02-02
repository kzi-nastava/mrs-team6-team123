// panic-dialog.ts

import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { PanicService, PanicAlertRequest } from '../../../services/panic.service';

interface RideInfo {
  rideId: number;
  userId: number;
  driverName: string;
  vehicleInfo: string;
  currentLocation: string;
  timeElapsed: number;
}

@Component({
  selector: 'app-panic-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  templateUrl: './panic-dialog.html',
  styleUrls: ['./panic-dialog.css'],
})
export class PanicDialogComponent implements OnInit {
  panicActivated = false;
  alertSent = false;
  locationShared = false;
  helpDispatched = false;
  isLoading = false;
  errorMessage = '';
  rideInfo: RideInfo;

  constructor(
    public dialogRef: MatDialogRef<PanicDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { rideInfo: RideInfo },
    private panicService: PanicService
  ) {
    this.rideInfo = data.rideInfo;
  }

  ngOnInit() {
    console.log('Panic dialog opened with ride info:', this.rideInfo);
  }

  activatePanic() {
    const confirmed = confirm(
      'Are you sure you want to activate the PANIC alert? This will immediately notify all administrators.'
    );

    if (!confirmed) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const request: PanicAlertRequest = {
      rideId: this.rideInfo.rideId,
      userId: this.rideInfo.userId,
      currentLocation: this.rideInfo.currentLocation
    };

    this.panicService.triggerPanic(request).subscribe({
      next: (response) => {
        console.log('✅ PANIC alert triggered successfully:', response);
        this.panicActivated = true;
        this.isLoading = false;

        // Simulacija sekvence UI animacija
        setTimeout(() => {
          this.alertSent = true;
          this.playAlertSound();
        }, 500);

        setTimeout(() => {
          this.locationShared = true;
        }, 1500);

        setTimeout(() => {
          this.helpDispatched = true;
        }, 2500);
      },
      error: (error) => {
        console.error('❌ Failed to trigger PANIC alert:', error);
        this.errorMessage = error.error || 'Failed to send alert. Please try again or call emergency services directly.';
        this.isLoading = false;
      }
    });
  }

  playAlertSound() {
    console.log('🚨 ALERT SOUND PLAYING 🚨');
    // Opciono: dodaj pravi zvuk
    // const audio = new Audio('assets/sounds/alert.mp3');
    // audio.play();
  }

  cancelPanic() {
    const confirmed = confirm(
      'Are you sure you want to cancel this alert? Administrators have already been notified.'
    );

    if (!confirmed) {
      return;
    }

    console.log('PANIC CANCELLED - False alarm');
    this.dialogRef.close({ activated: true, cancelled: true });
  }

  onClose() {
    if (this.panicActivated) {
      alert('Cannot close while alert is active. Please cancel the alert first or contact support.');
      return;
    }
    this.dialogRef.close({ activated: false });
  }
}