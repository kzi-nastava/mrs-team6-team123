import { Component, OnInit, ɵsetUnknownPropertyStrictMode, ChangeDetectorRef, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { UserProfile } from '../../models/user';
import { UserService } from '../../services/user.service';
import { ChangePasswordFormComponent } from '../../components/change-password-form/change-password-form';
import { first, finalize } from 'rxjs';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, ChangePasswordFormComponent],
  templateUrl: './profile.html',
  styleUrls: ['./profile.css'],
})
export class ProfileComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  userType: string | null = null;
  isEditMode = false;
  showChangePasswordForm = false;
  isSaving = false;
  saveError = '';
  userProfile: UserProfile | null = null;
  profileData : UserProfile = {
    id: 0,
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

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.authService.currentUser$
      .pipe(first())
      .subscribe({
        next: (authUser) => {
          const userId = authUser?.userId ?? this.getStoredUserId();

          if (!userId) {
            console.warn('No logged-in user found; cannot load profile.');
            return;
          }

          this.loadUser(userId);
        },
        error: (err) => {
          console.error('Error retrieving logged-in user info:', err);
        }
      });

  }

  private getStoredUserId(): number | null {
    const storedUser = localStorage.getItem('current_user');
    if (!storedUser) {
      return null;
    }

    try {
      const parsed = JSON.parse(storedUser) as { userId?: number };
      return parsed.userId ?? null;
    } catch (err) {
      console.warn('Failed to parse stored user info:', err);
      return null;
    }
  }

  loadUser(userId: number) {
    this.userService.getUserProfile(userId).subscribe({
      next: (user) => {
        this.profileData = { ...user };
        
        // Set userType based on role from backend
        if (user.userRole === 'ADMIN') this.userType = 'admin';
        else if (user.userRole === 'DRIVER') this.userType = 'driver';
        else this.userType = 'registered-user';

        // Initialize edit form data

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
    if (this.isSaving) return;
    this.isSaving = true;
    this.saveError = '';

    const userId = this.profileData.id;
    const updatedData = {
      firstName: this.editFormData.firstName,
      lastName: this.editFormData.lastName,
      email: this.profileData.email,
      phone: this.editFormData.phone,
      address: this.editFormData.address
    };
    this.userService.updateUserProfile(userId, updatedData)
      .pipe(finalize(() => {
        this.isSaving = false;
      }))
      .subscribe({
        next: (updatedProfile) => {
          // If user is a driver, changes go for admin approval
          const isDriver = this.userType === 'driver' || updatedProfile?.userRole === 'DRIVER';
          if (isDriver) {
            this.profileData = { ...this.profileData, ...updatedData } as UserProfile;
            window.alert('Changes sent for admin approval.');
          } else {
            this.profileData = updatedProfile;
          }
          this.isEditMode = false;
          this.cdr.detectChanges();
          console.log('Profile save handled; edit mode off. Driver pending flow:', isDriver);
        },
        error: (err) => {
          console.error('Error updating profile', err);
          this.saveError = 'Failed to save changes. Please try again.';
        }
      });
  }

  cancelEdit() {
    this.isEditMode = false;
  }

  changePassword() {
    this.showChangePasswordForm = true;
  }

  triggerFileInput() {
    this.fileInput.nativeElement.click();
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.uploadProfilePhoto(file);
    }
  }

  uploadProfilePhoto(file: File) {
    const formData = new FormData();  
    formData.append('profileImage', file);

    this.userService.uploadProfilePhoto(this.profileData.id, formData).subscribe({
      next: (response) => {
        // Update display with new image
        this.profileData.profileImage = response.profileImage;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error uploading profile photo:', err);
      }
    });
  }
}
