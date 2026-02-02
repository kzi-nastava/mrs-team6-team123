import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';

export interface PassengerRideHistoryDTO {
  rideId: number;
  startLocation: string;
  endLocation: string;
  startedAt: string;
  endedAt: string;
  date: string;
  price: number;
  startLat: number;
  startLng: number;
  endLat: number;
  endLng: number;
  driverId: number;
  driverName: string;
  driverPhoto: string;
  driverRating: number;
  rideDriverRating: number;
  rideVehicleRating: number;
  rated: boolean;
  inconsistencyReports: string[];
  routeId: number;
}

@Injectable({
  providedIn: 'root'
})
export class PassengerRideHistoryService {
  private apiUrl = `${environment.apiUrl}/passenger`;

  constructor(private http: HttpClient) {}

  getRideHistory(
    passengerId: number,
    from?: string,
    to?: string,
    sortBy: string = 'date',
    sortOrder: string = 'desc'
  ): Observable<PassengerRideHistoryDTO[]> {
    let params = new HttpParams()
      .set('sortBy', sortBy)
      .set('sortOrder', sortOrder);

    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);

    return this.http.get<PassengerRideHistoryDTO[]>(
      `${this.apiUrl}/${passengerId}/rides`,
      { params }
    );
  }

  getRideDetails(passengerId: number, rideId: number): Observable<PassengerRideHistoryDTO> {
    return this.http.get<PassengerRideHistoryDTO>(
      `${this.apiUrl}/${passengerId}/rides/${rideId}`
    );
  }
}