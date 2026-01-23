import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';
import { AdminRideHistory } from '../models/admin-ride-history.model';

@Injectable({
  providedIn: 'root'
})
export class RideService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

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