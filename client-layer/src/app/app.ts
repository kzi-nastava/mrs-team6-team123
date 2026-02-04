import { Component, signal, effect, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar';
import { AuthService } from './services/auth.service';
import { ChatWidgetComponent } from './components/chat/chat-widget/chat-widget';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { NotificationService } from './services/notification.service';
import { SoundService } from './services/sound.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, ChatWidgetComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements OnInit, OnDestroy {
  protected readonly title = signal('client-layer');
  navLinks: { route: string; icon: string }[] = [];
  playedOnce = false;
  private notifSub?: Subscription;

  registeredUserLinks = [
    { route: '/registered-home', icon: 'home.png' },
    { route: '/history', icon: 'history.png' },
    { route: '/favorites', icon: 'favorites.png' },
    { route: '/notifications', icon: 'notification.png' },
    { route: '/profile', icon: 'user.png' }
  ];

  driverLinks = [
    { route: '/driver/home', icon: 'home.png' },
    { route: '/driver/ride-history', icon: 'history.png' },
    { route: '/driver/favorites', icon: 'favorites.png' },
    { route: '/driver/notifications', icon: 'notification.png' },
    { route: '/driver/reports', icon: 'pricing.png' },
    { route: '/driver/profile', icon: 'user.png' }
  ];

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private soundService: SoundService
  ) {
    effect(() => {
      const userType = this.authService.userType();
      if (userType === 'DRIVER') {
        this.navLinks = [...this.driverLinks];
      } else {
        this.navLinks = [...this.registeredUserLinks];
      }
    });
  }
  ngOnDestroy(): void {
    this.notifSub?.unsubscribe();
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
