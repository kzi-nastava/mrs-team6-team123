import { Component, EventEmitter, Output, Input, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { finalize } from 'rxjs';

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
  successMessage = '';
  isLoading = false;

  passwordData = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  constructor(private userService: UserService, private cdr: ChangeDetectorRef) {}

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
    this.successMessage = '';
    this.isLoading = true;

    // Call backend service
    this.userService.changeUserPassword(this.userId, this.passwordData.currentPassword, this.passwordData.newPassword)
      .pipe(finalize(() => { this.isLoading = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: () => {
          console.log('Password changed successfully');
          this.errorMessage = '';
          this.successMessage = 'Password changed successfully';
          // show confirmation briefly, then close
          setTimeout(() => this.closeForm(), 1200);
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error changing password:', err);
          this.errorMessage = err.error?.message || 'Failed to change password. Current password may be incorrect.';
          this.successMessage = '';
          this.cdr.detectChanges();
        }
      });
  }

  closeForm() {
    this.close.emit();
  }

}
