import { Component } from '@angular/core';
import { MapComponent } from '../../components/map/map';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { RateRideComponent } from '../../components/rate-ride/rate-ride';

@Component({
  selector: 'app-unregistered-home',
  standalone: true,
  imports: [MapComponent, RateRideComponent],
  templateUrl: './unregistered-home.html',
  styleUrls: ['./unregistered-home.css'],
})
export class UnregisteredHomeComponent {
  constructor(private dialog: MatDialog) {}

  openRateDialog() {
    const dialogRef = this.dialog.open(RateRideComponent, {
      width: '350px',
      height: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Rating submitted:', result);
      }
    });
  }
}
