import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroment';
import { UserProfile, UpdateUserProfileRequest } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  // 2.3 Get User Profile
  getUserProfile(userId: number): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/${userId}`);
  }

  // 2.3 Update User Profile 
  updateUserProfile(userId: number, data: UpdateUserProfileRequest): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/${userId}`, data);
  }

  changeUserPassword(userId: number, currentPassword: string, newPassword: string): Observable<void> {
    const payload = {
      currentPassword,
      newPassword
    };
    return this.http.post<void>(`${this.apiUrl}/${userId}/change-password`, payload);
  }

  // Upload profile photo
  uploadProfilePhoto(userId: number, formData: FormData): Observable<UserProfile> {
    return this.http.post<UserProfile>(`${this.apiUrl}/${userId}/profile-photo`, formData);
  }

  // List Users (for admin)
  listUsers(excludeUserId: number): Observable<UserProfile[]> {
      const params = new HttpParams().set('excludeUserId', excludeUserId.toString());
      return this.http.get<UserProfile[]>(`${this.apiUrl}/users`, { params });
    }

  // Get User by Email
  getUserByEmail(email: string): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/email/${email}`);
  }

  // Block User
  blockUser(userId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${userId}/block`, {});
  }

  // Unblock User
  unblockUser(userId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${userId}/unblock`, {});
  }
}