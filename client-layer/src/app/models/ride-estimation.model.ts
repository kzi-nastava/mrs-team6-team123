import { VehicleType } from "./enums";

export interface RideEstimationRequest {
  startLocation: string;
  endLocation: string;
  intermediateStops?: string[];
  vehicleType?: VehicleType;
}

export interface RideEstimationResponse {
  startLocation: string;
  endLocation: string;
  estimatedDistance: number;
  estimatedTime: number;
  estimatedPrice: number;
  route: string;
}