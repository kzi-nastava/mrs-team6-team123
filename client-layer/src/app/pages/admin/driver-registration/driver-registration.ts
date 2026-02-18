import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { DriverService } from '../../../services/driver.service';
import { DriverRegistrationRequest } from '../../../models/driver.model';
import { VehicleType } from '../../../models/enums';
import { validateDriverForm } from '../../../utils/driver-registration-validation';

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
    { value: 'LUXURY', label: 'Luxury' },
    { value: 'VAN', label: 'Van' },
  ];


  constructor(private driverService: DriverService) {}

  submit(form?: NgForm) {
    const f = this.driverForm;
    const validation = validateDriverForm(f);
    if (!validation.valid) {
      alert(validation.message);
      return;
    }

    // Only create request and call service if validation passes
    const request: DriverRegistrationRequest = {
      firstName: f.firstName,
      lastName: f.lastName,
      email: f.email,
      address: f.address,
      phone: f.phone,
      vehicleModel: f.vehicleModel,
      vehicleType: f.vehicleType as VehicleType,
      licensePlate: f.licensePlate,
      seats: f.seats,
      babyTransport: f.babyFriendly,
      petTransport: f.petFriendly,
    };

    if (this.driverService && typeof this.driverService.registerDriver === 'function') {
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
