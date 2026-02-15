import { Component, ChangeDetectorRef, Output, EventEmitter, Input, OnInit, Inject, Optional } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatIconModule } from '@angular/material/icon';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { RideService } from '../../services/ride.service';
import { OrderRideService } from '../../services/order-ride.service';
import { UserService } from '../../services/user.service';
import { PassengerManagementService } from '../../services/schedule_ride/passenger-management.service';
import { StopManagementService } from '../../services/schedule_ride/stop-management.service';
import { RideEstimationRequest, RideEstimationResponse } from '../../models/ride-estimation.model';
import { RideOrderRequest, RideResponse } from '../../models/ride.model';
import { VehicleType } from '../../models/enums';
import { GeocodeHit } from '../../services/graphhopper.service';
import { LocationInputComponent } from './location-input/location-input.component';
import { ScheduleTimeComponent } from './schedule-time/schedule-time.component';

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
    LocationInputComponent,
    ScheduleTimeComponent
  ],
  templateUrl: './schedule-ride.html',
  styleUrl: './schedule-ride.css'
})
export class ScheduleRideComponent implements OnInit {
  @Input() prefilledRoute?: {
    startLocation: string;
    endLocation: string;
    startLatitude: number;
    startLongitude: number;
    endLatitude: number;
    endLongitude: number;
  };

  startAddress = '';
  endAddress = '';
  hasPet = false;
  hasBaby = false;
  vehicleType = 'STANDARD';
  scheduleType = 'now';
  scheduledHour = '12';
  scheduledMinute = '00';
  additionalInstructions = '';

  @Output() locationsChanged = new EventEmitter<{
    startLat?: number;
    startLng?: number;
    endLat?: number;
    endLng?: number;
    stops: Array<{ lat?: number; lng?: number }>;
  }>();

