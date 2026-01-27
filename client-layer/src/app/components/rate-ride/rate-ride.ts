import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, Inject, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { RideService } from '../../services/ride.service';
import { RateRideRequest, RateRideResponse } from '../../models/rate-ride.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-rate-ride',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rate-ride.html',
  styleUrls: ['./rate-ride.css'],
})
export class RateRideComponent {

  driverRating = 0;
  vehicleRating = 0;
  comment = '';

  constructor(
   public dialogRef: MatDialogRef<RateRideComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { rideId: number },
    private rideService: RideService,
    private cdr: ChangeDetectorRef,
    private auth: AuthService
  ) {}

  ride!: RateRideRequest;

  ngOnInit() {
    this.rideService.getRideForRating(this.data.rideId).subscribe({
      next: ride => {
        this.ride = ride;
        console.log(this.ride);
        this.cdr.detectChanges();
      },
      error: err => console.error(err)
    });
  }

  setDriverRating(value: number) {
    this.driverRating = value;
  }

  setVehicleRating(value: number) {
    this.vehicleRating = value;
  }

  isDriverStarFilled(star: number): boolean {
    return star <= this.driverRating;
  }

  isVehicleStarFilled(star: number): boolean {
    return star <= this.vehicleRating;
  }

  close() {
    this.dialogRef.close();
  }

  submit() {
    this.rateRide();
    this.dialogRef.close();
  }

  rateRide() {
    var authorId = this.auth.getCurrentUserId();

    if (!authorId) {
      console.error('User not logged in');
      window.alert('You must be logged in!');
      return;
    }

    const response: RateRideResponse = {
      rideId: this.ride.rideId,
      driverId: this.ride.driverId,
      vehicleId: this.ride.vehicleId,
      comment: this.comment,
      driverRating: this.driverRating,
      vehicleRating: this.vehicleRating,
      authorId: authorId
    };

    this.rideService.rateRide(response).subscribe({
      next: () => {
        console.log('Ride rated successfully');
      },
      error: (err) => {
        console.error('Error rating ride:', err);
      }
    });
  }
}
