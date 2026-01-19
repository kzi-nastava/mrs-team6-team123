// Auth Models
export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: number;
  email: string;
  role: 'PASSENGER' | 'DRIVER' | 'ADMIN';
}

export interface RegistrationRequest {
  email: string;
  password: string;
  confirmPassword: string;
  firstName: string;
  lastName: string;
  address: string;
  phoneNumber: string;
  profilePicture?: string;
}

export interface RegistrationResponse {
  message: string;
  userId: number;
  email: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
  confirmPassword: string;
}

// User Profile Models
export interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
}

export interface UpdateUserProfileRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
}

// Ride Estimation Models
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

// Ride Order Models
export interface RideOrderRequest {
  creatorId: number;
  passengerIds?: number[];
  startLocation: string;
  endLocation: string;
  scheduledAt?: string; // ISO date string, null for immediate
  babySeat: boolean;
  petFriendly: boolean;
  vehicleType: VehicleType;
  waypoints?: string[];
}

export interface RideResponse {
  rideId: number;
  driverId: number;
  status: RideStatus;
  estimatedTimeMinutes: number;
  estimatedPrice: number;
}

// Ride Tracking
export interface RideTrackingResponse {
  rideId: number;
  currentLocation: string;
  nextStop: string;
  timeLeft: number;
}

// Ride Rating
export interface RideRatingRequest {
  rideId: number;
  driverRating: number;
  vehicleRating: number;
}

export interface RideRatingResponse {
  rideId: number;
  driverId: number;
  driverRating: number;
  vehicleId: number;
  vehicleRating: number;
}

// Cancel Ride
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

// Stop Ride
export interface StopRideRequest {
  currentLocation: string;
  stoppedAt: string; // ISO date string
}

export interface StopRideResponse {
  rideId: number;
  stoppedLocation: string;
  stoppedAt: string;
  recalculatedPrice: number;
  message: string;
}

// Driver Models
export interface DriverRegistrationRequest {
  firstName: string;
  lastName: string;
  email: string;
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

export interface ReportDriverRequest {
  rideId: number;
  driverId: number;
  comment: string;
}

export interface ReportDriverResponse {
  rideId: number;
  driverId: number;
  vehicleId: number;
  comment: string;
}

// Driver Ride History
export interface DriverRideHistory {
  rideId: number;
  passengerIds: number[];
  startLocation: string;
  endLocation: string;
  startedAt: string;
  endedAt: string;
  price: number;
  panicTriggered: boolean;
  canceledByUserId?: number;
}

// Admin Ride History
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

// Public Map
export interface ActiveVehicle {
  vehicleId: number;
  location: string;
  available: boolean;
}

// Enums
export type VehicleType = 'STANDARD' | 'VAN' | 'LUXURY';
export type RideStatus = 'CREATED' | 'ACCEPTED' | 'STARTED' | 'FINISHED';
export type DriverStatus = 'PENDING_APPROVAL' | 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
export type UserRole = 'PASSENGER' | 'DRIVER' | 'ADMIN';