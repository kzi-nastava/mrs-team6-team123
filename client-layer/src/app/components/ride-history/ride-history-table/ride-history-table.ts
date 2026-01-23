import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ViewRouteComponent } from '../view-route/view-route';
import { DriverRideHistory } from '../../../models/driver-ride-history.model';

@Component({
  selector: 'app-ride-history-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ride-history-table.html',
  styleUrls: ['./ride-history-table.css'],
})
export class RideHistoryTableComponent {
  @Input() rides: DriverRideHistory[] = [];
  @Input() columns: string[] = [];
  @Input() attributes: Record<string, keyof DriverRideHistory> = {};

  constructor(private dialog: MatDialog) {}

  showRoute(ride: DriverRideHistory) {
    this.dialog.open(ViewRouteComponent, {
      width: '400px',
      height: '450px',
      data: { ride }
    });
  }

  getRideValue(ride: any, attr: keyof DriverRideHistory): any {
    console.log(this.rides);
    return ride[attr];
  }
}
