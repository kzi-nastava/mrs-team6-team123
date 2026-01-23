import { Vehicle } from "./vehicle";

export interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: string;
  userRole: 'PASSENGER' | 'DRIVER' | 'ADMIN';
  hoursActive?: string;
  totalRides?: number;
  rating?: number;
  vehicle?: Vehicle;
  
}

export interface UpdateUserProfileRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: string;
}