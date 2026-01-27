import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';

export interface PanicAlertRequest {
  rideId: number;
  userId: number;
  currentLocation: string;
}

export interface PanicAlertResponse {
  id: number;
  rideId: number;
  triggeredByUserId: number;
  triggeredByName: string;
  currentLocation: string;
  triggeredAt: string;
  resolved: boolean;
  resolvedAt?: string;
  resolvedByAdminId?: number;
  resolutionNotes?: string;
  driverId: number;
  driverName: string;
  startLocation: string;
  endLocation: string;
}

export interface ResolvePanicRequest {
  adminId: number;
  notes: string;
}

@Injectable({
  providedIn: 'root'
})
export class PanicService {
  private apiUrl = `${environment.apiUrl}/api/panic`;

  constructor(private http: HttpClient) {}

  triggerPanic(request: PanicAlertRequest): Observable<PanicAlertResponse> {
    return this.http.post<PanicAlertResponse>(this.apiUrl, request);
  }

  getAllAlerts(): Observable<PanicAlertResponse[]> {
    return this.http.get<PanicAlertResponse[]>(this.apiUrl);
  }

  getUnresolvedAlerts(): Observable<PanicAlertResponse[]> {
    return this.http.get<PanicAlertResponse[]>(`${this.apiUrl}/unresolved`);
  }

  getAlertById(alertId: number): Observable<PanicAlertResponse> {
    return this.http.get<PanicAlertResponse>(`${this.apiUrl}/${alertId}`);
  }

  resolveAlert(alertId: number, request: ResolvePanicRequest): Observable<PanicAlertResponse> {
    return this.http.put<PanicAlertResponse>(`${this.apiUrl}/${alertId}/resolve`, request);
  }
}