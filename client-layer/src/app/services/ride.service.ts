import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RideService {
  private apiUrl = 'http://localhost:8080/api/ride-history';

  constructor(private http: HttpClient) {}

  /*
  getDriverRideHistory(driverId: number): Observable<Ride[]> {
    return this.http.get<Ride[]>(`${this.apiUrl}/${driverId}/rides`);
  }
  */
}

