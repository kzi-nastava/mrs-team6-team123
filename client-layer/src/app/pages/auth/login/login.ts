import { Component } from '@angular/core';
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
    private authService: AuthService
  ) {}

  login() {
    if (!this.email || !this.password) {
      this.errorMessage = 'Please enter email and password';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login({ email: this.email, password: this.password })
      .subscribe({
        next: (response) => {
          console.log('✅ Login successful, redirecting...');
          
          // Redirect based on role
          if (response.role === 'DRIVER') {
            this.router.navigate(['/registered-home']);
          } else if (response.role === 'ADMIN') {
            this.router.navigate(['/registered-home']);
          } else {
            this.router.navigate(['/registered-home']);
          }
        },
        error: (error) => {
          console.error('❌ Login failed:', error);
          this.errorMessage = error.error || 'Invalid email or password';
          this.loading = false;
        }
      });
  }

  goToReset() {
    this.router.navigate(['/reset-password']);
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }
}