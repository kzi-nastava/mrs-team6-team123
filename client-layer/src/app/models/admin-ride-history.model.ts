import { VehicleType } from "./enums";

export interface AdminRideHistory {
  rideId: number;
  driverId: number;
  creatorId: number;
  passengerIds: number[];
  startLocation: string;
  endLocation: string;
  startedAt: string;
  endedAt: string;
  price: number;
  totalDistance: number;
  panicTriggered: boolean;
  canceledByUserId?: number;
  cancelReason?: string;
  route: string;
  inconsistencyReports?: string[];
  driverRating?: number;
  vehicleRating?: number;
  driverName: string;
  vehicleType: VehicleType;
}