import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouteCardComponent } from '../../../components/route-card/route-card';
import { FavoriteRoutesService, FavoriteRoute } from '../../../services/favorite-routes.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-user-favorites',
  standalone: true,
  imports: [CommonModule, RouteCardComponent],
  templateUrl: './user-favorites.html',
  styleUrls: ['./user-favorites.css'],
})
export class UserFavoritesComponent implements OnInit {
  favorites: Array<{
    id: number;
    title: string;
    duration: string;
    bookText: string;
    deleteText: string;
    routeId: number;
  }> = [];
  loading = false;
  errorMessage = '';

  constructor(
    private favoriteRoutesService: FavoriteRoutesService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadFavoriteRoutes();
  }

  loadFavoriteRoutes(): void {
    const passengerId = this.authService.getCurrentUserId();
    
    if (!passengerId) {
      this.errorMessage = 'Please log in to view your favorite routes';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.favoriteRoutesService.getFavoriteRoutes(passengerId).subscribe({
      next: (data: FavoriteRoute[]) => {
        this.favorites = data.map(fav => ({
          id: fav.id,
          routeId: fav.routeId,
          title: `${fav.startLocation} → ${fav.endLocation}`,
          duration: 'N/A',
          bookText: 'Book now',
          deleteText: 'Remove'
        }));
        this.loading = false;
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error('Error loading favorite routes:', err);
        this.errorMessage = 'Failed to load favorite routes';
        this.loading = false;
        this.cdr.detectChanges(); 
      }
    });
  }

  removeFavorite(index: number) {
    const passengerId = this.authService.getCurrentUserId();
    if (!passengerId) return;

    const favorite = this.favorites[index];
    
    this.favoriteRoutesService.removeFavoriteRoute(passengerId, favorite.id).subscribe({
      next: () => {
        this.favorites.splice(index, 1);
        this.favorites = [...this.favorites];
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error('Error removing favorite route:', err);
        alert('Failed to remove favorite route');
      }
    });
  }
}
