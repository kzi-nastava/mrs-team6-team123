import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-activate-account',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="auth-container">
      <h1>Account Activation</h1>
      
      <div *ngIf="loading" class="status-message">
        <p>⏳ Activating your account...</p>
      </div>

      <div *ngIf="success" class="success-message">
        <p>✅ {{ message }}</p>
        <p>Redirecting to login...</p>
      </div>

      <div *ngIf="error" class="error-message">
        <p>❌ {{ message }}</p>
        <button (click)="goToLogin()">Go to Login</button>
      </div>
    </div>
  `,
  styleUrls: ['../auth.css']
})
export class ActivateAccountComponent implements OnInit {
  loading = true;
  success = false;
  error = false;
  message = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.error = true;
      this.loading = false;
      this.message = 'Invalid activation link';
      return;
    }

    console.log('🔑 Activating account with token:', token);

    this.authService.activateAccount(token).subscribe({
      next: (response) => {
        console.log('✅ Activation successful:', response);
        this.success = true;
        this.loading = false;
        this.message = response;

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: (error) => {
        console.error('❌ Activation failed:', error);
        this.error = true;
        this.loading = false;
        this.message = error.error || 'Activation failed. The link may have expired.';
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}