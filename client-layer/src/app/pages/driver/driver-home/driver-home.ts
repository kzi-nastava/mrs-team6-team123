import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MapComponent } from '../../../components/map/map';
import { DriverRideCardComponent } from '../../../components/driver-ride-card/driver-ride-card';
import { DriverService, DriverAssignedRide } from '../../../services/driver.service';
import { AuthService } from '../../../services/auth.service';
import { MapMode } from '../../../models/enums';
import { Subscription } from 'rxjs';
import { NotificationService } from '../../../services/notification.service';
import { SoundService } from '../../../services/sound.service';

@Component({
  selector: 'app-driver-home',
  standalone: true,
  imports: [CommonModule, MapComponent, DriverRideCardComponent],
  templateUrl: './driver-home.html',
  styleUrls: ['./driver-home.css']
})
export class DriverHomeComponent implements OnInit, OnDestroy {
  rides: DriverAssignedRide[] = [];
  loading = true;
  driverId: number = 0;
  playedOnce = false;
  private notifSub?: Subscription;
  
  // Map defaults to showing a static route for the current ride.
  mapMode: MapMode = 'STATIC_ROUTE';
  
  rideForMap?: {
    startLat?: number;
    startLng?: number;
    endLat?: number;
    endLng?: number;
  };

  // Periodic refresh handle for assigned rides polling.
  private refreshInterval: any;

  constructor(
    private driverService: DriverService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private notificationService: NotificationService,
    private soundService: SoundService
  ) {}

  ngOnInit() {
    // Initialize notification sound for unread items.
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
    this.authService.currentUser$.subscribe(user => {
      if (user?.userId && this.driverId === 0) {
        this.driverId = user.userId;
        this.loadRides();
        this.startRefreshInterval();
      }
    });
  }

  // Start periodic refresh of rides every 10 seconds.
  private startRefreshInterval() {
    if (!this.refreshInterval) {
      this.refreshInterval = setInterval(() => this.loadRides(), 10000);
    }
  }
  
  ngOnDestroy() {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
    this.notifSub?.unsubscribe();
  }

  loadRides() {
    // Skip request until we know the driver id.
    if (!this.driverId) {
      this.loading = false;
      return;
    }

    this.driverService.getAssignedRides(this.driverId).subscribe({
      next: (rides) => {
        this.rides = rides;
        this.updateRideState();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error loading rides:', error);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  private updateRideState() {
    // If a ride is already started, go straight to tracking.
    const startedRide = this.rides.find(r => r.status === 'STARTED');
    if (startedRide) {
      this.router.navigate(['/track-ride-page'], { queryParams: { rideId: startedRide.rideId } });
      return;
    }

    // Rides are ordered by soonest; use the first to populate the map.
    const nextRide = this.rides[0];
    if (nextRide) {
      this.mapMode = 'STATIC_ROUTE';
      this.rideForMap = {
        startLat: nextRide.startLatitude,
        startLng: nextRide.startLongitude,
        endLat: nextRide.endLatitude,
        endLng: nextRide.endLongitude
      };
    } else {
      this.rideForMap = undefined;
    }
  }

  handleStartRide(rideId: number) {
    // Backend validates whether this ride is allowed to start.
    this.driverService.startRide(this.driverId, rideId).subscribe({
      next: () => {
        this.router.navigate(['/track-ride-page'], { queryParams: { rideId: rideId } });
      },
      error: (error) => {
        console.error('Error starting ride:', error);
        alert('Failed to start ride. Please try again.');
      }
    });
  }

  handleSeeRoute(ride: DriverAssignedRide) {
    // Preview a ride route on the map without starting it.
    this.rideForMap = {
      startLat: ride.startLatitude,
      startLng: ride.startLongitude,
      endLat: ride.endLatitude,
      endLng: ride.endLongitude
    };
  }
}
