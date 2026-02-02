import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { RideHistoryFilterComponent } from '../../../components/ride-history/ride-history-filter/ride-history-filter';
import { ViewRouteComponent } from '../../../components/ride-history/view-route/view-route';
import { AdminRideHistoryService, AdminRideHistoryDTO } from '../../../services/admin-ride-history.service';

@Component({
  selector: 'app-admin-ride-history',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RideHistoryFilterComponent
  ],
  templateUrl: './admin-ride-history.html',
  styleUrls: ['./admin-ride-history.css'],
})
export class AdminRideHistoryComponent implements OnInit {
  rides: AdminRideHistoryDTO[] = [];
  loading = false;
  errorMessage = '';

  userIdFilter: number | null = null;
  showAllRides = true;

  fromDate = '';
  toDate = '';

  currentSortBy = 'date';
  currentSortOrder: 'asc' | 'desc' = 'desc';

  constructor(
    private adminRideHistoryService: AdminRideHistoryService,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadAllRides();
  }

  loadAllRides(): void {
    this.loading = true;
    this.errorMessage = '';
    this.showAllRides = true;
    this.userIdFilter = null;

    this.adminRideHistoryService
      .getAllRideHistory(this.fromDate, this.toDate, this.currentSortBy, this.currentSortOrder)
      .subscribe({
        next: (data) => {
          console.log('✅ All rides loaded:', data.length);
          this.rides = data;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('❌ Error loading rides:', err);
          this.errorMessage = 'Failed to load ride history';
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
  }

  loadUserRides(userId: number): void {
    if (!userId) {
      this.errorMessage = 'Please enter a valid user ID';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.showAllRides = false;
    this.userIdFilter = userId;

    this.adminRideHistoryService
      .getUserRideHistory(userId, this.fromDate, this.toDate, this.currentSortBy, this.currentSortOrder)
      .subscribe({
        next: (data) => {
          console.log('✅ User rides loaded:', data.length);
          this.rides = data;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('❌ Error loading user rides:', err);
          this.errorMessage = err.error || 'Failed to load user ride history';
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
  }

  onFilterApplied(filter: { fromDate: string; toDate: string }): void {
    this.fromDate = filter.fromDate;
    this.toDate = filter.toDate;
    
    if (this.showAllRides) {
      this.loadAllRides();
    } else if (this.userIdFilter) {
      this.loadUserRides(this.userIdFilter);
    }
  }

  onFilterCleared(): void {
    this.fromDate = '';
    this.toDate = '';
    this.loadAllRides();
  }

  onSort(column: string): void {
    const sortMapping: Record<string, string> = {
      'Date': 'date',
      'From': 'startLocation',
      'To': 'endLocation',
      'Price': 'price',
      'Distance': 'totalDistance',
      'Driver': 'driverName'
    };

    const sortBy = sortMapping[column] || 'date';

    if (this.currentSortBy === sortBy) {
      this.currentSortOrder = this.currentSortOrder === 'asc' ? 'desc' : 'asc';
    } else {
      this.currentSortBy = sortBy;
      this.currentSortOrder = 'desc';
    }

    if (this.showAllRides) {
      this.loadAllRides();
    } else if (this.userIdFilter) {
      this.loadUserRides(this.userIdFilter);
    }
  }

  showRideDetails(ride: AdminRideHistoryDTO): void {
    this.dialog.open(ViewRouteComponent, {
      width: '900px',
      maxWidth: '95vw',
      data: {
        ride: {
          rideId: ride.rideId,
          startLocation: ride.startLocation,
          endLocation: ride.endLocation,
          startLat: ride.startLat,
          startLng: ride.startLng,
          endLat: ride.endLat,
          endLng: ride.endLng,
          date: ride.date,
          startedAt: ride.startedAt,
          endedAt: ride.endedAt,
          price: ride.price,
          passengers: ride.passengers.map(p => p.name),
          reports: ride.inconsistencyReports || [],
          driverName: ride.driverName,
          cancelled: ride.cancelled,
          cancelledByName: ride.cancelledByName,
          panicTriggered: ride.panicTriggered
        },
        isAdmin: true
      }
    });
  }

  getSortIcon(column: string): string {
    const sortMapping: Record<string, string> = {
      'Date': 'date',
      'From': 'startLocation',
      'To': 'endLocation',
      'Price': 'price',
      'Distance': 'totalDistance',
      'Driver': 'driverName'
    };

    const sortBy = sortMapping[column];
    if (this.currentSortBy !== sortBy) return '';
    return this.currentSortOrder === 'asc' ? '↑' : '↓';
  }
}