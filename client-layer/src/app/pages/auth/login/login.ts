import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['../auth.css'],
})
export class LoginComponent {
  email = '';
  password = '';
  loading = false;
  errorMessage = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef 
  ) {
    if (this.authService.isAuthenticated()) {
      this.redirectBasedOnRole();
    }
  }

  login() {
    this.errorMessage = '';

    if (!this.email.trim()) {
      this.errorMessage = 'Please enter your email';
      return;
    }

    if (!this.password) {
      this.errorMessage = 'Please enter your password';
      return;
    }

    this.loading = true;

    this.authService.login({ email: this.email.trim(), password: this.password })
      .subscribe({
        next: (response) => {
          console.log('✅ Login SUCCESS:', response);
          this.loading = false;
          this.cdr.detectChanges();
          this.redirectBasedOnRole();
        },
        error: (error) => {
          console.log('❌ Login ERROR:', error.status, error.error);
          
          this.loading = false;
          
          if (error.status === 401) {
            this.errorMessage = 'Invalid email or password';
          } else if (error.status === 400) {
            this.errorMessage = typeof error.error === 'string' ? error.error : 'Invalid request';
          } else if (error.status === 0) {
            this.errorMessage = 'Cannot connect to server';
          } else if (typeof error.error === 'string') {
            this.errorMessage = error.error;
          } else {
            this.errorMessage = 'Login failed. Please try again.';
          }
          
          this.cdr.detectChanges();
        }
      });
  }

  private redirectBasedOnRole() {
    const role = this.authService.userRole();
    
    switch (role) {
      case 'DRIVER':
        this.router.navigate(['/driver/driver-ride-history']);
        break;
      case 'ADMIN':
        this.router.navigate(['/admin-home']);
        break;
      case 'PASSENGER':
      default:
        this.router.navigate(['/registered-home']);
        break;
    }
  }

  goToReset() {
    this.router.navigate(['/forgot-password']);
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }
}