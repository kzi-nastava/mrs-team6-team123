import { Component, signal, effect } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar';
import { AuthService } from './services/auth.service';
import { ChatWidgetComponent } from './components/chat/chat-widget/chat-widget';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, ChatWidgetComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {
  protected readonly title = signal('client-layer');
  navLinks: { route: string; icon: string }[] = [];

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

  constructor(private authService: AuthService) {
      // TEST: Uncomment one to test
      // this.authService.login({
      //   id: '1',
      //   name: 'John Driver',
      //   email: 'driver@test.com',
      //   type: 'driver'
      // });

      // this.authService.login({
      //   id: '2',
      //   name: 'Jane Passenger',
      //   email: 'passenger@test.com',
      //   type: 'passenger'
      // });
    effect(() => {
      const userType = this.authService.userType();
      if (userType === 'driver') {
        this.navLinks = [...this.driverLinks];
      } else {
        this.navLinks = [...this.registeredUserLinks];
      }
    });
  }
}
