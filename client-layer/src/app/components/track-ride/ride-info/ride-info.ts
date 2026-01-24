import { CommonModule } from '@angular/common';
import { Component, computed, Input } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { RideInfo, TrackRideResponse } from '../../../models/track-ride.model';

export interface Ride {
  from: string;
  to: string;
  nextStop?: string;
  timeLeft?: string;
  driverName?: string;
  price?: number;
  passengers?: string[];
  startedAt?: string;
}

@Component({
  selector: 'app-ride-info',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ride-info.html',
  styleUrls: ['./ride-info.css'],
})
export class RideInfoComponent {
  @Input() ride!: TrackRideResponse;

  constructor(public auth: AuthService) {}

  userType = computed(() => this.auth.getUserType());

  get isAdmin() {
    return this.userType() === 'admin';
  }

  get isDriver() {
    return this.userType() === 'driver';
  }

  get passengers() {
    return this.ride?.info.passengers || [];
  }
}
