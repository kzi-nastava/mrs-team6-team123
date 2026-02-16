import { Component, Inject, OnInit, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
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
  changeDetection: ChangeDetectionStrategy.OnPush  // <-- ključni fix
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
    private panicService: PanicService,
    private cdr: ChangeDetectorRef  // <-- dodato
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
    if (!confirmed) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.cdr.markForCheck();

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
        this.cdr.markForCheck();

        setTimeout(() => { this.alertSent = true; this.playAlertSound(); this.cdr.markForCheck(); }, 500);
        setTimeout(() => { this.locationShared = true; this.cdr.markForCheck(); }, 1500);
        setTimeout(() => { this.helpDispatched = true; this.cdr.markForCheck(); }, 2500);
      },
      error: (error) => {
        console.error('❌ Failed to trigger PANIC alert:', error);
        this.errorMessage = error.error || 'Failed to send alert. Please try again or call emergency services directly.';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  playAlertSound() {
    console.log('🚨 ALERT SOUND PLAYING 🚨');
  }

  cancelPanic() {
    const confirmed = confirm(
      'Are you sure you want to cancel this alert? Administrators have already been notified.'
    );
    if (!confirmed) return;

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