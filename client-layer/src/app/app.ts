import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {
  protected readonly title = signal('client-layer');
  
  registeredUserLinks = [
    { label: 'Home', route: '/registered-home' },
    { label: 'History', route: '/history' },
    { label: 'Favorites', route: '/favorites' },
    { label: 'Notifications', route: '/notifications' },
    { label: 'Profile', route: '/profile' }
  ];
}
