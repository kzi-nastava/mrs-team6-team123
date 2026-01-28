// ride-estimate.ts

import { Component, Inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../enviroment';
import { GeocodingService, GeocodingResult } from '../../services/geocoding.service';
import { debounceTime, distinctUntilChanged, Subject, switchMap } from 'rxjs';

export enum VehicleType {
  STANDARD = 'STANDARD',
  LUXURY = 'LUXURY',
  VAN = 'VAN'
}

interface IntermediateStop {
  id: string;
  address: string;
  coordinates: string;
  suggestions: GeocodingResult[];
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
    MatProgressSpinnerModule,
    MatIconModule,
    MatAutocompleteModule
  ],
  templateUrl: './ride-estimate.html',
  styleUrls: ['./ride-estimate.css'],
})
export class RideEstimateModalComponent {
  // Adrese (za prikaz korisniku)
  startAddress = '';
  destinationAddress = '';
  
  // Koordinate (za slanje na backend)
  startCoordinates = '';
  destinationCoordinates = '';
  
  vehicleType: VehicleType = VehicleType.STANDARD;
  
  // Međustanice
  intermediateStops: IntermediateStop[] = [];
  
  // Autocomplete suggestions
  startSuggestions: GeocodingResult[] = [];
  destinationSuggestions: GeocodingResult[] = [];
  
  showResult = false;
  loading = false;
  geocodingLoading = false;
  errorMessage = '';
  
  estimatedDistance = 0;
  estimatedTime = 0;
  estimatedPrice = 0;

  // Za prikaz u rezultatu
  resolvedStartAddress = '';
  resolvedDestinationAddress = '';

  vehicleTypes = [
    { value: VehicleType.STANDARD, label: 'Standard', basePrice: 300 },
    { value: VehicleType.LUXURY, label: 'Luxury', basePrice: 500 },
    { value: VehicleType.VAN, label: 'Van', basePrice: 400 }
  ];

  // Debounce za autocomplete
  private startSearch$ = new Subject<string>();
  private destinationSearch$ = new Subject<string>();

  constructor(
    public dialogRef: MatDialogRef<RideEstimateModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private geocodingService: GeocodingService
  ) {
    this.setupAutocomplete();
  }

