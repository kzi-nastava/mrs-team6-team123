import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatIconModule } from '@angular/material/icon';
import { RideService } from '../../services/ride.service';
import { RideEstimationRequest, RideEstimationResponse } from '../../models/ride-estimation.model';
import { VehicleType } from '../../models/enums';
import { GeocodeHit } from '../../services/graphhopper.service';
import { LocationInputComponent } from './location-input/location-input.component';

interface Stop {
  id: string;
  address: string;
  lat?: number;
  lng?: number;
}

interface Passenger {
  name: string;
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
    MatIconModule,
    LocationInputComponent
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
  passengers: Passenger[] = [];
  vehicleType = 'STANDARD';
  scheduleType = 'now';
  scheduledTime = '';
  additionalInstructions = '';

  vehicleOptions = [
    { value: 'STANDARD', label: 'Standard' },
    { value: 'LUXURY', label: 'Luxury' },
    { value: 'VAN', label: 'Van' }
  ];

  estimatedPrice: string | null = null;
  estimating = false;

  startLat?: number;
  startLng?: number;
  endLat?: number;
  endLng?: number;

  constructor(
    private rideService: RideService,
    private cdr: ChangeDetectorRef
  ) {}

  addPassenger() {
    this.passengers.push({ name: '' });
  }

  removePassenger() {
    this.passengers.pop();
  }

  updatePassengerName(index: number, name: string) {
    if (this.passengers[index]) {
      this.passengers[index].name = name;
    }
  }

  addStop() {
    this.stops.push({
      id: Date.now().toString(),
      address: ''
    });
  }

  removeStop(id: string) {
    this.stops = this.stops.filter(stop => stop.id !== id);
    this.refreshEstimate();
  }

  onVehicleTypeChange() {
    this.refreshEstimate();
  }

  onStartLocationSelected(hit: GeocodeHit) {
    this.startLat = hit.point.lat;
    this.startLng = hit.point.lng;
    this.refreshEstimate();
  }

  onEndLocationSelected(hit: GeocodeHit) {
    this.endLat = hit.point.lat;
    this.endLng = hit.point.lng;
    this.refreshEstimate();
  }

  onStopLocationSelected(stopId: string, hit: GeocodeHit) {
    const stop = this.stops.find(s => s.id === stopId);
    if (stop) {
      stop.lat = hit.point.lat;
      stop.lng = hit.point.lng;
      this.refreshEstimate();
    }
  }

  onStartInputChanged(query: string) {
    this.startLat = this.startLng = undefined;
    this.estimatedPrice = null;
  }

  onEndInputChanged(query: string) {
    this.endLat = this.endLng = undefined;
    this.estimatedPrice = null;
  }

  onStopInputChanged(stopId: string, query: string) {
    const stop = this.stops.find(s => s.id === stopId);
    if (stop) {
      stop.lat = stop.lng = undefined;
    }
    this.estimatedPrice = null;
  }

  private refreshEstimate() {
    if (!this.startAddress || !this.endAddress || !this.startLat || !this.endLat) {
      this.estimatedPrice = null;
      return;
    }

    const request: RideEstimationRequest = {
      startLocation: `${this.startLat},${this.startLng}`,
      endLocation: `${this.endLat},${this.endLng}`,
      intermediateStops: this.stops
        .filter(s => s.lat !== undefined && s.lng !== undefined)
        .map(s => `${s.lat},${s.lng}`),
      vehicleType: this.vehicleType as VehicleType
    };

    this.estimating = true;
    this.rideService.estimateRide(request).subscribe({
      next: (response: RideEstimationResponse) => {
        this.estimatedPrice = `${response.estimatedPrice.toFixed(2)} RSD`;
        this.estimating = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to estimate ride', err);
        this.estimatedPrice = null;
        this.estimating = false;
        this.cdr.detectChanges();
      }
    });
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
