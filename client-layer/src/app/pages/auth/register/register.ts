import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.html',
  styleUrls: ['../auth.css'],
})
export class RegisterComponent {
  form = {
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: '',
    address: '',
    phoneNumber: '',
  };

  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  register() {
    // Validation
    if (!this.form.email || !this.form.password || !this.form.firstName || 
        !this.form.lastName || !this.form.address || !this.form.phoneNumber) {
      this.errorMessage = 'All fields are required';
      return;
    }

    if (this.form.password !== this.form.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      return;
    }

    if (this.form.password.length < 6) {
      this.errorMessage = 'Password must be at least 6 characters';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.register(this.form).subscribe({
      next: (response) => {
        console.log('✅ Registration successful:', response);
        this.successMessage = response.message;
        this.loading = false;

        // Redirect to login after 3 seconds
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: (error) => {
        console.error('❌ Registration failed:', error);
        this.errorMessage = error.error || 'Registration failed. Please try again.';
        this.loading = false;
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}