import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';

export interface PassengerInfoDTO {
  id: number;
  name: string;
  email: string;
  profileImage: string;
}

export interface AdminRideHistoryDTO {
  rideId: number;
  startLocation: string;
  endLocation: string;
  startedAt: string;
  endedAt: string;
  date: string;
  price: number;
  totalDistance: number;
  startLat: number;
  startLng: number;
  endLat: number;
  endLng: number;
  driverId: number;
  driverName: string;
  driverPhoto: string;
  creatorId: number;
  creatorName: string;
  passengers: PassengerInfoDTO[];
  cancelled: boolean;
  cancelledByUserId: number;
  cancelledByName: string;
  cancelledByRole: string;
  panicTriggered: boolean;
  driverRating: number;
  vehicleRating: number;
  rated: boolean;
  inconsistencyReports: string[];
  routeId: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminRideHistoryService {
  private apiUrl = `${environment.apiUrl}/admin/ride-history`;

  constructor(private http: HttpClient) {}

  getAllRideHistory(
    from?: string,
    to?: string,
    sortBy: string = 'date',
    sortOrder: string = 'desc'
  ): Observable<AdminRideHistoryDTO[]> {
    let params = new HttpParams()
      .set('sortBy', sortBy)
      .set('sortOrder', sortOrder);

    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);

    return this.http.get<AdminRideHistoryDTO[]>(this.apiUrl, { params });
  }

  getUserRideHistory(
    userId: number,
    from?: string,
    to?: string,
    sortBy: string = 'date',
    sortOrder: string = 'desc'
  ): Observable<AdminRideHistoryDTO[]> {
    let params = new HttpParams()
      .set('sortBy', sortBy)
      .set('sortOrder', sortOrder);

    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);

    return this.http.get<AdminRideHistoryDTO[]>(
      `${this.apiUrl}/user/${userId}`,
      { params }
    );
  }
  getRideDetails(rideId: number): Observable<AdminRideHistoryDTO> {
    return this.http.get<AdminRideHistoryDTO>(`${this.apiUrl}/${rideId}`);
  }
}