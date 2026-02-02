import { Injectable } from '@angular/core';

export interface Stop {
  id: string;
  address: string;
  lat?: number;
  lng?: number;
}

@Injectable({
  providedIn: 'root'
})
export class StopManagementService {
  stops: Stop[] = [];

  addStop() {
    this.stops.push({
      id: Date.now().toString(),
      address: ''
    });
  }

  removeStop(id: string) {
    this.stops = this.stops.filter(stop => stop.id !== id);
  }

  updateStopLocation(stopId: string, lat: number, lng: number) {
    const stop = this.stops.find(s => s.id === stopId);
    if (stop) {
      stop.lat = lat;
      stop.lng = lng;
    }
  }

  clearStopLocation(stopId: string) {
    const stop = this.stops.find(s => s.id === stopId);
    if (stop) {
      stop.lat = stop.lng = undefined;
    }
  }

  getValidStops() {
    return this.stops.filter(s => s.lat !== undefined && s.lng !== undefined);
  }

  clear() {
    this.stops = [];
  }
}
