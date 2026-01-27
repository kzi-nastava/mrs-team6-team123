import { Component, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Subscription } from 'rxjs';
import { GeocodeHit } from '../../../services/graphhopper.service';
import { LocationAutocompleteService } from '../../../services/location-autocomplete.service';

@Component({
  selector: 'app-location-input',
  standalone: true,
  imports: [CommonModule, FormsModule, MatInputModule, MatFormFieldModule],
  templateUrl: './location-input.component.html',
  styleUrl: './location-input.component.css'
})
export class LocationInputComponent implements OnDestroy {
  @Input() label = 'Location';
  @Input() placeholder = 'Enter address';
  @Input() address = '';
  
  @Output() addressChange = new EventEmitter<string>();
  @Output() locationSelected = new EventEmitter<GeocodeHit>();
  @Output() inputChanged = new EventEmitter<string>();

  suggestions: GeocodeHit[] = [];
  private subscription?: Subscription;
  private debouncedSearch: any;

  constructor(private autocompleteService: LocationAutocompleteService) {
    this.debouncedSearch = this.autocompleteService.createDebouncedSearch();
    this.subscription = this.debouncedSearch.results$.subscribe((hits: GeocodeHit[]) => {
      this.suggestions = hits;
    });
  }

  onInput(event: any) {
    const query = event.target.value;
    this.address = query;
    this.addressChange.emit(query);
    this.inputChanged.emit(query);

    if (query.length < 3) {
      this.suggestions = [];
      return;
    }

    this.debouncedSearch.input$.next(query);
  }

  selectLocation(hit: GeocodeHit) {
    this.address = hit.name;
    this.addressChange.emit(hit.name);
    this.suggestions = [];
    this.locationSelected.emit(hit);
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }
}
