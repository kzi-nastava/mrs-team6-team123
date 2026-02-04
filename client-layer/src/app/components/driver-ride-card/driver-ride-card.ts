import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DriverAssignedRide } from '../../services/driver.service';

@Component({
  selector: 'app-driver-ride-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-ride-card.html',
  styleUrls: ['./driver-ride-card.css']
})
export class DriverRideCardComponent {
  @Input() ride!: DriverAssignedRide;
  @Input() hasAcceptedRide: boolean = false;
  
  @Output() accept = new EventEmitter<number>();
  @Output() start = new EventEmitter<number>();
  @Output() seeRoute = new EventEmitter<DriverAssignedRide>();

  onAccept() {
    this.accept.emit(this.ride.rideId);
  }

  onStart() {
    this.start.emit(this.ride.rideId);
  }

  onSeeRoute() {
    this.seeRoute.emit(this.ride);
  }

  formatScheduledTime(scheduledAt: string): string {
    if (!scheduledAt) return '';
    const date = new Date(scheduledAt);
    return date.toLocaleString();
  }
}