  get stops() { return this.stopManagement.stops; }
  get passengers() { return this.passengerManagement.passengers; }

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
    private orderRideService: OrderRideService,
    private userService: UserService,
    private passengerManagement: PassengerManagementService,
    private stopManagement: StopManagementService,
    private cdr: ChangeDetectorRef,
    @Optional() @Inject(MAT_DIALOG_DATA) public dialogData?: any
  ) {}

  ngOnInit(): void {
    // Pre-fill from dialog data (favorite route) if provided
    if (this.dialogData) {
      this.startAddress = this.dialogData.startLocation;
      this.endAddress = this.dialogData.endLocation;
      this.startLat = this.dialogData.startLatitude;
      this.startLng = this.dialogData.startLongitude;
      this.endLat = this.dialogData.endLatitude;
      this.endLng = this.dialogData.endLongitude;
    } else if (this.prefilledRoute) {
      // Fallback to @Input if not using dialog
      this.startAddress = this.prefilledRoute.startLocation;
      this.endAddress = this.prefilledRoute.endLocation;
      this.startLat = this.prefilledRoute.startLatitude;
      this.startLng = this.prefilledRoute.startLongitude;
      this.endLat = this.prefilledRoute.endLatitude;
      this.endLng = this.prefilledRoute.endLongitude;
    }
  }


  addPassenger() {
    this.passengerManagement.addPassenger();
  }

  removePassenger() {
    this.passengerManagement.removePassenger();
  }

  updatePassengerInput(index: number, input: string) {
    this.passengerManagement.updatePassenger(index, input);
  }

  addStop() {
    this.stopManagement.addStop();
  }

  removeStop(id: string) {
    this.stopManagement.removeStop(id);
    this.refreshEstimate();
  }

  onVehicleTypeChange() {
    this.refreshEstimate();
  }

  onStartLocationSelected(hit: GeocodeHit) {
    this.startLat = hit.point.lat;
    this.startLng = hit.point.lng;
    this.emitLocationChanges();
    this.refreshEstimate();
  }

  onEndLocationSelected(hit: GeocodeHit) {
    this.endLat = hit.point.lat;
    this.endLng = hit.point.lng;
    this.emitLocationChanges();
    this.refreshEstimate();
  }

  onStopLocationSelected(stopId: string, hit: GeocodeHit) {
    this.stopManagement.updateStopLocation(stopId, hit.point.lat, hit.point.lng);
    this.emitLocationChanges();
    this.refreshEstimate();
  }

  private emitLocationChanges() {
    this.locationsChanged.emit({
      startLat: this.startLat,
      startLng: this.startLng,
      endLat: this.endLat,
      endLng: this.endLng,
      stops: this.stopManagement.getValidStops()
    });
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
    this.stopManagement.clearStopLocation(stopId);
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
      intermediateStops: this.stopManagement.getValidStops()
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
      error: (err: HttpErrorResponse) => {
        console.error('Failed to estimate ride', err);
        this.estimatedPrice = null;
        this.estimating = false;
        this.cdr.detectChanges();
      }
    });
  }

  private getMaxPassengersForVehicle(vehicleType: string): number {
    switch (vehicleType) {
      case 'STANDARD':
      case 'LUXURY':
        return 4;
      case 'VAN':
        return 7;
      default:
        return 4;
    }
  }

  bookRide() {
    if (!this.startAddress || !this.endAddress) {
      alert('Please fill in both pickup and destination locations');
      return;
    }

    // Check if coordinates are defined (not checking for falsy since 0 is a valid coordinate)
    if (this.startLat === undefined || this.startLng === undefined || this.endLat === undefined || this.endLng === undefined) {
      alert('Please select valid locations from the suggestions');
      return;
    }

    // Debug: Log coordinates to verify they are valid numbers
    console.log('Booking ride with coordinates:', {
      startLat: this.startLat,
      startLng: this.startLng,
      endLat: this.endLat,
      endLng: this.endLng,
      startAddress: this.startAddress,
      endAddress: this.endAddress
    });

    // Validate passenger count for vehicle type
    const maxPassengers = this.getMaxPassengersForVehicle(this.vehicleType);
    const totalPassengers = 1 + this.passengerManagement.passengers.filter(p => p.input.trim().length > 0).length;
    
    if (totalPassengers > maxPassengers) {
      alert(`${this.vehicleType} can accommodate maximum ${maxPassengers} passengers. You have ${totalPassengers}.`);
      return;
    }

    const userStr = localStorage.getItem('current_user');
    if (!userStr) {
      alert('Please log in to book a ride');
      return;
    }
    const user = JSON.parse(userStr);

    console.log('Current user:', user);

    if (!user.userId || user.userId <= 0) {
      alert('Invalid user ID. Please log in again.');
      return;
    }

    this.passengerManagement.resolvePassengerIds(user.userId)
      .then(passengerIds => {
        this.submitRideOrder(user.userId, passengerIds);
      })
      .catch(err => {
        alert('One or more passenger emails not found. Please check and try again.');
      });
  }

  private submitRideOrder(creatorId: number, passengerIds: number[]) {
    // Format scheduledAt: always send a time (now for immediate, or scheduled time for later)
    let scheduledAt: string;
    if (this.scheduleType === 'later' && this.scheduledHour && this.scheduledMinute) {
      const today = new Date();
      const dateStr = today.toISOString().split('T')[0]; // Get YYYY-MM-DD
      scheduledAt = `${dateStr}T${this.scheduledHour}:${this.scheduledMinute}:00`; // Combine with HH:mm and add seconds
    } else {
      // For immediate rides, send current time
      scheduledAt = new Date().toISOString().substring(0, 19); // Format: YYYY-MM-DDTHH:mm:ss
    }

    const request: RideOrderRequest = {
      creatorId,
      passengerIds,
      startLocation: this.startAddress,
      endLocation: this.endAddress,
      startLatitude: this.startLat!,
      startLongitude: this.startLng!,
      endLatitude: this.endLat!,
      endLongitude: this.endLng!,
      waypoints: this.stopManagement.getValidStops()
        .map(s => `${s.lat},${s.lng}`),
      scheduledAt: scheduledAt,
      babySeat: this.hasBaby,
      petFriendly: this.hasPet,
      vehicleType: this.vehicleType as VehicleType,
      estimatedPrice: this.estimatedPrice ? parseFloat(this.estimatedPrice.split(' ')[0]) : 0
    };

    console.log('Submitting ride order with request:', request);

    this.orderRideService.orderRide(request).subscribe({
      next: (response: RideResponse) => {
        console.log(`Ride booked successfully! Ride ID: ${response.rideId}`);
      },
      error: (err: HttpErrorResponse) => {
        console.error('Error response:', err);
        console.error('Error message:', err.error);
        if (err.status === 503) {
          console.log('No drivers available. Notification sent to passenger.');
          return;
        }
        alert('Failed to book ride: ' + (err.error || 'Unknown error'));
      }
    });
  }
}
