import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ViewRouteComponent } from '../view-route/view-route';

@Component({
  selector: 'app-ride-history-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ride-history-table.html',
  styleUrls: ['./ride-history-table.css'],
})
export class RideHistoryTableComponent {
  @Input() rides: any[] = [];
  @Input() columns: string[] = [];

  constructor(private dialog: MatDialog) {}

  showRoute() {
    this.dialog.open(ViewRouteComponent, {
      width: '400px',
      height: '450px'
    });
  }
}
