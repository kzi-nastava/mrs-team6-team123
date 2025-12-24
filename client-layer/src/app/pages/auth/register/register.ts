import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

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
    phone: '',
  };

  register() {
    if (this.form.password !== this.form.confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    console.log('REGISTER DATA', this.form);
  }
}
