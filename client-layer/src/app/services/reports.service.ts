import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';

export interface RideDataPoint {
  date: string;
  value: number;
}

export interface Statistics {
  totalRides: number;
  avgRidesPerDay: number;
  ridesData: RideDataPoint[];

  totalKmTraveled: number;
  avgKmPerDay: number;
  kmData: RideDataPoint[];

  totalAmountSpent: number;
  avgAmountPerDay: number;
  amountData: RideDataPoint[];
}

export interface UserBasicInfo {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  userRole: string; // "PASSENGER" or "DRIVER"
}

@Injectable({
  providedIn: 'root'
})
export class ReportsService {
  private apiUrl = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  getStatistics(userId: number, userType: string, filteredUserId?: number, filteredUserType?: string, fromDate?: string, toDate?: string): Observable<Statistics> {
    let params = new HttpParams()
      .set('userId', userId.toString())
      .set('userType', userType);
    
    if (filteredUserId) {
      params = params.set('filteredUserId', filteredUserId.toString());
    }
    
    if (filteredUserType) {
      params = params.set('filteredUserType', filteredUserType);
    }
    
    if (fromDate) {
      params = params.set('fromDate', fromDate);
    }
    
    if (toDate) {
      params = params.set('toDate', toDate);
    }
    
    return this.http.get<Statistics>(`${this.apiUrl}/statistics`, { params });
  }

  getAllPassengers(): Observable<UserBasicInfo[]> {
    return this.http.get<UserBasicInfo[]>(`${this.apiUrl}/passengers`);
  }

  getAllActiveUsers(excludeUserId: number): Observable<UserBasicInfo[]> {
    const params = new HttpParams().set('excludeUserId', excludeUserId.toString());
    return this.http.get<UserBasicInfo[]>(`${this.apiUrl}/users`, { params });
  }
}
