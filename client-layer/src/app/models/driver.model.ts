import { DriverStatus, VehicleType } from "./enums";

export interface DriverRegistrationRequest {
  firstName: string;
  lastName: string;
  email: string;
  address: string;
  phone: string;
  vehicleModel: string;
  vehicleType: VehicleType;
  licensePlate: string;
  seats: number;
  babyTransport: boolean;
  petTransport: boolean;
}

export interface DriverResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  vehicleModel: string;
  licensePlate: string;
  status: DriverStatus;
}