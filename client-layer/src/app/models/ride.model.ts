import { RideStatus, VehicleType } from "./enums";

export interface RideResponse {
  rideId: number;
  driverId: number;
  driverName: string;
  vehicleLicense: string;
  status: RideStatus;
  estimatedTimeMinutes: number;
  estimatedPrice: number;
}

export interface RideOrderRequest {
  creatorId: number;
  passengerIds?: number[];
  startLocation: string;
  endLocation: string;
  startLatitude: number;
  startLongitude: number;
  endLatitude: number;
  endLongitude: number;
  scheduledAt?: string; // ISO date string, null for immediate
  babySeat: boolean;
  petFriendly: boolean;
  vehicleType: VehicleType;
  waypoints?: string[];
  estimatedPrice?: number;
}

export interface CancelRideRequest {
  userId: number;
  reason: string;
}

export interface CancelRideResponse {
  rideId: number;
  cancelledBy: number;
  reason: string;
  message: string;
}

export interface StopRideRequest {
  currentLocation: string;
  stoppedAt: string;
}

export interface StopRideResponse {
  rideId: number;
  stoppedLocation: string;
  stoppedAt: string;
  recalculatedPrice: number;
  message: string;
}
