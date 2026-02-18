
import { HttpInterceptorFn } from '@angular/common/http';

// Domeni na koje NE treba slati Authorization header
const PUBLIC_DOMAINS = [
  'graphhopper.com',
  'nominatim.openstreetmap.org',
  'openstreetmap.org',
  'tile.openstreetmap.org'
];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Preskoči dodavanje tokena za eksterne servise
  const isPublic = PUBLIC_DOMAINS.some(domain => req.url.includes(domain));
  if (isPublic) {
    return next(req);
  }

  const token = localStorage.getItem('auth_token');
  if (token) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedRequest);
  }

  return next(req);
};
