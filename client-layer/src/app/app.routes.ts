import { Routes } from '@angular/router';
import { RegisteredHome } from './pages/registered-home/registered-home';
import { UnregisteredHomeComponent } from './pages/unregistered-home/unregistered-home';
import { UserFavoritesComponent } from './pages/user-favorites/user-favorites';
import { ProfileComponent } from './pages/profile/profile';
import { LoginComponent } from './pages/auth/login/login';
import { RegisterComponent } from './pages/auth/register/register';
import { ResetPasswordComponent } from './pages/auth/reset-password/reset-password';
import { AdminHomeComponent } from './pages/admin-home/admin-home';
import { TrackRidePageComponent } from './pages/track-ride-page/track-ride-page';
import { DriverRideHistoryComponent } from './pages/driver/driver-ride-history/driver-ride-history';
import { RateRideComponent } from './components/rate-ride/rate-ride';
import { AdminPricingComponent } from './pages/admin-pricing/admin-pricing';

export const routes: Routes = [
  { path: '', redirectTo: '/unregistered-home', pathMatch: 'full' },
  { path: 'driver/driver-ride-history', component: DriverRideHistoryComponent },
  { path: 'registered-home', component: RegisteredHome },
  { path: 'favorites', component: UserFavoritesComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'unregistered-home', component: UnregisteredHomeComponent },
  { path: 'admin-home', component: AdminHomeComponent},
  { path: 'track-ride-page', component: TrackRidePageComponent },
  { path: 'rate-ride', component: RateRideComponent },
  { path: 'admin-pricing', component: AdminPricingComponent }
];
