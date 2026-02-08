import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActiveRidesCardComponent } from '../../../components/active-rides-card/active-rides-card';
import { CommonModule } from '@angular/common';
import { RideMonitoringResponse } from '../../../models/ride-monitoring.model';
import { RideMonitoringService } from '../../../services/ride-monitoring.service';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { SoundService } from '../../../services/sound.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [ActiveRidesCardComponent, CommonModule, FormsModule],
  templateUrl: './admin-home.html',
  styleUrls: ['./admin-home.css'],
})
export class AdminHomeComponent implements OnInit {
  rides: RideMonitoringResponse[] = [];
  filteredRides: RideMonitoringResponse[] = [];
  searchTerm: string = '';
  playedOnce = false;
  private notifSub?: Subscription;

  constructor(
    private rideMonitoringService: RideMonitoringService,
    private cdr: ChangeDetectorRef,
    private authService: AuthService,
    private notificationService: NotificationService,
    private soundService: SoundService
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    if (userId) {
      this.notificationService.loadUnread(userId);
    }
    this.playedOnce = false;
    this.notifSub = this.notificationService.getUnread().subscribe(list => {
      if (list.length > 0 && !this.playedOnce) {
        this.soundService.play();
        this.playedOnce = true;
        console.log('Playing notification sound');
      }
    });
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
