import { ChangeDetectorRef, Component } from '@angular/core';
import { RideHistoryFilterComponent } from '../../../components/ride-history/ride-history-filter/ride-history-filter';
import { RideHistoryTableComponent } from '../../../components/ride-history/ride-history-table/ride-history-table';
import { DriverRideHistory } from '../../../models/driver-ride-history.model';
import { RideHistoryService } from '../../../services/ride-history.service';

@Component({
  selector: 'app-driver-ride-history',
  standalone: true,
  imports: [RideHistoryFilterComponent, RideHistoryTableComponent],
  templateUrl: './driver-ride-history.html',
  styleUrls: ['./driver-ride-history.css'],
})
export class DriverRideHistoryComponent {
  columns = ['Date', 'From', 'To', 'Started at', 'Ended at', 'Canceled', 'PANIC', 'Price', 'Details'];
  attributes: Record<string, keyof DriverRideHistory> = {
    'Date': 'date',
    'From': 'startLocation',
    'To': 'endLocation',
    'Started at': 'startedAt',
    'Ended at': 'endedAt',
    'Canceled': 'canceledBy',
    'PANIC': 'panicTriggered',
    'Price': 'price'
  }

  rides: DriverRideHistory[] = [];
  driverId = 3;

  constructor(
    private rideHistoryService: RideHistoryService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadRideHistory();
  }

  loadRideHistory(filter?: { fromDate: string, toDate: string }): void {
    this.rideHistoryService
    .getDriverRideHistory(this.driverId, filter?.fromDate, filter?.toDate)
    .subscribe({
      next: (data) => {
        console.log('Rides from backend:', data);
        this.rides = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading ride history', err);
      }
    })
  }
}
