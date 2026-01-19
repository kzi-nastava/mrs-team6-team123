import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../enviroment';
import {
  RideEstimationRequest,
  RideEstimationResponse,
  RideOrderRequest,
  RideResponse,
  RideTrackingResponse,
  RideRatingRequest,
  RideRatingResponse,
  CancelRideRequest,
  CancelRideResponse,
  StopRideRequest,
  StopRideResponse,
  DriverRideHistory,
  AdminRideHistory
} from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class RideService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  // 2.1.2 Ride Estimation
  estimateRide(request: RideEstimationRequest): Observable<RideEstimationResponse> {
    return this.http.post<RideEstimationResponse>(
      `${this.apiUrl}/ride-estimation`,
      request
    );
  }

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

  // Track Ride
  trackRide(rideId: number): Observable<RideTrackingResponse> {
    return this.http.get<RideTrackingResponse>(
      `${this.apiUrl}/rides/${rideId}/tracking`
    );
  }

  // 2.6.1 Start Ride
  startRide(rideId: number): Observable<RideResponse> {
    return this.http.post<RideResponse>(
      `${this.apiUrl}/rides/${rideId}/start`,
      {}
    );
  }

  // 2.7 Finish Ride
  finishRide(rideId: number): Observable<RideResponse> {
    return this.http.post<RideResponse>(
      `${this.apiUrl}/rides/${rideId}/finish`,
      {}
    );
  }

  // 2.8 Rate Ride
  rateRide(rating: RideRatingRequest): Observable<RideRatingResponse> {
    return this.http.post<RideRatingResponse>(
      `${this.apiUrl}/rides/${rating.rideId}/rate`,
      rating
    );
  }

  // 2.5 Cancel Ride
  cancelRide(rideId: number, request: CancelRideRequest): Observable<CancelRideResponse> {
    return this.http.post<CancelRideResponse>(
      `${this.apiUrl}/rides/${rideId}/cancel`,
      request
    );
  }

  // 2.6.5 Stop Ride Early
  stopRide(rideId: number, request: StopRideRequest): Observable<StopRideResponse> {
    return this.http.post<StopRideResponse>(
      `${this.apiUrl}/rides/${rideId}/stop`,
      request
    );
  }

  // 2.9.2 Driver Ride History
  getDriverRideHistory(
    driverId: number,
    from?: string,
    to?: string
  ): Observable<DriverRideHistory[]> {
    let params = new HttpParams();
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);

    return this.http.get<DriverRideHistory[]>(
      `${this.apiUrl}/ride-history/${driverId}/rides`,
      { params }
    );
  }

  // 2.9.3 Admin - Driver Ride History
  getAdminDriverHistory(
    driverId: number,
    from?: string,
    to?: string,
    sortBy?: string
  ): Observable<AdminRideHistory[]> {
    let params = new HttpParams();
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    if (sortBy) params = params.set('sortBy', sortBy);

    return this.http.get<AdminRideHistory[]>(
      `${this.apiUrl}/admin/ride-history/driver/${driverId}`,
      { params }
    );
  }

  // 2.9.3 Admin - Passenger Ride History
  getAdminPassengerHistory(
    passengerId: number,
    from?: string,
    to?: string,
    sortBy?: string
  ): Observable<AdminRideHistory[]> {
    let params = new HttpParams();
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    if (sortBy) params = params.set('sortBy', sortBy);

    return this.http.get<AdminRideHistory[]>(
      `${this.apiUrl}/admin/ride-history/passenger/${passengerId}`,
      { params }
    );
  }

  // Admin - Get Ride Details
  getRideDetails(rideId: number): Observable<AdminRideHistory> {
    return this.http.get<AdminRideHistory>(
      `${this.apiUrl}/admin/ride-history/${rideId}/details`
    );
  }
}