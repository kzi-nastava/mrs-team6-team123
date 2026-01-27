import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, filter } from 'rxjs/operators';
import { GraphhopperService, GeocodeHit } from './graphhopper.service';

@Injectable({
  providedIn: 'root'
})
export class LocationAutocompleteService {
  constructor(private graphhopper: GraphhopperService) {}

  createDebouncedSearch(debounceMs: number = 400): {
    input$: Subject<string>;
    results$: Observable<GeocodeHit[]>;
  } {
    const input$ = new Subject<string>();
    const results$ = input$.pipe(
      debounceTime(debounceMs),
      distinctUntilChanged(),
      filter(query => query.length >= 3),
      switchMap(query => this.graphhopper.geocode(query))
    );

    return { input$, results$ };
  }
}
