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
import { authGuard, guestGuard, driverGuard, adminGuard, passengerGuard } from './guards/auth.guard';
import { AdminPricingComponent } from './pages/admin/admin-pricing/admin-pricing';
import { DriverRegistration } from './pages/admin/driver-registration/driver-registration';
import { ActivateAccountComponent } from './pages/auth/activate-account/activate-account';
import { ForgotPasswordComponent } from './pages/auth/forgot-password/forgot-password';
import { PassengerRideHistoryComponent } from './pages/user/passenger-ride-history/passenger-ride-history';
import { AdminRideHistoryComponent } from './pages/admin/admin-ride-history/admin-ride-history';


import { RateRidePageComponent } from './pages/rate-ride-page/rate-ride-page';

export const routes: Routes = [
  // Javne rute 
  { path: '', redirectTo: '/unregistered-home', pathMatch: 'full' },
  { path: 'unregistered-home', component: UnregisteredHomeComponent },
  
  // Samo za goste
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },
  { path: 'forgot-password', component: ForgotPasswordComponent, canActivate: [guestGuard] },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'activate', component: ActivateAccountComponent },
  
  // Zaštićene rute
  { path: 'registered-home', component: RegisteredHome, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'favorites', component: UserFavoritesComponent, canActivate: [authGuard] },
  { path: 'track-ride-page', component: TrackRidePageComponent, canActivate: [authGuard] },
  { path: 'history', component: PassengerRideHistoryComponent, canActivate: [authGuard] },
  
  // Samo za vozače
  { path: 'driver/driver-ride-history', component: DriverRideHistoryComponent, canActivate: [driverGuard] },
  
  // Samo za admine
  { path: 'admin-home', component: AdminHomeComponent, canActivate: [adminGuard] },
  { path: 'admin/drivers', component: DriverRegistration, canActivate: [adminGuard] },
  { path: 'admin-pricing', component: AdminPricingComponent, canActivate: [adminGuard] },
  { path: 'admin/ride-history', component: AdminRideHistoryComponent, canActivate: [adminGuard] },
  
  // Fallback
  { path: '**', redirectTo: '/unregistered-home' }
];
