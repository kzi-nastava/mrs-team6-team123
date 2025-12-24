import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reset-password.html',
  styleUrls: ['../auth.css'],
})
export class ResetPasswordComponent {
  email = '';
  password = '';
  confirmPassword = '';

  constructor(private router: Router) {}

  reset() {
    if (this.password !== this.confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    console.log('RESET PASSWORD FOR', this.email);
    this.router.navigate(['/login']);
  }
}
