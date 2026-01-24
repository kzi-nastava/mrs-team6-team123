import { Component, EventEmitter, Output, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-change-password-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './change-password-form.html',
  styleUrls: ['./change-password-form.css']
})
export class ChangePasswordFormComponent {
  @Output() close = new EventEmitter<void>();
  @Input() userId: number = 0;

  showCurrentPassword = false;
  showNewPassword = false;
  showConfirmPassword = false;
  
  errorMessage = '';
  isLoading = false;

  passwordData = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  constructor(private userService: UserService) {}

  togglePasswordVisibility(field: string) {
    if (field === 'current') {
      this.showCurrentPassword = !this.showCurrentPassword;
    } else if (field === 'new') {
      this.showNewPassword = !this.showNewPassword;
    } else if (field === 'confirm') {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }

  changePassword() {
    // Validate inputs
    if (!this.passwordData.currentPassword || !this.passwordData.newPassword || !this.passwordData.confirmPassword) {
      this.errorMessage = 'All fields are required';
      return;
    }

    if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
      this.errorMessage = 'New passwords do not match';
      return;
    }

    if (this.passwordData.newPassword.length < 6) {
      this.errorMessage = 'Password must be at least 6 characters';
      return;
    }

    this.errorMessage = '';
    this.isLoading = true;

    // Call backend service
    this.userService.changeUserPassword(this.userId, this.passwordData.currentPassword, this.passwordData.newPassword).subscribe({
      next: () => {
        console.log('Password changed successfully');
        this.isLoading = false;
        this.closeForm();
      },
      error: (err) => {
        console.error('Error changing password:', err);
        this.errorMessage = err.error?.message || 'Failed to change password. Current password may be incorrect.';
        this.isLoading = false;
      }
    });
  }

  closeForm() {
    this.close.emit();
  }

}
