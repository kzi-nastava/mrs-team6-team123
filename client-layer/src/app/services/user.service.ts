import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';
import { UserProfile, UpdateUserProfileRequest } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/api/users`;

  constructor(private http: HttpClient) {}

  // 2.3 Get User Profile
  getUserProfile(userId: number): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/${userId}`);
  }

  // 2.3 Update User Profile
  updateUserProfile(userId: number, data: UpdateUserProfileRequest): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/${userId}`, data);
  }

  // List Users (optional, for admin)
  listUsers(nameFilter?: string): Observable<UserProfile[]> {
    let params = new HttpParams();
    if (nameFilter) {
      params = params.set('name', nameFilter);
    }
    return this.http.get<UserProfile[]>(this.apiUrl, { params });
  }
}