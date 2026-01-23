import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';
import { 
  RideEstimationRequest, 
  RideEstimationResponse 
} from '../models/ride-estimation.model';
import { CancelRideRequest, CancelRideResponse, RideResponse, StopRideRequest, StopRideResponse } from '../models/ride.model';

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

  // 2.6.1 Start Ride
  startRide(rideId: number): Observable<RideResponse> {
    return this.http.post<RideResponse>(
      `${this.apiUrl}/rides/${rideId}/start`,
      {}
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
}