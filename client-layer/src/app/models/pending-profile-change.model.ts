export interface PendingProfileChange {
  id: number;
  driverId: number;
  driverName: string;
  driverEmail: string;
  firstNameOld: string;
  firstNameNew: string;
  lastNameOld: string;
  lastNameNew: string;
  phoneOld: string;
  phoneNew: string;
  addressOld: string;
  addressNew: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
}

export interface ProfileChangeResponse {
  changes: {
    [key: string]: {
      oldValue: string;
      newValue: string;
    }
  }
}
