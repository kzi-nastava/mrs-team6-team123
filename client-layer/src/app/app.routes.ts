import { Routes } from '@angular/router';
import { RegisteredHome } from './pages/user/registered-home/registered-home';
import { UnregisteredHomeComponent } from './pages/unregistered-home/unregistered-home';
import { UserFavoritesComponent } from './pages/user/user-favorites/user-favorites';
import { ProfileComponent } from './pages/profile/profile';
import { LoginComponent } from './pages/auth/login/login';
import { RegisterComponent } from './pages/auth/register/register';
import { ResetPasswordComponent } from './pages/auth/reset-password/reset-password';
import { AdminHomeComponent } from './pages/admin/admin-home/admin-home';
import { TrackRidePageComponent } from './pages/track-ride-page/track-ride-page';
import { DriverRideHistoryComponent } from './pages/driver/driver-ride-history/driver-ride-history';
import { DriverHomeComponent } from './pages/driver/driver-home/driver-home';
import { authGuard, guestGuard, driverGuard, adminGuard, passengerGuard } from './guards/auth.guard';
import { AdminPricingComponent } from './pages/admin/admin-pricing/admin-pricing';
import { DriverRegistration } from './pages/admin/driver-registration/driver-registration';
import { ActivateAccountComponent } from './pages/auth/activate-account/activate-account';
import { ForgotPasswordComponent } from './pages/auth/forgot-password/forgot-password';
import { PassengerRideHistoryComponent } from './pages/user/passenger-ride-history/passenger-ride-history';
import { AdminRideHistoryComponent } from './pages/admin/admin-ride-history/admin-ride-history';
import { ReportsComponent } from './components/reports/reports';


import { RateRidePageComponent } from './pages/rate-ride-page/rate-ride-page';

export const routes: Routes = [
  // Public routes
  { path: '', redirectTo: '/unregistered-home', pathMatch: 'full' },
  { path: 'unregistered-home', component: UnregisteredHomeComponent },
  
  // Guest only
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },
  { path: 'forgot-password', component: ForgotPasswordComponent, canActivate: [guestGuard] },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'activate', component: ActivateAccountComponent },
  
  // Protected routes
  { path: 'registered-home', component: RegisteredHome, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'favorites', component: UserFavoritesComponent, canActivate: [authGuard] },
  { path: 'track-ride-page', component: TrackRidePageComponent, canActivate: [authGuard] },
  { path: 'history', component: PassengerRideHistoryComponent, canActivate: [authGuard] },
  { path: 'rate-ride', component: RateRidePageComponent, canActivate: [authGuard] },
  { path: 'reports', component: ReportsComponent, canActivate: [passengerGuard] },

  // Driver only
  { path: 'driver/home', component: DriverHomeComponent, canActivate: [driverGuard] },
  { path: 'driver/driver-ride-history', component: DriverRideHistoryComponent, canActivate: [driverGuard] },
  { path: 'driver/reports', component: ReportsComponent, canActivate: [driverGuard] },
  
  // Admin only
  { path: 'admin/home', component: AdminHomeComponent, canActivate: [adminGuard] },
  { path: 'admin/drivers', component: DriverRegistration, canActivate: [adminGuard] },
  { path: 'admin/pricing', component: AdminPricingComponent, canActivate: [adminGuard] },
  { path: 'admin/ride-history', component: AdminRideHistoryComponent, canActivate: [adminGuard] },
  { path: 'admin/reports', component: ReportsComponent, canActivate: [adminGuard] },
  
  // Fallback
  { path: '**', redirectTo: '/unregistered-home' }
];
