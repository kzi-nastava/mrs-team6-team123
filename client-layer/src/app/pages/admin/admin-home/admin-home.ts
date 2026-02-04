import { ChangeDetectorRef, Component } from '@angular/core';
import { ActiveRidesCardComponent } from '../../../components/active-rides-card/active-rides-card';
import { CommonModule } from '@angular/common';
import { RideMonitoringResponse } from '../../../models/ride-monitoring.model';
import { RideMonitoringService } from '../../../services/ride-monitoring.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [ActiveRidesCardComponent, CommonModule, FormsModule],
  templateUrl: './admin-home.html',
  styleUrls: ['./admin-home.css'],
})
export class AdminHomeComponent {
  rides: RideMonitoringResponse[] = [];
  filteredRides: RideMonitoringResponse[] = [];
  searchTerm: string = '';

  constructor(
    private rideMonitoringService: RideMonitoringService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadRides();
  }

  loadRides(): void {
    this.rideMonitoringService.getActiveRides().subscribe({
      next: (data) => {
        console.log('Rides from backend:', data);
        this.rides = data;
        this.filteredRides = this.rides;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading active rides', err);
      }
    });
  }

  filterRides() {
    const term = this.searchTerm.toLowerCase().trim();

    this.filteredRides = this.rides.filter(ride =>
      ride.driverName?.toLowerCase().includes(term)
    );
  }
}
