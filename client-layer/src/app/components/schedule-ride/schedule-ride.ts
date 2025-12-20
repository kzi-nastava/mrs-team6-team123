import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatIconModule } from '@angular/material/icon';

interface Stop {
  id: string;
  address: string;
}

@Component({
  selector: 'app-schedule-ride',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatSlideToggleModule,
    MatIconModule
  ],
  templateUrl: './schedule-ride.html',
  styleUrl: './schedule-ride.css'
})
export class ScheduleRideComponent {
  startAddress = '';
  endAddress = '';
  stops: Stop[] = [];
  hasPet = false;
  hasBaby = false;
  passengers = 1;
  vehicleType = 'economy';
  scheduleType = 'now';
  scheduledTime = this.getCurrentTime();
  additionalInstructions = '';

  vehicleOptions = [
    { value: 'standard', label: 'Standard' },
    { value: 'luxury', label: 'Comfort' },
    { value: 'van', label: 'Van' }
  ];

  estimatedPrice = '$12.50 - $18.00';
  estimatedTime = '15-20 min';

  constructor() {
    // Update scheduled time whenever we change to 'later'
  }

  private getCurrentTime(): string {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  addPassenger() {
    this.passengers++;
  }

  removePassenger() {
    if (this.passengers > 1) {
      this.passengers--;
    }
  }

  addStop() {
    const newStop: Stop = {
      id: Date.now().toString(),
      address: ''
    };
    this.stops.push(newStop);
  }

  removeStop(id: string) {
    this.stops = this.stops.filter(stop => stop.id !== id);
  }

  updateStop(id: string, address: string) {
    const stop = this.stops.find(s => s.id === id);
    if (stop) {
      stop.address = address;
    }
  }

  bookRide() {
    if (!this.startAddress || !this.endAddress) {
      alert('Please fill in both pickup and destination locations');
      return;
    }
    console.log('Ride booked:', {
      from: this.startAddress,
      to: this.endAddress,
      stops: this.stops,
      passengers: this.passengers,
      vehicleType: this.vehicleType,
      hasPet: this.hasPet,
      hasBaby: this.hasBaby,
      scheduleType: this.scheduleType,
      scheduledTime: this.scheduleType === 'now' ? 'Now' : this.scheduledTime,
      additionalInstructions: this.additionalInstructions
    });
    const time = this.scheduleType === 'now' ? 'now' : `at ${this.scheduledTime}`;
    alert(`Ride requested for ${time}! Looking for drivers...`);
  }
}
