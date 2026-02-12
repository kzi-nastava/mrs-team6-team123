import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { RideHistoryFilterComponent } from '../ride-history/ride-history-filter/ride-history-filter';
import { StatCardComponent } from './stat-card/stat-card';
import { ReportsService, Statistics, UserBasicInfo } from '../../services/reports.service';
import { AuthService } from '../../services/auth.service';

export interface DateDataPoint {
  date: string;
  [key: string]: any;
}

export interface RideStatistics {
  totalRides: number;
  avgRidesPerDay: number;
  ridesData: DateDataPoint[];
  
  totalKmTraveled: number;
  avgKmPerDay: number;
  kmData: DateDataPoint[];
  
  totalAmountSpent: number;
  avgAmountPerDay: number;
  amountData: DateDataPoint[];
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
    MatSelectModule,
    MatFormFieldModule,
    FormsModule,
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
  
  amountLabel = 'Amount Spent';
  
  // For admin user selector
  users: UserBasicInfo[] = [];
  selectedUserId: number | null = null;
  selectedUserType: string | null = null;
  
  // For date filtering
  fromDate: string | null = null;
  toDate: string | null = null;

  constructor(
    public authService: AuthService,
    private cdr: ChangeDetectorRef,
    private reportsService: ReportsService
  ) {}

  ngOnInit(): void {
    this.updateAmountLabel();
    
    // Load users list if admin
    const currentUser = this.authService.currentUser();
    if (currentUser && currentUser.role === 'ADMIN') {
      this.loadUsers();
    }
    
    this.loadStatistics();
  }

  onFilterApplied(filter: { fromDate: string, toDate: string }): void {
    console.log('Filter applied:', filter);
    this.fromDate = filter.fromDate;
    this.toDate = filter.toDate;
    this.loadStatistics();
  }

  onFilterCleared(): void {
    console.log('Filter cleared');
    this.fromDate = null;
    this.toDate = null;
    this.loadStatistics();
  }

  onUserSelected(userId: number | null): void {
    this.selectedUserId = userId;
    
    // Find the selected user and get their role
    if (userId !== null) {
      const selectedUser = this.users.find(u => u.id === userId);
      this.selectedUserType = selectedUser ? selectedUser.userRole : null;
      
      // Update amount label based on user type
      if (selectedUser) {
        if (selectedUser.userRole === 'DRIVER') {
          this.amountLabel = 'Money Made';
        } else {
          this.amountLabel = 'Amount Spent';
        }
      }
    } else {
      // When "All Rides" is selected, admin sees "Money Made"
      this.selectedUserType = null;
      this.updateAmountLabel();
    }
    
    this.cdr.detectChanges();
    this.loadStatistics();
  }

  private updateAmountLabel(): void {
    const currentUser = this.authService.currentUser();
    if (currentUser && (currentUser.role === 'DRIVER' || currentUser.role === 'ADMIN')) {
      this.amountLabel = 'Money Made';
    } else {
      this.amountLabel = 'Amount Spent';
    }
  }

  private loadUsers(): void {
    const currentUser = this.authService.currentUser();
    if (!currentUser) return;
    
    this.reportsService.getAllActiveUsers(currentUser.userId).subscribe({
      next: (users) => {
        this.users = users;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading users:', err);
      }
    });
  }

  private loadStatistics(): void {
    const currentUser = this.authService.currentUser();
    if (!currentUser) {
      this.error = 'User not authenticated';
      return;
    }

    this.isLoading = true;
    this.error = null;
    this.cdr.detectChanges();
    
    // Pass filteredUserId and filteredUserType if admin selected a specific user (not "All Rides")
    const filteredUserId = currentUser.role === 'ADMIN' && this.selectedUserId !== null ? this.selectedUserId : undefined;
    const filteredUserType = currentUser.role === 'ADMIN' && this.selectedUserType !== null ? this.selectedUserType : undefined;
    
    this.reportsService.getStatistics(
      currentUser.userId, 
      currentUser.role, 
      filteredUserId, 
      filteredUserType,
      this.fromDate || undefined,
      this.toDate || undefined
    ).subscribe({
      next: (data) => {
        // Transform the data to match the expected format for stat-card
        this.statistics = {
          ...data,
          ridesData: data.ridesData.map(dp => ({ date: dp.date, rides: dp.value })),
          kmData: data.kmData.map(dp => ({ date: dp.date, km: dp.value })),
          amountData: data.amountData.map(dp => ({ date: dp.date, spent: dp.value }))
        };
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading statistics:', err);
        this.error = 'Failed to load statistics. Please try again.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
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

  getSelectedUser(): UserBasicInfo | undefined {
    if (this.selectedUserId === null) {
      return undefined;
    }
    return this.users.find(u => u.id === this.selectedUserId);
  }
}
