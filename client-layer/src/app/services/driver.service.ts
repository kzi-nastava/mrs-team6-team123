import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';
import { DriverRegistrationRequest, DriverResponse } from '../models/driver.model';
import { RideStatus } from '../models/enums';

export interface DriverAssignedRide {
  rideId: number;
  startLocation: string;
  endLocation: string;
  startLatitude: number;
  startLongitude: number;
  endLatitude: number;
  endLongitude: number;
  status: RideStatus;
  scheduledAt?: string;
  estimatedPrice: number;
  passengerNames: string[];
  vehicleType: string;
}

@Injectable({
  providedIn: 'root'
})
export class DriverService {
  private apiUrl = `${environment.apiUrl}/drivers`;

  constructor(private http: HttpClient) {}

  // 2.2.3 Register Driver (Admin only)
  registerDriver(data: DriverRegistrationRequest): Observable<DriverResponse> {
    return this.http.post<DriverResponse>(this.apiUrl, data);
  }

  // Get assigned rides for driver
  getAssignedRides(driverId: number): Observable<DriverAssignedRide[]> {
    return this.http.get<DriverAssignedRide[]>(`${this.apiUrl}/${driverId}/assigned-rides`);
  }

  // Accept a ride
  acceptRide(driverId: number, rideId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${driverId}/rides/${rideId}/accept`, {});
  }

  // Start a ride
  startRide(driverId: number, rideId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${driverId}/rides/${rideId}/start`, {});
  }
}