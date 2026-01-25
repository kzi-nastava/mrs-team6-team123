import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { DriverService } from '../../../services/driver.service';
import { DriverRegistrationRequest } from '../../../models/driver.model';
import { VehicleType } from '../../../models/enums';

@Component({
  selector: 'app-driver-registration',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './driver-registration.html',
  styleUrls: ['./driver-registration.css'],
})
export class DriverRegistration {
  driverForm = {
    email: '',
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    vehicleModel: '',
    vehicleType: 'STANDARD',
    licensePlate: '',
    seats: 4,
    babyFriendly: false,
    petFriendly: false,
  };

  vehicleTypes = [
    { value: 'STANDARD', label: 'Standard' },
    { value: 'LUX', label: 'Luxury' },
    { value: 'VAN', label: 'Van' },
  ];

  constructor(private driverService: DriverService) {}

  submit(form?: NgForm) {
    // Map frontend form to backend DTO
    const request: DriverRegistrationRequest = {
      firstName: this.driverForm.firstName,
      lastName: this.driverForm.lastName,
      email: this.driverForm.email,
      address: this.driverForm.address,
      phone: this.driverForm.phone,
      vehicleModel: this.driverForm.vehicleModel,
      vehicleType: this.driverForm.vehicleType as VehicleType,
      licensePlate: this.driverForm.licensePlate,
      seats: this.driverForm.seats,
      babyTransport: this.driverForm.babyFriendly,
      petTransport: this.driverForm.petFriendly,
    };

    this.driverService.registerDriver(request).subscribe({
      next: (response) => {
        alert(`Driver registered: ${response.firstName} ${response.lastName}\nEmail: ${response.email}`);
        this.resetForm(form);
      },
      error: (err) => {
        console.error('Error:', err);
        alert('Failed to register driver: ' + (err.error?.message || 'Unknown error'));
      }
    });
  }

  resetForm(form?: NgForm) {
    this.driverForm = {
      email: '',
      firstName: '',
      lastName: '',
      address: '',
      phone: '',
      vehicleModel: '',
      vehicleType: 'STANDARD',
      licensePlate: '',
      seats: 4,
      babyFriendly: false,
      petFriendly: false,
    };
    form?.resetForm(this.driverForm); 
  }
}
