import { CommonModule } from '@angular/common';
import { Component, computed, EventEmitter, Output } from '@angular/core';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-ride-actions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ride-actions.html',
  styleUrls: ['./ride-actions.css'],
})
export class RideActionsComponent {
  constructor(public auth: AuthService) {}

  userType = computed(() => this.auth.getUserType());

  get isAdmin() {
    return this.userType() === 'admin';
  }

  get isDriver() {
    return this.userType() === 'driver';
  }

  get isPassenger() {
    return this.userType() === 'passenger';
  }

  @Output() panicClicked = new EventEmitter<void>();
  @Output() stopRideClicked = new EventEmitter<void>();
  @Output() reportClicked = new EventEmitter<void>();
  @Output() goInactiveClicked = new EventEmitter<void>();
  @Output() finishClicked = new EventEmitter<void>();

  onPanic() {
    this.panicClicked.emit();
  }

  onStopRide() {
    this.stopRideClicked.emit();
  }

  onReport() {
    this.reportClicked.emit();
  }

  onGoInactive() {
    this.goInactiveClicked.emit();
  }

  onFinish() {
    this.finishClicked.emit();
  }
}
