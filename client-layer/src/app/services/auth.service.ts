import { Injectable, signal } from '@angular/core';
import { environment } from '../../enviroment';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { ForgotPasswordRequest, LoginRequest, LoginResponse, RegistrationRequest, RegistrationResponse, ResetPasswordRequest } from '../models/auth.model';
import { HttpClient } from '@angular/common/http';
import { UserProfile } from '../models/user.model';
import { UserRole } from '../models/enums';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<LoginResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  currentUser = signal<UserProfile | null>(null);
  userType = signal<UserRole>('GUEST');

  constructor(private http: HttpClient) {
    const storedUser = localStorage.getItem('current_user');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  login(user: UserProfile) {
    this.currentUser.set(user);
    this.userType.set(user.role);
  }
  
  /*
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          // Store token and user info
          localStorage.setItem('auth_token', response.token);
          localStorage.setItem('current_user', JSON.stringify(response));
          this.currentUserSubject.next(response);
        })
      );
  }
  */

  isLoggedIn(): boolean {
    return !!localStorage.getItem('auth_token');
  }

  logout() {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('current_user');
    this.currentUser.set(null);
    this.userType.set('GUEST');
  }

  register(data: RegistrationRequest): Observable<RegistrationResponse> {
    return this.http.post<RegistrationResponse>(`${this.apiUrl}/register`, data);
  }

  activateAccount(token: string): Observable<string> {
    return this.http.get(`${this.apiUrl}/activate`, {
      params: { token },
      responseType: 'text'
    });
  }

  forgotPassword(data: ForgotPasswordRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/forgot-password`, data, {
      responseType: 'text'
    });
  }

  resetPassword(data: ResetPasswordRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/reset-password`, data, {
      responseType: 'text'
    });
  }

  getCurrentUser() {
    return this.currentUser();
  }

  getUserType() {
    //return this.userRole();
    return 'ADMIN';
  }
}
