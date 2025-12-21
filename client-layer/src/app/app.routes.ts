import { Routes } from '@angular/router';
import { RideHistoryComponent } from './pages/driver/ride-history/ride-history';
import { RegisteredHome } from './pages/registered-home/registered-home';

export const routes: Routes = [
    {path: 'driver/ride-history', component: RideHistoryComponent },

  { path: '', redirectTo: '/registered-home', pathMatch: 'full' },
  { path: 'registered-home', component: RegisteredHome }
];
