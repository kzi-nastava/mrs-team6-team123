import { CommonModule } from '@angular/common';
import { Component, Input, OnInit, ChangeDetectorRef, effect, computed } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NotificationPanelComponent } from '../notification-panel/notification-panel';


@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatButtonModule,
    NotificationPanelComponent
  ],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css'],
})
export class NavbarComponent {
  @Input() showHamburger = false;

  links: { route: string; icon: string; type: string }[] = [];
  menuActive = false;
  userRole: string = 'GUEST';
  notificationPanelOpen = false;

  isLoggedIn = computed(() => this.authService.isLoggedIn());

  guestLinks = [
    { route: '/unregistered-home', icon: 'home.png', type: 'home' },
    { route: '/login', icon: 'user.png', type: 'login' }
  ];

  registeredUserLinks = [
    { route: '/registered-home', icon: 'home.png', type: 'home' },
    { route: '/history', icon: 'history.png', type: 'history' },
    { route: '/favorites', icon: 'favorites.png', type: 'favorites' },
    { route: '', icon: 'notification.png', type: 'notifications' },
    { route: '/profile', icon: 'user.png', type: 'profile' }
  ];

  driverLinks = [
    { route: '/driver/home', icon: 'home.png', type: 'home' },
    { route: '/driver/driver-ride-history', icon: 'history.png', type: 'history' },
    { route: '/driver/favorites', icon: 'favorites.png', type: 'favorites' },
    { route: '', icon: 'notification.png', type: 'notifications' },
    { route: '/driver/reports', icon: 'report.png', type: 'reports' },
    { route: '/profile', icon: 'user.png', type: 'profile' }
  ];

  adminLinks = [
    { route: '/admin/home', icon: 'home.png', type: 'home' },
    { route: '/admin/ride-history', icon: 'history.png', type: 'history' },
    { route: '/admin/drivers', icon: 'drivers.png', type: 'drivers' },
    { route: '/admin/reports', icon: 'report.png', type: 'reports' },
    { route: '/admin/pricing', icon: 'pricing.png', type: 'pricing' },
    { route: '', icon: 'notification.png', type: 'notifications' },
    { route: '/profile', icon: 'user.png', type: 'profile' }
  ]

  constructor(private authService: AuthService, private cdr: ChangeDetectorRef) {
    // Subscribe to current user changes
    this.authService.currentUser$.subscribe(user => {
      this.userRole = user?.role || 'GUEST';
      this.updateLinks();
      this.cdr.markForCheck(); // Notify Angular of changes
    });
  }

  ngOnInit() {
    this.updateLinks();
  }

  private updateLinks() {
    if (this.userRole === 'DRIVER') {
      this.links = [...this.driverLinks];
    } else if (this.userRole === 'PASSENGER') {
      this.links = [...this.registeredUserLinks];
    } else if (this.userRole === 'ADMIN') {
      this.links = [...this.adminLinks];
    } else {
      this.links = [...this.guestLinks];
    }
  }

  onNavClick(link: { route: string; icon: string; type: string }, event: Event) {
    if (link.type === 'notifications') {
      event.preventDefault();
      this.notificationPanelOpen = !this.notificationPanelOpen;
      return;
    }

    this.notificationPanelOpen = false;
    this.menuActive = false;
  }

  toggleMenu() {
    this.menuActive = !this.menuActive;
  }

    logout() {
    this.authService.logout();
    }
}
