import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-rate-ride',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rate-ride.html',
  styleUrls: ['./rate-ride.css'],
})
export class RateRideComponent {
  //@Input data = {};

  data = {
    "driver": "John Doe",
    "vehicle": "Peugeout AA123TX"
  }

  driverRating = 0;
  vehicleRating = 0;
  comment = '';

  constructor(public dialogRef: MatDialogRef<RateRideComponent>) {}

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
    this.dialogRef.close({ driver: this.driverRating, vehicle: this.vehicleRating, comment: this.comment });
  }
}
