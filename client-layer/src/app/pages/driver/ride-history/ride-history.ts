import { Component } from '@angular/core';
import { RideHistoryFilterComponent } from '../../../components/ride-history/ride-history-filter/ride-history-filter';
import { RideHistoryTableComponent } from '../../../components/ride-history/ride-history-table/ride-history-table';
import { NavbarComponent } from '../../../components/navbar/navbar';

@Component({
  selector: 'app-ride-history',
  standalone: true,
  imports: [RideHistoryFilterComponent, RideHistoryTableComponent, NavbarComponent],
  templateUrl: './ride-history.html',
  styleUrls: ['./ride-history.css'],
})
export class RideHistoryComponent {
  columns = ['Date', 'From', 'To', 'Started at', 'Ended at', 'Canceled', 'PANIC', 'Price'];
}
