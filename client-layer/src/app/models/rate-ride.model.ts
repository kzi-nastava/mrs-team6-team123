export interface RateRideRequest {
    rideId: number;
    driverId: number;
    vehicleId: number;
    driver: string;
    licencePlate: string;
}

export interface RateRideResponse {
    rideId: number;
    driverId: number;
    vehicleId: number;
    driverRating: number;
    vehicleRating: number;
    comment: string;
    authorId: number;
}