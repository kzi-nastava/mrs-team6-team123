import { Component } from '@angular/core';
import { MapComponent } from '../../components/map/map';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { RideEstimateModalComponent } from '../../components/ride-estimate/ride-estimate';
import { RateRideComponent } from '../../components/rate-ride/rate-ride';

@Component({
  selector: 'app-unregistered-home',
  standalone: true,
  imports: [MapComponent, MatButtonModule],
  templateUrl: './unregistered-home.html',
  styleUrls: ['./unregistered-home.css'],
})
export class UnregisteredHomeComponent {
  constructor(private dialog: MatDialog) {}

  openEstimateDialog() {
    const dialogRef = this.dialog.open(RideEstimateModalComponent, {
      width: '600px',
      maxWidth: '90vw',
      data: { isRegistered: false }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.action === 'book') {
        console.log('User wants to book ride but is not registered');
        alert('Please register or login to book a ride');
      }
    });
  }

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