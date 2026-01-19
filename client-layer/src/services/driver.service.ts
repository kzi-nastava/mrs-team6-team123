import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../enviroment';
import {
  DriverRegistrationRequest,
  DriverResponse,
  ReportDriverRequest,
  ReportDriverResponse
} from '../models/api.models';

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

  // 2.6.2 Report Driver Inconsistency
  reportDriver(request: ReportDriverRequest): Observable<ReportDriverResponse> {
    return this.http.post<ReportDriverResponse>(
      `${this.apiUrl}/${request.driverId}/report`,
      request
    );
  }
}