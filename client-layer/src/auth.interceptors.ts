import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  //Skip auth header for GraphHopper (CORS-safe)
  if (req.url.includes('graphhopper.com')) {
    return next(req);
  }

  // Get token from localStorage
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
