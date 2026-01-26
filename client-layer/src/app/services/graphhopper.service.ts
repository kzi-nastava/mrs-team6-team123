import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RideStop } from '../models/track-ride.model';
import { environment } from '../../enviroment';

@Injectable({
  providedIn: 'root'
})
export class GraphhopperService {

  private baseUrl = `${environment.apiUrl}/api/route`;

  constructor(private http: HttpClient) { }

  getRoute(points: RideStop[]): Observable<any> {
    return this.http.post(this.baseUrl, points);
  }
}
