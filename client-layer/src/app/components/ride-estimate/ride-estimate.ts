import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-ride-estimate-modal',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule
  ],
  templateUrl: './ride-estimate.html',
  styleUrls: ['./ride-estimate.css'],
})
export class RideEstimateModalComponent {
  startLocation = '';
  destination = '';
  
  showResult = false;
  estimatedDistance = 0;
  estimatedTime = 0;
  estimatedPrice = 0;

  constructor(
    public dialogRef: MatDialogRef<RideEstimateModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  calculateEstimate() {
    if (!this.startLocation || !this.destination) {
      alert('Please enter both starting location and destination');
      return;
    }

    // Simulacija izračunavanja - kasnije će biti povezano sa serverom
    this.estimatedDistance = Math.floor(Math.random() * 20) + 5; // 5-25 km
    this.estimatedTime = this.estimatedDistance * 2; // Aproksimacija: 2 min po km
    
    // Formula iz specifikacije: cena_po_tipu_vozila + broj_kilometara * 120
    const basePrice = 300; // Početna cena za standardno vozilo
    this.estimatedPrice = basePrice + this.estimatedDistance * 120;
    
    this.showResult = true;
    
    console.log('ESTIMATE DATA', {
      start: this.startLocation,
      destination: this.destination,
      distance: this.estimatedDistance,
      time: this.estimatedTime,
      price: this.estimatedPrice
    });
  }

  onClose(): void {
    this.dialogRef.close();
  }

  onBookRide(): void {
    // Samo za registrovane korisnike
    this.dialogRef.close({
      action: 'book',
      startLocation: this.startLocation,
      destination: this.destination,
      estimatedDistance: this.estimatedDistance,
      estimatedTime: this.estimatedTime,
      estimatedPrice: this.estimatedPrice
    });
  }
}