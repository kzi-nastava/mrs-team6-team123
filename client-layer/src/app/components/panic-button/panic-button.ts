import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { PanicDialogComponent } from './panic-dialog/panic-dialog';

interface RideInfo {
  driverName: string;
  vehicleInfo: string;
  currentLocation: string;
  timeElapsed: number;
}

@Component({
  selector: 'app-panic-button',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatDialogModule],
  templateUrl: './panic-button.html',
  styleUrls: ['./panic-button.css'],
})
export class PanicButtonComponent implements OnInit {
  @Input() rideInfo!: RideInfo;
  @Input() buttonStyle: 'floating' | 'inline' = 'floating';

  constructor(private dialog: MatDialog) {}

  ngOnInit() {
    // Ako nema prosleđenih podataka, koristi simulirane
    if (!this.rideInfo) {
      this.rideInfo = {
        driverName: 'Marko Marković',
        vehicleInfo: 'Toyota Corolla - NS 123 AB',
        currentLocation: 'Bulevar Cara Lazara 45, Novi Sad',
        timeElapsed: 12
      };
    }
  }

  openPanicDialog() {
    const dialogRef = this.dialog.open(PanicDialogComponent, {
      width: '600px',
      maxWidth: '90vw',
      disableClose: false,
      data: { rideInfo: this.rideInfo }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.activated) {
        console.log('PANIC was activated');
        // Ovde bi se izvršile dodatne akcije nakon aktivacije panic-a
      }
    });
  }
}