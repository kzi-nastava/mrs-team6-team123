import { Component, OnInit, ɵsetUnknownPropertyStrictMode, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { UserProfile } from '../../models/user';
import { UserService } from '../../services/user.service';
import { ChangePasswordFormComponent } from '../../components/change-password-form/change-password-form';
import { first } from 'rxjs';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, ChangePasswordFormComponent],
  templateUrl: './profile.html',
  styleUrls: ['./profile.css'],
})
export class ProfileComponent implements OnInit {
  userType: string | null = null;
  isEditMode = false;
  showChangePasswordForm = false;
  userProfile: UserProfile | null = null;
  profileData : UserProfile = {
    id: 3,
    firstName: '',
    lastName: '',
    email: '',
    address: '',
    phone: '',
    userRole: 'PASSENGER'
  };

  editFormData = {
    firstName: '',
    lastName: '',
    phone: '',
    address: ''
  };

  constructor(private userService: UserService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    const userId = 3;
    this.loadUser(userId);

  }

  loadUser(userId: number) {
    this.userService.getUserProfile(userId).subscribe({
      next: (user) => {
        console.log('Full user object received from backend:', user);
        console.log('User role from backend:', user.userRole);
        console.log('Type of userRole:', typeof user.userRole);
        
        this.profileData = { ...user };
        
        // Set userType based on role from backend
        this.userType = user.userRole === 'DRIVER' ? 'driver' : 'passenger';
        console.log('Calculated userType:', this.userType);
        console.log('Condition check - user.userRole === DRIVER:', user.userRole === 'DRIVER');

        this.editFormData = {
          firstName: user.firstName,
          lastName: user.lastName,
          phone: user.phone,
          address: user.address
        };
        
        // Manually trigger change detection
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading user profile:', err);
      }
    });
  }

  toggleEditMode() {
    if(!this.isEditMode) {
      // Populate form data with current profile data
      this.editFormData = {
        firstName: this.profileData.firstName,
        lastName: this.profileData.lastName,
        phone: this.profileData.phone,
        address: this.profileData.address
      };
    }
    this.isEditMode = !this.isEditMode;

  }

  saveProfile() {
    const userId = this.profileData.id;
    const updatedData = {
      firstName: this.editFormData.firstName,
      lastName: this.editFormData.lastName,
      email: this.profileData.email,
      phone: this.editFormData.phone,
      address: this.editFormData.address
    };
    this.userService.updateUserProfile(userId, updatedData).subscribe({
      next: (updatedProfile) => {
        this.profileData = updatedProfile;
        this.isEditMode = false;
      },
      error: (err) => {
        console.error('Error updating profile', err);
      }
    });
  }

  cancelEdit() {
    this.isEditMode = false;
  }

  changePassword() {
    this.showChangePasswordForm = true;
  }
}
