// Validation utility for driver registration form
export interface DriverForm {
  email: string;
  firstName: string;
  lastName: string;
  address: string;
  phone: string;
  vehicleModel: string;
  vehicleType: string;
  licensePlate: string;
  seats: number;
  babyFriendly: boolean;
  petFriendly: boolean;
}

export interface ValidationResult {
  valid: boolean;
  message?: string;
}

const NAME_PATTERN = /^[a-zA-ZÀ-ÿ\s'-]+$/;
const EMAIL_PATTERN = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
const PHONE_PATTERN = /^\+?[0-9]{7,15}$/;
const LICENSE_PLATE_PATTERN = /^[A-Z0-9\s-]+$/i;
const MIN_NAME_LENGTH = 2;
const MAX_NAME_LENGTH = 50;
const MIN_ADDRESS_LENGTH = 5;
const MAX_ADDRESS_LENGTH = 50;
const MIN_VEHICLE_MODEL_LENGTH = 2;
const MAX_VEHICLE_MODEL_LENGTH = 50;
const MIN_LICENSE_PLATE_LENGTH = 2;
const MAX_LICENSE_PLATE_LENGTH = 20;
const MIN_SEATS = 1;
const MAX_SEATS = 15;

export function validateDriverForm(f: DriverForm): ValidationResult {
  if (!f.firstName.trim() || !f.lastName.trim() || !f.email.trim() || !f.address.trim() || !f.phone.trim() || !f.vehicleModel.trim() || !f.licensePlate.trim()) {
    return { valid: false, message: 'All fields are required' };
  }
  if (f.firstName.trim().length < MIN_NAME_LENGTH) {
    return { valid: false, message: `First name must be at least ${MIN_NAME_LENGTH} characters long` };
  }
  if (f.firstName.trim().length > MAX_NAME_LENGTH) {
    return { valid: false, message: `First name must not exceed ${MAX_NAME_LENGTH} characters` };
  }
  if (!NAME_PATTERN.test(f.firstName.trim())) {
    return { valid: false, message: 'First name can only contain letters, spaces, hyphens, and apostrophes' };
  }
  if (f.lastName.trim().length < MIN_NAME_LENGTH) {
    return { valid: false, message: `Last name must be at least ${MIN_NAME_LENGTH} characters long` };
  }
  if (f.lastName.trim().length > MAX_NAME_LENGTH) {
    return { valid: false, message: `Last name must not exceed ${MAX_NAME_LENGTH} characters` };
  }
  if (!NAME_PATTERN.test(f.lastName.trim())) {
    return { valid: false, message: 'Last name can only contain letters, spaces, hyphens, and apostrophes' };
  }
  if (!EMAIL_PATTERN.test(f.email.trim())) {
    return { valid: false, message: 'Invalid email format' };
  }
  if (!PHONE_PATTERN.test(f.phone.trim())) {
    return { valid: false, message: 'Invalid phone number format. Must be 7-15 digits, optionally starting with +' };
  }
  if (f.address.trim().length < MIN_ADDRESS_LENGTH) {
    return { valid: false, message: `Address must be at least ${MIN_ADDRESS_LENGTH} characters long` };
  }
  if (f.address.trim().length > MAX_ADDRESS_LENGTH) {
    return { valid: false, message: `Address must not exceed ${MAX_ADDRESS_LENGTH} characters` };
  }
  if (f.vehicleModel.trim().length < MIN_VEHICLE_MODEL_LENGTH) {
    return { valid: false, message: `Vehicle model must be at least ${MIN_VEHICLE_MODEL_LENGTH} characters long` };
  }
  if (f.vehicleModel.trim().length > MAX_VEHICLE_MODEL_LENGTH) {
    return { valid: false, message: `Vehicle model must not exceed ${MAX_VEHICLE_MODEL_LENGTH} characters` };
  }
  if (!f.vehicleType) {
    return { valid: false, message: 'Vehicle type is required' };
  }
  if (f.licensePlate.trim().length < MIN_LICENSE_PLATE_LENGTH) {
    return { valid: false, message: `License plate must be at least ${MIN_LICENSE_PLATE_LENGTH} characters long` };
  }
  if (f.licensePlate.trim().length > MAX_LICENSE_PLATE_LENGTH) {
    return { valid: false, message: `License plate must not exceed ${MAX_LICENSE_PLATE_LENGTH} characters` };
  }
  if (!LICENSE_PLATE_PATTERN.test(f.licensePlate.trim())) {
    return { valid: false, message: 'Invalid license plate format. Can only contain letters, numbers and spaces' };
  }
  if (f.seats < MIN_SEATS) {
    return { valid: false, message: `Number of seats must be at least ${MIN_SEATS}` };
  }
  if (f.seats > MAX_SEATS) {
    return { valid: false, message: `Number of seats must not exceed ${MAX_SEATS}` };
  }
  return { valid: true };
}
