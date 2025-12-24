import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

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
}
