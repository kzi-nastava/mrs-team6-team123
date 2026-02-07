import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { RideHistoryFilterComponent } from '../ride-history/ride-history-filter/ride-history-filter';
import { StatCardComponent } from './stat-card/stat-card';

export interface RideStatistics {
  totalRides: number;
  avgRidesPerDay: number;
  ridesData: { date: string; rides: number }[];
  
  totalKmTraveled: number;
  avgKmPerDay: number;
  kmData: { date: string; km: number }[];
  
  totalAmountSpent: number;
  avgAmountPerDay: number;
  amountData: { date: string; spent: number }[];
}

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    RideHistoryFilterComponent,
    StatCardComponent
  ],
  templateUrl: './reports.html',
  styleUrls: ['./reports.css'],
})
export class ReportsComponent implements OnInit {
  statistics: RideStatistics | null = null;
  isLoading = false;
  error: string | null = null;

  constructor(private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadStatistics();
  }

  onFilterApplied(filter: { fromDate: string, toDate: string }): void {
    console.log('Filter applied:', filter);
    // TODO: Use filter values to fetch statistics for the specified date range
    this.loadStatistics();
  }

  onFilterCleared(): void {
    console.log('Filter cleared');
    this.loadStatistics();
  }

  private loadStatistics(): void {
    this.isLoading = true;
    this.error = null;
    this.cdr.detectChanges();
    
    // TODO: Connect to backend API endpoint
    // this.reportService.getRideStatistics(params).subscribe({
    //   next: (data) => {
    //     this.statistics = data;
    //     this.isLoading = false;
    //     this.cdr.detectChanges();
    //   },
    //   error: (err) => {
    //     console.error('Error loading statistics:', err);
    //     this.error = 'Failed to load statistics. Please try again.';
    //     this.isLoading = false;
    //   }
    // });
  }

  formatNumber(num: number): string {
    return new Intl.NumberFormat('en-US').format(num);
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('sr-RS', {
      style: 'currency',
      currency: 'RSD'
    }).format(amount);
  }
}
