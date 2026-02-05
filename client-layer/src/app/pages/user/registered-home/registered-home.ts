import { Component, OnInit } from '@angular/core';
import { MapComponent } from '../../../components/map/map';
import { ScheduleRideComponent } from '../../../components/schedule-ride/schedule-ride';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { SoundService } from '../../../services/sound.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-registered-home',
  imports: [MapComponent, ScheduleRideComponent],
  templateUrl: './registered-home.html',
  styleUrl: './registered-home.css',
})
export class RegisteredHome implements OnInit {
  constructor(
      private authService: AuthService,
      private notificationService: NotificationService,
      private soundService: SoundService
    ) {}
  playedOnce = false;
  private notifSub?: Subscription;
  rideForMap?: {
    startLat?: number;
    startLng?: number;
    endLat?: number;
    endLng?: number;
  };

  onLocationsChanged(locations: {
    startLat?: number;
    startLng?: number;
    endLat?: number;
    endLng?: number;
    stops: Array<{ lat?: number; lng?: number }>;
  }) {
    this.rideForMap = locations;
  }

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    if (userId) {
      this.notificationService.loadUnread(userId);
    }
    this.playedOnce = false;
    this.notifSub = this.notificationService.getUnread().subscribe(list => {
      if (list.length > 0 && !this.playedOnce) {
        this.soundService.play();
        this.playedOnce = true;
        console.log('Playing notification sound');
      }
    });
  }
}
