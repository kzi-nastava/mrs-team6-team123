import { Routes } from '@angular/router';
import { RideHistoryComponent } from './pages/driver/ride-history/ride-history';
import { RegisteredHome } from './pages/registered-home/registered-home';
import { UnregisteredHomeComponent } from './pages/unregistered-home/unregistered-home';
import { UserFavoritesComponent } from './pages/user-favorites/user-favorites';
import { ProfileComponent } from './pages/profile/profile';
import { LoginComponent } from './pages/auth/login/login';
import { RegisterComponent } from './pages/auth/register/register';
import { ResetPasswordComponent } from './pages/auth/reset-password/reset-password';
import { AdminHomeComponent } from './pages/admin-home/admin-home';
import { TrackRidePageComponent } from './pages/track-ride-page/track-ride-page';

export const routes: Routes = [
  {path: 'driver/ride-history', component: RideHistoryComponent },

  { path: '', redirectTo: '/admin-home', pathMatch: 'full' },
  { path: 'registered-home', component: RegisteredHome },
  { path: 'favorites', component: UserFavoritesComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'unregistered-home', component: UnregisteredHomeComponent },
  { path: 'admin-home', component: AdminHomeComponent},
  { path: 'track-ride-page', component: TrackRidePageComponent}
];
