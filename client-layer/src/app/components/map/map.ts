import { Component, AfterViewInit, OnDestroy, Input } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import * as L from 'leaflet';
import { MapService } from '../../services/map.service';
import { DriverRideHistory } from '../../models/driver-ride-history.model';
import { ActiveVehicle } from '../../models/active-vehicle.model';
import { MapMode } from '../../models/enums';

@Component({
  selector: 'app-map',
  standalone: true,
  templateUrl: './map.html',
  styleUrls: ['./map.css'],
})
export class MapComponent implements AfterViewInit, OnDestroy {
  @Input() mode: MapMode = 'VEHICLES';
  @Input() ride?: DriverRideHistory;

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
    this.http.get<ActiveVehicle[]>('http://localhost:8080/api/public-map/active')
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

  private loadRoute(): void {
    if (this.ride) {
      console.log(this.ride);
      this.mapService.addRoute(this.ride.startLat, this.ride.startLng, this.ride.endLat, this.ride.endLng);
      this.mapService.fitBoundsOnMarkers();
    }
  }

  ngAfterViewInit(): void {
    this.mapService.initMap('map');
    if (this.mode === 'VEHICLES') {
      this.loadActiveVehicles();
      this.refreshIntervalId = setInterval(() => this.loadActiveVehicles(), 1000);
    } else if (this.mode === 'STATIC_ROUTE') {
      console.log(this.ride);
      this.loadRoute();
    }
  }

  ngOnDestroy(): void {
    if (this.refreshIntervalId) {
      clearInterval(this.refreshIntervalId);
    }
  }
}
