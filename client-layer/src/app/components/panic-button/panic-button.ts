import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { PanicDialogComponent } from '../panic-button/panic-dialog/panic-dialog';

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
export class PanicButtonComponent {

  @Input() rideInfo!: RideInfo;
  @Input() buttonStyle: 'floating' | 'inline' = 'floating';

  constructor(private dialog: MatDialog) {}

  openPanicDialog() {
    this.dialog.open(PanicDialogComponent, {
      width: '600px',
      disableClose: true,
      data: {
        rideInfo: this.rideInfo
      }
    });
  }
}
