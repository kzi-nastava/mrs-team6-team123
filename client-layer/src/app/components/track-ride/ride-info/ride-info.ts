import { CommonModule } from '@angular/common';
import { Component, computed, Input } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { RideInfo } from '../../../models/track-ride.model';

@Component({
  selector: 'app-ride-info',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ride-info.html',
  styleUrls: ['./ride-info.css'],
})
export class RideInfoComponent {
  @Input() rideInfo!: RideInfo;

  constructor(
    public auth: AuthService
  ) {}

  userType = computed(() => this.auth.getUserType());

  get isAdmin() {
    return this.userType() === 'ADMIN';
  }

  get isDriver() {
    return this.userType() === 'DRIVER';
  }

  get passengers() {
    return this.rideInfo.passengers || [];
  }

  get reports() {
    return this.rideInfo.reports || [];
  }
}
