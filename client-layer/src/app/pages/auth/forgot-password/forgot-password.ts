import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../enviroment';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="auth-container">
      <h1>Forgot Password</h1>
      
      <p class="info-text" *ngIf="!successMessage">
        Enter your email address and we'll send you a link to reset your password.
      </p>

      <form (ngSubmit)="sendResetLink()" *ngIf="!successMessage">
        <input
          type="email"
          placeholder="Email"
          [(ngModel)]="email"
          name="email"
          required
          [disabled]="loading"
        />

        <div *ngIf="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <button type="submit" [disabled]="loading || !email">
          {{ loading ? 'Sending...' : 'Send Reset Link' }}
        </button>
      </form>

      <div *ngIf="successMessage" class="success-message">
        <p>✅ {{ successMessage }}</p>
        <p class="redirect-info">Check your email for the reset link.</p>
        <button (click)="goToLogin()">Back to Login</button>
      </div>

      <div class="auth-links" *ngIf="!successMessage">
        <button class="link-btn" (click)="goToLogin()" [disabled]="loading">
          Back to Login
        </button>
      </div>
    </div>
  `,
  styleUrls: ['../auth.css']
})
export class ForgotPasswordComponent {
  email = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private router: Router,
    private http: HttpClient
  ) {}

  sendResetLink() {
    if (!this.email) {
      this.errorMessage = 'Please enter your email';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.http.post(`${environment.apiUrl}/auth/forgot-password`, 
      { email: this.email },
      { responseType: 'text' }
    ).subscribe({
      next: (response) => {
        console.log('✅ Reset link sent:', response);
        this.successMessage = response;
        this.loading = false;
      },
      error: (error) => {
        console.error('❌ Failed to send reset link:', error);
        this.errorMessage = error.error || 'Failed to send reset link. Please try again.';
        this.loading = false;
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}