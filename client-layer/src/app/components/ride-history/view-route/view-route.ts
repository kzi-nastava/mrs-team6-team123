import { Component, Inject } from '@angular/core';
import { MapComponent } from '../../map/map';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DriverRideHistory } from '../../../models/driver-ride-history.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-view-route',
  standalone: true,
  imports: [MapComponent, CommonModule],
  templateUrl: './view-route.html',
  styleUrls: ['./view-route.css'],
})
export class ViewRouteComponent {
  ride!: DriverRideHistory;

  constructor(
    public dialogRef: MatDialogRef<ViewRouteComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { ride: DriverRideHistory }
  ) {}

  ngOnInit(): void {
    this.ride = this.data.ride;
  }
  
  onClose() {
    this.dialogRef.close();
  }
}
