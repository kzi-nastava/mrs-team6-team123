import { Component, ChangeDetectorRef } from '@angular/core';
import { MapComponent } from '../../components/map/map';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { RideEstimateModalComponent } from '../../components/ride-estimate/ride-estimate';

@Component({
  selector: 'app-unregistered-home',
  standalone: true,
  imports: [MapComponent, MatButtonModule],
  templateUrl: './unregistered-home.html',
  styleUrls: ['./unregistered-home.css'],
})
export class UnregisteredHomeComponent {
  estimateRoute?: string[];

  constructor(
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef   // ← dodato
  ) {}

  openEstimateDialog() {
    const dialogRef = this.dialog.open(RideEstimateModalComponent, {
      width: '600px',
      maxWidth: '90vw',
      data: { isRegistered: false }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.action === 'book') {
        alert('Please register or login to book a ride');
        return;
      }

      if (result?.startCoordinates && result?.destinationCoordinates) {
        this.estimateRoute = [
          result.startCoordinates,
          ...(result.intermediateStops ?? []).map((s: any) => s.coordinates),
          result.destinationCoordinates
        ];
        this.cdr.detectChanges();  // ← odmah obavesti Angular o promeni
      }
    });
  }
}

  /*
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
  */
