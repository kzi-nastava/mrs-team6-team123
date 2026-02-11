import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';

export interface FavoriteRoute {
  id: number;
  routeId: number;
  startLocation: string;
  endLocation: string;
  startLatitude: number;
  startLongitude: number;
  endLatitude: number;
  endLongitude: number;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class FavoriteRoutesService {
  private apiUrl = `${environment.apiUrl}/passenger`;

  constructor(private http: HttpClient) {}

  getFavoriteRoutes(passengerId: number): Observable<FavoriteRoute[]> {
    return this.http.get<FavoriteRoute[]>(`${this.apiUrl}/${passengerId}/favorite-routes`);
  }

  addFavoriteRoute(passengerId: number, routeId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${passengerId}/favorite-routes?routeId=${routeId}`, {});
  }

  removeFavoriteRoute(passengerId: number, favoriteRouteId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${passengerId}/favorite-routes/${favoriteRouteId}`);
  }
}
