export interface DriverRideHistory {
  rideId: number;
  passengers: string[];
  startLocation: string;
  endLocation: string;
  startedAt: string;   // "HH:mm"
  endedAt: string;     // "HH:mm"
  date: string;        // "yyyy-MM-dd"
  price: number;
  panicTriggered: string;
  canceledBy?: string;
  startLat: number;
  startLng: number;
  endLat: number;
  endLng: number;
}