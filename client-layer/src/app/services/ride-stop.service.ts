import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';

export interface StopRideRequest {
  currentLocation: string;
  stoppedAt: string;
}

export interface StopRideResponse {
  rideId: number;
  stoppedLocation: string;
  stoppedAt: string;
  recalculatedPrice: number;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class RideStopService {
  private apiUrl = `${environment.apiUrl}/api/rides`;

  constructor(private http: HttpClient) {}
  stopRide(rideId: number, request: StopRideRequest): Observable<StopRideResponse> {
    return this.http.post<StopRideResponse>(
      `${this.apiUrl}/${rideId}/stop`,
      request
    );
  }
}