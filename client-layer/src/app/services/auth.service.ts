import { Injectable, signal, computed, effect } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../enviroment';
import { 
  LoginRequest, 
  LoginResponse, 
  RegistrationRequest, 
  RegistrationResponse,
  ForgotPasswordRequest,
  ResetPasswordRequest
} from '../models/auth.model';
import { UserRole } from '../models/enums';
import { UserService } from './user.service';
import { U } from '@angular/cdk/keycodes';

export interface CurrentUser {
  userId: number;
  email: string;
  role: UserRole;
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<LoginResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private _isLoggedIn = signal<boolean>(false);
  private _currentUser = signal<CurrentUser | null>(null);
  private _userRole = signal<UserRole>('GUEST');

  public isLoggedIn = computed(() => this._isLoggedIn());
  public currentUser = computed(() => this._currentUser());
  public userRole = computed(() => this._userRole());
  
  public userType = computed(() => this._userRole());

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadUserFromStorage();
  }

  private loadUserFromStorage() {
    const token = localStorage.getItem('auth_token');
    const storedUser = localStorage.getItem('current_user');

    if (token && storedUser) {
      try {
        const user: CurrentUser = JSON.parse(storedUser);
        this.setCurrentUser(user);
        console.log('✅ User loaded from storage:', user.email, user.role);
      } catch (e) {
        console.error('Failed to parse stored user:', e);
        this.clearAuth();
      }
    } else {
      this._isLoggedIn.set(false);
      this._userRole.set('GUEST');
    }
  }

  private setCurrentUser(user: CurrentUser) {
    this._currentUser.set(user);
    this._isLoggedIn.set(true);
    this._userRole.set(user.role);
    this.currentUserSubject.next(user);
  }

  private clearAuth() {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('current_user');
    this._currentUser.set(null);
    this._isLoggedIn.set(false);
    this._userRole.set('GUEST');
    this.currentUserSubject.next(null);
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          const user: CurrentUser = {
            userId: response.userId,
            email: response.email,
            role: response.role,
            token: response.token
          };
          localStorage.setItem('auth_token', response.token);
          localStorage.setItem('current_user', JSON.stringify(user));
          this.setCurrentUser(user);

          console.log('✅ Login successful:', user.email, user.role);
        })
      );
  }

  logout() {
    const user = this._currentUser();
    
    if (user && user.role === 'DRIVER') {
      this.http.post(`${this.apiUrl}/logout`, { userId: user.userId })
        .subscribe({
          next: () => console.log('✅ Driver logged out on server'),
          error: (err) => console.error('Logout error:', err)
        });
    }

    this.clearAuth();
    
    this.router.navigate(['/login']);
    
    console.log('👋 User logged out');
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

  getCurrentUserId(): number | null {
    return this._currentUser()?.userId || null;
  }

  getCurrentUserId(): number | null {
    const stored = localStorage.getItem('current_user');
    console.log(stored);
    if (!stored) return null;

    const user = JSON.parse(stored);
    console.log(user);
    return user.userId ?? null;
  }
 
  getUserType() {
    return this.currentUser()?.role;
    //return 'DRIVER';
  getCurrentUserEmail(): string | null {
    return this._currentUser()?.email || null;
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }


  isAuthenticated(): boolean {
    return this._isLoggedIn();
  }

  isAdmin(): boolean {
    return this._userRole() === 'ADMIN';
  }


  isDriver(): boolean {
    return this._userRole() === 'DRIVER';
  }

  isPassenger(): boolean {
    return this._userRole() === 'PASSENGER';
  }

  getUserType(): UserRole {
    return this._userRole();
  }
}