import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';
import { PendingProfileChange } from '../models/pending-profile-change.model';

@Injectable({
  providedIn: 'root'
})
export class ProfileChangeService {
  private apiUrl = `${environment.apiUrl}/admin/profile-changes`;

  constructor(private http: HttpClient) {}

  getPendingChange(changeId: number): Observable<PendingProfileChange> {
    return this.http.get<PendingProfileChange>(`${this.apiUrl}/${changeId}`);
  }

  approveChange(changeId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${changeId}/approve`, {});
  }

  declineChange(changeId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${changeId}/decline`, {});
  }
}
