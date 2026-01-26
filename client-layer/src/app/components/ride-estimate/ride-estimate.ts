import { Component, Inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../enviroment';

export enum VehicleType {
  STANDARD = 'STANDARD',
  LUXURY = 'LUXURY',
  VAN = 'VAN'
}

interface RideEstimationRequest {
  startLocation: string;
  endLocation: string;
  intermediateStops?: string[];
  vehicleType: VehicleType;
}

interface RideEstimationResponse {
  startLocation: string;
  endLocation: string;
  estimatedDistance: number;
  estimatedTime: number;
  estimatedPrice: number;
  route: string;
}

@Component({
  selector: 'app-ride-estimate-modal',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './ride-estimate.html',
  styleUrls: ['./ride-estimate.css'],
})
export class RideEstimateModalComponent {
  startLocation = '';
  destination = '';
  vehicleType: VehicleType = VehicleType.STANDARD;
  
  showResult = false;
  loading = false;
  errorMessage = '';
  
  estimatedDistance = 0;
  estimatedTime = 0;
  estimatedPrice = 0;

  vehicleTypes = [
    { value: VehicleType.STANDARD, label: 'Standard', basePrice: 300 },
    { value: VehicleType.LUXURY, label: 'Luxury', basePrice: 500 },
    { value: VehicleType.VAN, label: 'Van', basePrice: 400 }
  ];

  constructor(
    public dialogRef: MatDialogRef<RideEstimateModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  calculateEstimate() {
    if (!this.startLocation || !this.destination) {
      this.errorMessage = 'Please enter both starting location and destination';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const request: RideEstimationRequest = {
      startLocation: this.startLocation,
      endLocation: this.destination,
      vehicleType: this.vehicleType
    };

    console.log('🚀 Sending request:', request);
    console.log('🔗 URL:', `${environment.apiUrl}/api/ride-estimation`);

    this.http.post<RideEstimationResponse>(
      `${environment.apiUrl}/api/ride-estimation`,
      request
    ).subscribe({
      next: (response) => {
        console.log('✅ SUCCESS - Full response:', response);
        this.estimatedDistance = response.estimatedDistance;
        this.estimatedTime = response.estimatedTime;
        this.estimatedPrice = response.estimatedPrice;
        this.loading = false;
        this.showResult = true;
        
        // FORCE CHANGE DETECTION
        this.cdr.detectChanges();
        
        console.log('📊 State after success:', {
          loading: this.loading,
          showResult: this.showResult,
          distance: this.estimatedDistance,
          time: this.estimatedTime,
          price: this.estimatedPrice
        });
      },
      error: (error) => {
        console.error('❌ ERROR - Full error object:', error);
        console.error('❌ Error status:', error.status);
        console.error('❌ Error message:', error.error);
        this.errorMessage = error.error?.message || error.error || 'Failed to calculate estimate. Please check your coordinates.';
        this.loading = false;
        this.cdr.detectChanges(); // FORCE CHANGE DETECTION
      }
    });
  }

  onClose(): void {
    this.dialogRef.close();
  }

  onBookRide(): void {
    this.dialogRef.close({
      action: 'book',
      startLocation: this.startLocation,
      destination: this.destination,
      vehicleType: this.vehicleType,
      estimatedDistance: this.estimatedDistance,
      estimatedTime: this.estimatedTime,
      estimatedPrice: this.estimatedPrice
    });
  }
}