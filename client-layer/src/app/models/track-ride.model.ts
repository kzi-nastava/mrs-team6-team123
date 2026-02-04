import { RideStatus } from "./enums";
import { RideStop } from "./route-stop.model";

export interface RideInfo {
    driver: string;
    startedAt: string;
    from: string;
    to: string;
    price: number;
    duration: number;
    passengers: string[];
    reports: string[];
    status: RideStatus;
}

export interface TrackRideResponse {
    rideId: number;
    driverId: number;
    stops: RideStop[];
    stopsMade: number;
    info: RideInfo;
}