  /**
   * Postavlja autocomplete sa debounce
   */
  private setupAutocomplete() {
    this.startSearch$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => this.geocodingService.searchAddress(query))
    ).subscribe(results => {
      this.startSuggestions = results;
      this.cdr.detectChanges();
    });

    this.destinationSearch$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => this.geocodingService.searchAddress(query))
    ).subscribe(results => {
      this.destinationSuggestions = results;
      this.cdr.detectChanges();
    });
  }

  /**
   * Poziva se kad korisnik kuca u start polje
   */
  onStartAddressInput(value: string) {
    this.startAddress = value;
    this.startCoordinates = ''; // Reset koordinate dok ne izabere
    if (value.length >= 3) {
      this.startSearch$.next(value);
    } else {
      this.startSuggestions = [];
    }
  }

  /**
   * Poziva se kad korisnik izabere start adresu iz liste
   */
  selectStartAddress(result: GeocodingResult) {
    this.startAddress = result.displayName;
    this.startCoordinates = `${result.latitude}, ${result.longitude}`;
    this.resolvedStartAddress = result.displayName;
    this.startSuggestions = [];
  }

  /**
   * Poziva se kad korisnik kuca u destination polje
   */
  onDestinationAddressInput(value: string) {
    this.destinationAddress = value;
    this.destinationCoordinates = ''; // Reset koordinate dok ne izabere
    if (value.length >= 3) {
      this.destinationSearch$.next(value);
    } else {
      this.destinationSuggestions = [];
    }
  }

  /**
   * Poziva se kad korisnik izabere destination adresu iz liste
   */
  selectDestinationAddress(result: GeocodingResult) {
    this.destinationAddress = result.displayName;
    this.destinationCoordinates = `${result.latitude}, ${result.longitude}`;
    this.resolvedDestinationAddress = result.displayName;
    this.destinationSuggestions = [];
  }

  /**
   * Dodaje novu međustanicu
   */
  addStop() {
    const newStop: IntermediateStop = {
      id: Date.now().toString(),
      address: '',
      coordinates: '',
      suggestions: []
    };
    this.intermediateStops.push(newStop);
  }

  /**
   * Uklanja međustanicu po ID-u
   */
  removeStop(stopId: string) {
    this.intermediateStops = this.intermediateStops.filter(stop => stop.id !== stopId);
  }

  /**
   * Poziva se kad korisnik kuca u stop polje
   */
  onStopAddressInput(stop: IntermediateStop, value: string) {
    stop.address = value;
    stop.coordinates = ''; // Reset dok ne izabere
    
    if (value.length >= 3) {
      this.geocodingService.searchAddress(value).subscribe(results => {
        stop.suggestions = results;
        this.cdr.detectChanges();
      });
    } else {
      stop.suggestions = [];
    }
  }

  /**
   * Poziva se kad korisnik izabere stop adresu iz liste
   */
  selectStopAddress(stop: IntermediateStop, result: GeocodingResult) {
    stop.address = result.displayName;
    stop.coordinates = `${result.latitude}, ${result.longitude}`;
    stop.suggestions = [];
  }

  /**
   * Pomera međustanicu gore
   */
  moveStopUp(index: number) {
    if (index > 0) {
      const temp = this.intermediateStops[index];
      this.intermediateStops[index] = this.intermediateStops[index - 1];
      this.intermediateStops[index - 1] = temp;
    }
  }

  /**
   * Pomera međustanicu dole
   */
  moveStopDown(index: number) {
    if (index < this.intermediateStops.length - 1) {
      const temp = this.intermediateStops[index];
      this.intermediateStops[index] = this.intermediateStops[index + 1];
      this.intermediateStops[index + 1] = temp;
    }
  }

  /**
   * Skraćuje dugačke adrese za prikaz
   */
  shortenAddress(address: string, maxLength: number = 50): string {
    if (address.length <= maxLength) return address;
    return address.substring(0, maxLength) + '...';
  }

  /**
   * Računa procenu vožnje
   */
  async calculateEstimate() {
    this.errorMessage = '';

    // Validacija
    if (!this.startAddress.trim()) {
      this.errorMessage = 'Please enter starting location';
      return;
    }
    
    if (!this.destinationAddress.trim()) {
      this.errorMessage = 'Please enter destination';
      return;
    }

    this.loading = true;
    this.geocodingLoading = true;

    try {
      // Geocoduj start ako nema koordinate
      if (!this.startCoordinates) {
        const startResult = await this.geocodingService.geocodeAddress(this.startAddress).toPromise();
        if (startResult) {
          this.startCoordinates = `${startResult.latitude}, ${startResult.longitude}`;
          this.resolvedStartAddress = startResult.displayName;
        } else {
          this.errorMessage = 'Could not find starting location. Please try a different address.';
          this.loading = false;
          this.geocodingLoading = false;
          return;
        }
      }

      // Geocoduj destination ako nema koordinate
      if (!this.destinationCoordinates) {
        const destResult = await this.geocodingService.geocodeAddress(this.destinationAddress).toPromise();
        if (destResult) {
          this.destinationCoordinates = `${destResult.latitude}, ${destResult.longitude}`;
          this.resolvedDestinationAddress = destResult.displayName;
        } else {
          this.errorMessage = 'Could not find destination. Please try a different address.';
          this.loading = false;
          this.geocodingLoading = false;
          return;
        }
      }

      // Geocoduj međustanice
      const validStops: string[] = [];
      for (const stop of this.intermediateStops) {
        if (stop.address.trim()) {
          if (!stop.coordinates) {
            const stopResult = await this.geocodingService.geocodeAddress(stop.address).toPromise();
            if (stopResult) {
              stop.coordinates = `${stopResult.latitude}, ${stopResult.longitude}`;
              validStops.push(stop.coordinates);
            }
            // Ako ne nađe, preskačemo tu stanicu
          } else {
            validStops.push(stop.coordinates);
          }
        }
      }

      this.geocodingLoading = false;

      // Sada šaljemo request sa koordinatama
      const request: RideEstimationRequest = {
        startLocation: this.startCoordinates,
        endLocation: this.destinationCoordinates,
        intermediateStops: validStops.length > 0 ? validStops : undefined,
        vehicleType: this.vehicleType
      };

      console.log('🚀 Sending estimation request:', request);

      this.http.post<RideEstimationResponse>(
        `${environment.apiUrl}/api/ride-estimation`,
        request
      ).subscribe({
        next: (response) => {
          console.log('✅ Estimation response:', response);
          this.estimatedDistance = response.estimatedDistance;
          this.estimatedTime = response.estimatedTime;
          this.estimatedPrice = response.estimatedPrice;
          this.loading = false;
          this.showResult = true;
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('❌ Estimation error:', error);
          this.errorMessage = error.error?.message || error.error || 'Failed to calculate estimate.';
          this.loading = false;
          this.cdr.detectChanges();
        }
      });

    } catch (error) {
      console.error('Geocoding failed:', error);
      this.errorMessage = 'Failed to process addresses. Please try again.';
      this.loading = false;
      this.geocodingLoading = false;
    }
  }

  /**
   * Resetuje formu za novu procenu
   */
  resetForm() {
    this.showResult = false;
    this.errorMessage = '';
  }

  onClose(): void {
    this.dialogRef.close();
  }

  onBookRide(): void {
    const validStops = this.intermediateStops
      .filter(stop => stop.coordinates)
      .map(stop => ({
        address: stop.address,
        coordinates: stop.coordinates
      }));

    this.dialogRef.close({
      action: 'book',
      startAddress: this.resolvedStartAddress || this.startAddress,
      startCoordinates: this.startCoordinates,
      destinationAddress: this.resolvedDestinationAddress || this.destinationAddress,
      destinationCoordinates: this.destinationCoordinates,
      intermediateStops: validStops,
      vehicleType: this.vehicleType,
      estimatedDistance: this.estimatedDistance,
      estimatedTime: this.estimatedTime,
      estimatedPrice: this.estimatedPrice
    });
  }
}