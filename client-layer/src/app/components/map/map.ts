import { Component, AfterViewInit, OnDestroy, Input } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import * as L from 'leaflet';
import { MapService } from '../../services/map.service';

export interface Vehicle {
  vehicleId: number;
  latitude: number;
  longitude: number;
  available: boolean;
}

type MapMode = 'vehicles' | 'staticRoute'; // more could be added if necessary

@Component({
  selector: 'app-map',
  standalone: true,
  templateUrl: './map.html',
  styleUrls: ['./map.css'],
})
export class MapComponent implements AfterViewInit, OnDestroy {
  @Input() mode: MapMode = 'vehicles';

  private availableIcon = L.icon({
    iconUrl: 'vehicle-available.png',
    iconSize: [24, 24],
    iconAnchor: [16, 32]
  });

  private takenIcon = L.icon({
    iconUrl: 'vehicle-taken.png',
    iconSize: [24, 24],
    iconAnchor: [16, 32]
  });

  private refreshIntervalId: any;

  constructor(private http: HttpClient, private mapService: MapService) {}

  private loadActiveVehicles(): void {
    this.http.get<Vehicle[]>('http://localhost:8080/api/public-map/active')
      .subscribe(vehicles => {
        vehicles.forEach(vehicle => {
          const icon = vehicle.available ? this.availableIcon : this.takenIcon;
          const popupText = vehicle.available ? 'Available' : 'Busy';
          this.mapService.addMarker(
            vehicle.vehicleId, 
            vehicle.latitude, 
            vehicle.longitude, 
            icon, 
            popupText);
        });
      });
  }

  ngAfterViewInit(): void {
    this.mapService.initMap('map');
    if (this.mode === 'vehicles') {
      this.loadActiveVehicles();
      this.refreshIntervalId = setInterval(() => this.loadActiveVehicles(), 1000);
    }
  }

  ngOnDestroy(): void {
    if (this.refreshIntervalId) {
      clearInterval(this.refreshIntervalId);
    }
  }
}
