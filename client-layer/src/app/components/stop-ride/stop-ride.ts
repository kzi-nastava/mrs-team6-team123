import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

interface CurrentRide {
  id: string;
  passengerName: string;
  originalDestination: string;
  timeElapsed: number;
  distanceTraveled: number;
  originalPrice: number;
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
  currentLocation = '';
  showConfirmation = false;
  
  baseFare = 300; // Bazna cena
  distanceFare = 0;
  newTotalPrice = 0;

  currentRide: CurrentRide;

  constructor(
    public dialogRef: MatDialogRef<StopRideDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { currentRide: CurrentRide }
  ) {
    this.currentRide = data.currentRide;
  }

  ngOnInit() {
    this.getCurrentLocation();
    this.calculateNewPrice();
  }

  getCurrentLocation() {
    // Simulacija dobijanja trenutne lokacije - kasnije će biti preko GPS/mape
    this.currentLocation = 'Bulevar Cara Lazara 34, Novi Sad';
    console.log('Current location obtained:', this.currentLocation);
  }

  calculateNewPrice() {
    // Formula: cena_po_tipu_vozila + broj_kilometara * 120
    this.distanceFare = this.currentRide.distanceTraveled * 120;
    this.newTotalPrice = this.baseFare + this.distanceFare;
  }

  stopRide() {
    if (!this.currentLocation) {
      alert('Unable to determine current location');
      return;
    }

    console.log('STOP RIDE', {
      rideId: this.currentRide.id,
      newDestination: this.currentLocation,
      stoppedAt: new Date().toISOString(),
      distanceTraveled: this.currentRide.distanceTraveled,
      newPrice: this.newTotalPrice,
      originalPrice: this.currentRide.originalPrice
    });

    // Prikaži potvrdu
    this.showConfirmation = true;

    // Zatvori dialog nakon 2.5 sekundi
    setTimeout(() => {
      this.dialogRef.close({ 
        stopped: true, 
        newDestination: this.currentLocation,
        newPrice: this.newTotalPrice 
      });
    }, 2500);
  }

  continueRide() {
    // Nastavi sa vožnjom do originalnog odredišta
    console.log('Continuing ride to original destination');
    this.dialogRef.close({ stopped: false });
  }

  onClose() {
    if (this.showConfirmation) {
      return; // Ne dozvoli zatvaranje tokom prikaza potvrde
    }
    this.dialogRef.close({ stopped: false });
  }
}