import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';

export interface CancelRideRequest {
  userId: number;
  reason: string;
}

export interface CancelRideResponse {
  rideId: number;
  cancelledBy: number;
  reason: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class RideCancelService {
  private apiUrl = `${environment.apiUrl}/rides`;

  constructor(private http: HttpClient) {}

  cancelRide(rideId: number, request: CancelRideRequest): Observable<CancelRideResponse> {
    return this.http.post<CancelRideResponse>(
      `${this.apiUrl}/${rideId}/cancel`,
      request
    );
  }
}