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
import { AdminPricingComponent } from './pages/admin/admin-pricing/admin-pricing';
import { DriverRegistration } from './pages/admin/driver-registration/driver-registration';
import { ActivateAccountComponent } from './pages/auth/activate-account/activate-account';
import { ForgotPasswordComponent } from './pages/auth/forgot-password/forgot-password';
import { RateRidePageComponent } from './pages/rate-ride-page/rate-ride-page';

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
  { path: 'admin/drivers', component: DriverRegistration },
  { path: 'track-ride-page', component: TrackRidePageComponent },
  { path: 'rate-ride', component: RateRidePageComponent },
  { path: 'admin-pricing', component: AdminPricingComponent },
  {path: 'activate',component: ActivateAccountComponent},
  {path: 'forgot-password', component: ForgotPasswordComponent},
  {path: 'reset-password', component: ResetPasswordComponent}
];
