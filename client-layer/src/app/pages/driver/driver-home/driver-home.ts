import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MapComponent } from '../../../components/map/map';
import { DriverRideCardComponent } from '../../../components/driver-ride-card/driver-ride-card';
import { DriverService, DriverAssignedRide } from '../../../services/driver.service';
import { AuthService } from '../../../services/auth.service';
import { RideService } from '../../../services/ride.service';
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
  acceptedRide?: DriverAssignedRide;
  pendingRides: DriverAssignedRide[] = [];
  loading = true;
  driverId: number = 0;
  playedOnce = false;
  private notifSub?: Subscription;
  
  mapMode: MapMode = 'STATIC_ROUTE';
  
  rideForMap?: {
    startLat?: number;
    startLng?: number;
    endLat?: number;
    endLng?: number;
  };

  private refreshInterval: any;

  constructor(
    private driverService: DriverService,
    private authService: AuthService,
    private rideService: RideService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private notificationService: NotificationService,
    private soundService: SoundService
  ) {}

  ngOnInit() {
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

  // Start periodic refresh of rides every 10 seconds
  private startRefreshInterval() {
    if (!this.refreshInterval) {
      this.refreshInterval = setInterval(() => this.loadRides(), 10000);
    }
  }
  
  ngOnDestroy() {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }

  loadRides() {
    if (!this.driverId) {
      this.loading = false;
      return;
    }

    this.driverService.getAssignedRides(this.driverId).subscribe({
      next: (rides) => {
        this.rides = rides;
        this.organizeRides();
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

  organizeRides() {
    //  STARTED ride - redirect to track page
    const startedRide = this.rides.find(r => r.status === 'STARTED');
    if (startedRide) {
      this.router.navigate(['/track-ride-page'], { queryParams: { rideId: startedRide.rideId } });
      return;
    }

    const accepted = this.rides.find(r => r.status === 'ACCEPTED');
    this.acceptedRide = accepted;
    
    // If there's an accepted ride, show route on map
    if (this.acceptedRide) {
      this.mapMode = 'STATIC_ROUTE';
      this.rideForMap = {
        startLat: this.acceptedRide.startLatitude,
        startLng: this.acceptedRide.startLongitude,
        endLat: this.acceptedRide.endLatitude,
        endLng: this.acceptedRide.endLongitude
      };
    }
    
    this.pendingRides = this.rides.filter(r => r.status === 'CREATED');
  }

  handleAcceptRide(rideId: number) {
    this.driverService.acceptRide(this.driverId, rideId).subscribe({
      next: () => {
        console.log('Ride accepted successfully');
        this.loadRides(); // Reload to update the view
      },
      error: (error) => {
        console.error('Error accepting ride:', error);
        alert('Failed to accept ride. Please try again.');
      }
    });
  }

  handleStartRide(rideId: number) {
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
    this.rideForMap = {
      startLat: ride.startLatitude,
      startLng: ride.startLongitude,
      endLat: ride.endLatitude,
      endLng: ride.endLongitude
    };
  }
}
