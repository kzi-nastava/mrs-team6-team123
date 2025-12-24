import { CommonModule } from '@angular/common';
import { Component, Input, effect } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatButtonModule
  ],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css'],
})
export class NavbarComponent {
  @Input() showHamburger = false;

  links: { route: string; icon: string }[] = [];
  menuActive = false;

  guestLinks = [
    { route: '/registered-home', icon: 'home.png' },
    //{ route: '/login', icon: 'user.png' },
    { route: '/driver/ride-history', icon: 'history.png' },
    { route: '/login', icon: 'login.png' },
    { route: '/register', icon: 'register.png' },
    { route: '/profile', icon: 'user.png' },
    //{ route: '/driver/profile', icon: 'drivers.png' },
    //{ route: '/admin/profile', icon: 'admin.png' }
  ];

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
    { route: '/driver/reports', icon: 'report.png' },
    { route: '/profile', icon: 'user.png' }
  ];

  adminLinks = [
    { route: '/admin/home', icon: 'home.png' },
    { route: '/admin/ride-history', icon: 'history.png' },
    { route: '/admin/drivers', icon: 'drivers.png' },
    { route: '/admin/reports', icon: 'report.png' },
    { route: '/admin/pricing', icon: 'pricing.png' },
    { route: '/admin/notifications', icon: 'notification.png' },
    { route: '/profile', icon: 'user.png' }
  ]

  constructor(private authService: AuthService) {
      // TEST: Uncomment one to test
  this.authService.login({
    id: '1',
    name: 'John Driver',
    email: 'driver@test.com',
    type: 'driver'
  });

  // this.authService.login({
  //   id: '2',
  //   name: 'Jane Passenger',
  //   email: 'passenger@test.com',
  //   type: 'passenger'
  // });

    effect(() => {
      const userType = this.authService.userType();
      if (userType === 'driver') {
        this.links = [...this.driverLinks];
      } else if (userType === 'passenger') {
        this.links = [...this.registeredUserLinks];
      } else if (userType === 'admin') {
        this.links = [...this.adminLinks];
      } else {
        this.links = [...this.guestLinks];
      }
    });
  }

  toggleMenu() {
    this.menuActive = !this.menuActive;
  }
}
