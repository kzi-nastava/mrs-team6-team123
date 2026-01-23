import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

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
    vehicleType: 'standard',
    licensePlate: '',
    seats: 4,
    babyFriendly: false,
    petFriendly: false,
  };

  vehicleTypes = [
    { value: 'standard', label: 'Standard' },
    { value: 'luxury', label: 'Luxury' },
    { value: 'van', label: 'Van' },
  ];

  submit() {
    console.log('Driver registration payload', this.driverForm);
    alert('Driver account created. Activation link sent.');
  }
}
