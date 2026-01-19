import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

interface RideInfo {
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
  rideInfo: RideInfo;

  constructor(
    public dialogRef: MatDialogRef<PanicDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { rideInfo: RideInfo }
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

    this.panicActivated = true;

    // Simulacija sekvence slanja alertova
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

    console.log('PANIC ACTIVATED', {
      timestamp: new Date().toISOString(),
      location: this.rideInfo.currentLocation,
      driver: this.rideInfo.driverName,
      vehicle: this.rideInfo.vehicleInfo
    });

    this.sendPanicNotification();
  }

  sendPanicNotification() {
    console.log('Sending PANIC notification to administrators...');
    console.log('Sound and visual alert triggered on admin dashboard');
  }

  playAlertSound() {
    console.log('🚨 ALERT SOUND PLAYING 🚨');
  }

  cancelPanic() {
    const confirmed = confirm(
      'Are you sure you want to cancel this alert? Administrators have already been notified.'
    );

    if (!confirmed) {
      return;
    }

    console.log('PANIC CANCELLED - False alarm');
    this.dialogRef.close({ activated: false, cancelled: true });
  }

  onClose() {
    if (this.panicActivated) {
      alert('Cannot close while alert is active. Please cancel the alert first or contact support.');
      return;
    }
    this.dialogRef.close({ activated: false });
  }
}