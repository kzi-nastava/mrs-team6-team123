import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DriverRideHistory } from '../models/driver-ride-history.model';

@Injectable({
  providedIn: 'root'
})
export class RideHistoryService {
  private baseUrl = 'http://localhost:8080/api/ride-history';

  constructor(private http: HttpClient) {}

  getDriverRideHistory(
    driverId: number,
    from?: string,
    to?: string
  ): Observable<DriverRideHistory[]> {
    let params = new HttpParams();

    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    
    return this.http.get<DriverRideHistory[]>(
      `${this.baseUrl}/${driverId}/rides`,
      { params }
    );
  }
}

