import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../enviroment';
import { ActiveVehicle } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class MapService {
  private apiUrl = `${environment.apiUrl}/public-map`;

  constructor(private http: HttpClient) {}

  // 2.1.1 Get Active Vehicles
  getActiveVehicles(): Observable<ActiveVehicle[]> {
    return this.http.get<ActiveVehicle[]>(`${this.apiUrl}/active`);
  }
}