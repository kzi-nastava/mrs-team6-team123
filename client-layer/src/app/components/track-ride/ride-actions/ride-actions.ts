import { CommonModule } from '@angular/common';
import { Component, computed, EventEmitter, Input, Output } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { ReportDriverComponent } from '../../report-driver/report-driver';
import { MatDialog } from '@angular/material/dialog';
import { TrackRideResponse } from '../../../models/track-ride.model';
import { RideService } from '../../../services/ride.service';

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

  onPanic() {
    this.panicClicked.emit();
  }

  onStopRide() {
    this.stopRideClicked.emit();
  }

  onReport() {
    const dialogRef = this.dialog.open(ReportDriverComponent, {
      width: '350px',
      height: '350px',
      data: this.ride 
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Report submitted:', result);
      }
    });
  }

  onGoInactive() {
    this.goInactiveClicked.emit();
  }

  onFinish() {
    this.rideService.finishRide(this.ride.rideId).subscribe({
      next: () => {
        console.log("Drive ended successfully");
      },
      error: (err) => {
        console.error("Error finishing ride: ", err);
      }
    });
  }
}
