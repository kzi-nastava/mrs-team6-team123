import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';
import { RideOrderRequest, RideResponse } from '../models/ride.model';

@Injectable({
  providedIn: 'root'
})
export class OrderRideService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  // 2.4.1 Order Ride
  orderRide(request: RideOrderRequest, immediate: boolean = true): Observable<RideResponse> {
    const params = new HttpParams().set('immediate', immediate.toString());
    return this.http.post<RideResponse>(
      `${this.apiUrl}/rides`,
      request,
      { params }
    );
  }

  // 2.4.3 Order Ride from Favorite
  orderRideFromFavorite(favoriteRouteId: number, immediate: boolean = true): Observable<RideResponse> {
    const params = new HttpParams().set('immediate', immediate.toString());
    return this.http.post<RideResponse>(
      `${this.apiUrl}/rides/favorites/${favoriteRouteId}`,
      {},
      { params }
    );
  }
}