import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { RideStop } from '../models/track-ride.model';
import { environment } from '../../enviroment';

export interface GeocodeHit {
  point: { lat: number; lng: number };
  name: string;
  street?: string;
  housenumber?: string;
  city?: string;
  country?: string;
}

export interface GeocodeResponse {
  hits: Array<{
    point: { lat: number; lng: number };
    name: string;
    street?: string;
    housenumber?: string;
    city?: string;
    country?: string;
  }>;
}

@Injectable({
  providedIn: 'root'
})
export class GraphhopperService {

  private baseUrl = `${environment.apiUrl}/api/route`;
  private geocodeUrl = 'https://graphhopper.com/api/1/geocode';

  constructor(private http: HttpClient) { }

  getRoute(points: RideStop[]): Observable<any> {
    return this.http.post(this.baseUrl, points);
  }

  geocode(query: string, limit: number = 10): Observable<GeocodeHit[]> {
    const params = new HttpParams()
      .set('q', query)
      .set('limit', limit.toString())
      .set('locale', 'sr')
      .set('key', environment.graphhopperApiKey || '');

    return this.http.get<GeocodeResponse>(this.geocodeUrl, { params })
      .pipe(
        map(response => {
          // Transform hits to include better formatting
          const hits = response.hits.map(hit => ({
            ...hit,
            name: this.formatLocationName(hit)
          }));

          // Sort: Prioritize Novi Sad results
          return hits.sort((a, b) => {
            const aIsNoviSad = a.city?.toLowerCase().includes('novi sad') || 
                              a.name.toLowerCase().includes('novi sad');
            const bIsNoviSad = b.city?.toLowerCase().includes('novi sad') || 
                              b.name.toLowerCase().includes('novi sad');
            
            if (aIsNoviSad && !bIsNoviSad) return -1;
            if (!aIsNoviSad && bIsNoviSad) return 1;
            return 0;
          });
        })
      );
  }

  private formatLocationName(hit: any): string {
    const parts: string[] = [];
    
    // Add street with house number if available
    if (hit.street) {
      if (hit.housenumber) {
        parts.push(`${hit.street} ${hit.housenumber}`);
      } else {
        parts.push(hit.street);
      }
    } else if (hit.name) {
      // Fallback to name if no street
      parts.push(hit.name);
    }
    
    // Add city if available and not already in the name
    if (hit.city && !parts[0]?.toLowerCase().includes(hit.city.toLowerCase())) {
      parts.push(hit.city);
    }
    
    return parts.join(', ');
  }
}
