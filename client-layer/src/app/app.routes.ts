import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/home/home').then(m => m.HomeComponent)
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./pages/register/register').then(m => m.RegisterComponent)
  },
  {
    path: 'history',
    loadComponent: () =>
      import('./pages/history/history').then(m => m.HistoryComponent)
  },
  {
    path: 'favorites',
    loadComponent: () =>
      import('./pages/favorites/favorites').then(m => m.FavoritesComponent)
  },
  {
    path: 'notifications',
    loadComponent: () =>
      import('./pages/notifications/notifications').then(m => m.NotificationsComponent)
  },
  {
    path: 'profile',
    loadComponent: () =>
      import('./pages/profile/profile').then(m => m.ProfileComponent)
  },

  // fallback
  {
    path: '**',
    redirectTo: ''
  }
];
