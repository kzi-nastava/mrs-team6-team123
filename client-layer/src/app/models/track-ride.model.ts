export interface RideStop {
    latitude: number;
    longitude: number;
    location: string;
}

export interface RideInfo {
    driver: string;
    startedAt: string;
    from: string;
    to: string;
    price: number;
    timeLeft: number;
    passengers: string[];
    reports: string[];
}

export interface TrackRideResponse {
    rideId: number;
    stops: RideStop[];
    stopsMade: number;
    info: RideInfo;
}