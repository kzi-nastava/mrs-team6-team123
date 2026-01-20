export interface DriverRideHistory {
  rideId: number;
  passengerIds: number[];
  startLocation: string;
  endLocation: string;
  startedAt: string;   // "HH:mm"
  endedAt: string;     // "HH:mm"
  date: string;        // "yyyy-MM-dd"
  price: number;
  panicTriggered: boolean;
  canceledByUserId?: number;
  routeId: number;
}