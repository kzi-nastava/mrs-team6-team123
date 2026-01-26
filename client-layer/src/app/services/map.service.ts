import { ChangeDetectorRef, Injectable, NgZone } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import { RideStop, TrackRideResponse } from '../models/track-ride.model';
import { GraphhopperService } from './graphhopper.service';

declare module 'leaflet' {
  namespace Routing {
    function control(options: any): any;
  }
}

@Injectable({ providedIn: 'root' })
export class MapService {
  private map!: L.Map;

  private routePoints: L.LatLng[] = [];
  private routeLine?: L.Polyline;
  private simulationInterval?: any;
  private currentRouteIndex = 0;

  private vehicleMarkers = new Map<number, L.Marker>();

  private defaultTaxiIcon = L.icon({
    iconUrl: 'taxi.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32]
  });

  private defaulLocationIcon = L.icon({
    iconUrl: 'end-icon.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32]
  });

  constructor(
    private graphhopperService: GraphhopperService,
    private ngZone: NgZone  
  ) {}

  initMap(elementId: string, center: L.LatLngExpression = [45.2396, 19.8227], zoom = 13) {
    this.map = L.map(elementId, { center, zoom });
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '&copy; OpenStreetMap'
    }).addTo(this.map);

    setTimeout(() => this.map.invalidateSize(), 100);
  }

  getMap() {
    return this.map;
  }

  addMarker(vehicleId: number, latitude: number, longitude: number, icon?: L.Icon, popupText?: string) {
    const position: L.LatLngExpression = [latitude, longitude];

    if (this.vehicleMarkers.has(vehicleId)) {
      const marker = this.vehicleMarkers.get(vehicleId)!;
      marker.setLatLng(position);
      if (icon) marker.setIcon(icon);
    } else {
      const marker = L.marker(position, { icon });
      if (popupText) marker.bindPopup(popupText);
      marker.addTo(this.map);
      this.vehicleMarkers.set(vehicleId, marker);
    }
  }

  addRoute(startLat: number, startLng: number, endLat: number, endLng: number) {
    const start = L.latLng(startLat, startLng);
    const end = L.latLng(endLat, endLng);

    const routingControl = L.Routing.control({
      waypoints: [start, end],
      addWaypoints: false,
      draggableWaypoints: false,
      fitSelectedRoutes: true,
      show: false,
      lineOptions: {
        styles: [{ color: '#2abc2f', weight: 5 }]
      },
      createMarker: (i: any, wp: any) => {
        if (i === 0) {
          return L.marker(wp.latLng, {
            icon: L.icon({
              iconUrl: 'start-icon.png',
              iconSize: [32, 32],
              iconAnchor: [16, 32]
            })
          }).bindPopup('Start Location');
        }

        return L.marker(wp.latLng, {
          icon: L.icon({
            iconUrl: 'end-icon.png',
            iconSize: [32, 32],
            iconAnchor: [16, 32]
          })
        }).bindPopup('End Location');
      }
    }).addTo(this.map);

    return routingControl;
  }

  trackRide(ride: TrackRideResponse, cdr: ChangeDetectorRef) {
    this.graphhopperService.getRoute(ride.stops).subscribe({
      next: (res) => {
        const coordinates = res.paths[0].points.coordinates;

        this.routePoints = coordinates.map(
          (c: number[]) => L.latLng(c[1], c[0])
        );

        this.drawRoute();
        this.drawStops(ride.stops);
        this.startSimulation(ride, cdr);
      },
      error: (err) => {
        console.error('GraphHopper error: ', err);
      }
    });
  }

  drawRoute() {
    if (this.routeLine) {
      this.map.removeLayer(this.routeLine);
    }

    this.routeLine = L.polyline(this.routePoints, {
      color: '#2abc2f',
      weight: 5
    }).addTo(this.map);

    this.map.fitBounds(this.routeLine.getBounds());
  }

  drawStops(stops: RideStop[]) {
    stops.forEach((stop, index) => {
      L.marker([stop.latitude, stop.longitude], { icon: this.defaulLocationIcon })
        .bindPopup(stop.location)
        .addTo(this.map);
    });
  }

  startSimulation(ride: TrackRideResponse, cdr: ChangeDetectorRef) {
    if (!this.routePoints.length) return;

    this.currentRouteIndex = 0;

    this.addMarker(
      ride.rideId,
      this.routePoints[0].lat,
      this.routePoints[0].lng,
      this.defaultTaxiIcon
    );

    const totalMinutes = ride.info.duration;
    const totalSteps = this.routePoints.length;
    const stepPerSecond = Math.ceil(totalSteps / totalMinutes);

    let minutesLeft = totalMinutes;

    this.simulationInterval = setInterval(() => {
      minutesLeft--;

      for (let i = 0; i < stepPerSecond; i++) {
        if (this.currentRouteIndex < totalSteps) {
          const p = this.routePoints[this.currentRouteIndex++];
          this.addMarker(ride.rideId, p.lat, p.lng, this.defaultTaxiIcon);
        }
      }

      this.ngZone.run(() => {
        ride.info = { ...ride.info, duration: minutesLeft };
        cdr.markForCheck();
      });

      if (minutesLeft <= 0) {
        clearInterval(this.simulationInterval);
      }

    }, 1000);
  }

  addPolyline(points: L.LatLngExpression[], options?: L.PolylineOptions) {
    return L.polyline(points, options).addTo(this.map);
  }

  fitBoundsOnMarkers() {
    if (this.vehicleMarkers.size > 0) {
      const group = L.featureGroup(Array.from(this.vehicleMarkers.values()));
      this.map.fitBounds(group.getBounds(), { padding: [50, 50] });
    }
  }
}
