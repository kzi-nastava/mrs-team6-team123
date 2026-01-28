import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';

export interface GeocodingResult {
  displayName: string;
  latitude: number;
  longitude: number;
}

@Injectable({
  providedIn: 'root'
})
export class GeocodingService {
  private nominatimUrl = 'https://nominatim.openstreetmap.org';

  constructor(private http: HttpClient) {}

  geocodeAddress(address: string): Observable<GeocodingResult | null> {
    const params = {
      q: address,
      format: 'json',
      limit: '1',
      addressdetails: '1'
    };

    return this.http.get<any[]>(`${this.nominatimUrl}/search`, { params }).pipe(
      map(results => {
        if (results && results.length > 0) {
          const result = results[0];
          return {
            displayName: result.display_name,
            latitude: parseFloat(result.lat),
            longitude: parseFloat(result.lon)
          };
        }
        return null;
      }),
      catchError(error => {
        console.error('Geocoding error:', error);
        return of(null);
      })
    );
  }

  addressToCoordinates(address: string): Observable<string | null> {
    return this.geocodeAddress(address).pipe(
      map(result => {
        if (result) {
          return `${result.latitude}, ${result.longitude}`;
        }
        return null;
      })
    );
  }
  searchAddress(query: string): Observable<GeocodingResult[]> {
    if (!query || query.length < 3) {
      return of([]);
    }

    const params = {
      q: query,
      format: 'json',
      limit: '5',
      addressdetails: '1'
    };

    return this.http.get<any[]>(`${this.nominatimUrl}/search`, { params }).pipe(
      map(results => {
        return results.map(result => ({
          displayName: result.display_name,
          latitude: parseFloat(result.lat),
          longitude: parseFloat(result.lon)
        }));
      }),
      catchError(error => {
        console.error('Search error:', error);
        return of([]);
      })
    );
  }
}