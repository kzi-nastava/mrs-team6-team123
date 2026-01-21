import { Injectable } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';

declare module 'leaflet' {
  namespace Routing {
    function control(options: any): any;
  }
}

@Injectable({ providedIn: 'root' })
export class MapService {
  private map!: L.Map;

  private vehicleMarkers = new Map<number, L.Marker>();

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
