import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RideHistoryFilterComponent } from '../../../components/ride-history/ride-history-filter/ride-history-filter';
import { PassengerRideTableComponent } from '../../../components/ride-history/passenger-ride-table/passenger-ride-table';
import { PassengerRideHistoryService, PassengerRideHistoryDTO } from '../../../services/passenger-ride-history.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-passenger-ride-history',
  standalone: true,
  imports: [
    CommonModule,
    RideHistoryFilterComponent,
    PassengerRideTableComponent 
  ],
  templateUrl: './passenger-ride-history.html',
  styleUrls: ['./passenger-ride-history.css'],
})
export class PassengerRideHistoryComponent implements OnInit {
  rides: PassengerRideHistoryDTO[] = [];
  loading = false;
  errorMessage = '';

  currentSortBy = 'date';
  currentSortOrder = 'desc';

  constructor(
    private rideHistoryService: PassengerRideHistoryService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadRideHistory();
  }

  loadRideHistory(filter?: { fromDate: string; toDate: string }): void {
    const passengerId = this.authService.getCurrentUserId();

    if (!passengerId) {
      this.errorMessage = 'Please log in to view your ride history';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.rideHistoryService
      .getRideHistory(
        passengerId,
        filter?.fromDate,
        filter?.toDate,
        this.currentSortBy,
        this.currentSortOrder
      )
      .subscribe({
        next: (data) => {
          console.log('✅ Passenger rides loaded:', data.length);
          this.rides = data;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('❌ Error loading ride history:', err);
          this.errorMessage = 'Failed to load ride history';
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
  }
}