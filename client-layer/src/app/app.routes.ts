import { Routes } from '@angular/router';
import { RideHistoryComponent } from './pages/driver/ride-history/ride-history';
import { RegisteredHome } from './pages/registered-home/registered-home';
import { UnregisteredHomeComponent } from './pages/unregistered-home/unregistered-home';

export const routes: Routes = [
  {path: 'driver/ride-history', component: RideHistoryComponent },

  { path: '', redirectTo: '/unregistered-home', pathMatch: 'full' },
  { path: 'registered-home', component: RegisteredHome },

  { path: 'unregistered-home', component: UnregisteredHomeComponent }
];
