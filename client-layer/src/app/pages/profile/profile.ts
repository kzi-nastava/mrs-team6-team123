import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrls: ['./profile.css'],
})
export class ProfileComponent implements OnInit {
  userType: string | null = null;
  isEditMode = false;
  
  profileData = {
    name: 'Dan Brown',
    email: 'danbrown@example.com',
    fullName: 'Dan J. Brown',
    homeAddress: 'Main Str. 12',
    phone: '01010111',
    
    hoursActive: '6h 12m',
    totalRides: 122,
    rating: 4.7,
    vehicleModel: 'Mercedes Benz Sprinter',
    vehicleType: 'Van',
    licensePlate: '112233',
    capacity: 7,
    babiesAllowed: true,
    petsAllowed: true
  };

  editFormData = {
    fullName: '',
    phone: '',
    homeAddress: ''
  };

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.userType = this.authService.userType();
  }

  toggleEditMode() {
    if (!this.isEditMode) {
      // copy current values
      this.editFormData = {
        fullName: this.profileData.fullName,
        phone: this.profileData.phone,
        homeAddress: this.profileData.homeAddress
      };
    }
    this.isEditMode = !this.isEditMode;
  }

  saveProfile() {
    // Update profile data
    this.profileData.fullName = this.editFormData.fullName;
    this.profileData.phone = this.editFormData.phone;
    this.profileData.homeAddress = this.editFormData.homeAddress;
    
    // Exit
    this.isEditMode = false;
  }

  cancelEdit() {
    this.isEditMode = false;
  }

  changePassword() {
    console.log('Change password clicked');
  }
}
