import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-ride-history-filter',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './ride-history-filter.html',
  styleUrls: ['./ride-history-filter.css'],
})
export class RideHistoryFilterComponent {
  fromDate: string = '';
  toDate: string = '';

  @Output() filterApplied = new EventEmitter<{ fromDate: string, toDate: string }>();
  @Output() filterCleared = new EventEmitter<void>();

  applyFilter(): void {
    this.filterApplied.emit({ fromDate: this.fromDate, toDate: this.toDate });
  }

  clearFilter(): void {
    this.fromDate = '';
    this.toDate = '';
    this.filterCleared.emit();
  }
}
