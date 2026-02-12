import { Component, Input, Output, EventEmitter, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { PassengerRideHistoryDTO } from '../../../services/passenger-ride-history.service';
import { FavoriteRoutesService, FavoriteRoute } from '../../../services/favorite-routes.service';
import { ViewRouteComponent } from '../view-route/view-route';
import { RateRideComponent } from '../../rate-ride/rate-ride';

@Component({
  selector: 'app-passenger-ride-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './passenger-ride-table.html',
  styleUrls: ['./passenger-ride-table.css'],
})
export class PassengerRideTableComponent implements OnInit {
  @Input() set rides(value: PassengerRideHistoryDTO[]) {
    this._rides = value;
    // Reload favorites whenever rides change to ensure they're in sync
    if (this.currentUserId && value && value.length > 0) {
      this.loadFavoriteRoutes();
    }
  }
  get rides(): PassengerRideHistoryDTO[] {
    return this._rides;
  }
  private _rides: PassengerRideHistoryDTO[] = [];

  @Output() viewDetails = new EventEmitter<PassengerRideHistoryDTO>();

  favoriteRouteIds = new Set<number>();
  favoriteIdMap = new Map<number, number>(); // Map of routeId -> favoriteId (for deletion)
  currentUserId: number | null = null;

  constructor(
    private dialog: MatDialog,
    private favoriteRoutesService: FavoriteRoutesService,
    private cdr: ChangeDetectorRef
  ) {
    const userStr = localStorage.getItem('current_user');
    if (userStr) {
      const user = JSON.parse(userStr);
      this.currentUserId = user.userId;
    }
  }

  ngOnInit(): void {
    if (this.currentUserId) {
      this.loadFavoriteRoutes();
    }
  }

  loadFavoriteRoutes(): void {
    if (!this.currentUserId) return;
    
    this.favoriteRoutesService.getFavoriteRoutes(this.currentUserId).subscribe({
      next: (routes: FavoriteRoute[]) => {
        console.log('Loaded favorite routes:', routes);
        const routeIds = routes.map(r => r.routeId);
        console.log('Extracted routeIds:', routeIds);
        this.favoriteRouteIds = new Set(routeIds);
        
        // Build map of routeId -> favoriteId for deletion
        this.favoriteIdMap.clear();
        routes.forEach(route => {
          this.favoriteIdMap.set(route.routeId, route.id);
        });
        
        console.log('Favorite routeIds Set:', Array.from(this.favoriteRouteIds));
        console.log('Favorite ID Map:', Array.from(this.favoriteIdMap.entries()));
        console.log('Current rides routeIds:', this._rides.map(r => r.routeId));
        // Force Angular to re-render the template with updated favorite status
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading favorite routes:', err);
      }
    });
  }

  isFavorited(routeId: number): boolean {
    const result = this.favoriteRouteIds.has(routeId);
    console.log('Checking if routeId', routeId, 'is favorited:', result);
    return result;
  }

  toggleFavorite(ride: PassengerRideHistoryDTO, event: Event): void {
    event.stopPropagation();
    
    if (!this.currentUserId) {
      alert('Please log in to save favorites');
      return;
    }

    console.log('Toggling favorite for ride:', ride.rideId, 'routeId:', ride.routeId, 'currently favorited:', this.isFavorited(ride.routeId));

    if (this.isFavorited(ride.routeId)) {
      // Remove from favorites
      const favoriteId = this.favoriteIdMap.get(ride.routeId);
      if (favoriteId) {
        this.favoriteRoutesService.removeFavoriteRoute(this.currentUserId, favoriteId).subscribe({
          next: () => {
            console.log('Favorite removed:', favoriteId);
            this.loadFavoriteRoutes(); // Reload to update UI
          },
          error: (err) => {
            alert('Failed to remove from favorites');
            console.error('Error removing favorite:', err);
          }
        });
      }
    } else {
      // Add to favorites
      this.favoriteRoutesService.addFavoriteRoute(this.currentUserId, ride.routeId).subscribe({
        next: () => {
          console.log('Favorite added for routeId:', ride.routeId);
          this.loadFavoriteRoutes(); // Reload to ensure consistency and trigger change detection
        },
        error: (err) => {
          if (err.status === 409) {
            alert('Route already added to favorites');
          } else {
            alert('Failed to add to favorites');
          }
          console.error('Error adding favorite:', err);
        }
      });
    }
  }

  showRoute(ride: PassengerRideHistoryDTO): void {
    this.dialog.open(ViewRouteComponent, {
      width: '800px',
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
          passengers: [],
          reports: ride.inconsistencyReports || []
        }
      }
    });
  }

  formatRating(rating: number): string {
    if (!rating || rating === 0) return 'Not rated';
    return `${rating} ⭐`;
  }

  canRateRide(ride: PassengerRideHistoryDTO): boolean {
    if (ride.rideDriverRating && ride.rideDriverRating > 0) {
      return false;
    }

    const endDate = new Date(ride.date);
    const now = new Date();

    const diffMs = now.getTime() - endDate.getTime();
    const diffDays = diffMs / (1000 * 60 * 60 * 24);

    return diffDays <= 3;
  }

  openRateRideDialog(ride: PassengerRideHistoryDTO) {
    this.dialog.open(RateRideComponent, {
      width: '400px',
      disableClose: true,
      data: {
        rideId: ride.rideId
      }
    });
  }
}