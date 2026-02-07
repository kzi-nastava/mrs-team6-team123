import { Component, Input, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule],
  templateUrl: './stat-card.html',
  styleUrls: ['./stat-card.css'],
})
export class StatCardComponent implements AfterViewInit {
  @Input() icon: string = 'analytics';
  @Input() title: string = '';
  @Input() totalLabel: string = '';
  @Input() totalValue: string = '';
  @Input() avgLabel: string = '';
  @Input() avgValue: string = '';
  @Input() chartData: any[] = [];
  @Input() dataKey: 'rides' | 'km' | 'spent' = 'rides';

  @ViewChild('chartContainer') chartContainer!: ElementRef;

  ngAfterViewInit(): void {
    // Scroll to the end (most recent data) after view is initialized
    if (this.chartContainer) {
      setTimeout(() => {
        const container = this.chartContainer.nativeElement;
        container.scrollLeft = container.scrollWidth;
      }, 0);
    }
  }

  onWheel(event: WheelEvent): void {
    // Convert vertical scroll to horizontal scroll
    const container = this.chartContainer.nativeElement;
    container.scrollLeft += event.deltaY;
    event.preventDefault();
  }

  getBarHeight(value: number): number {
    const maxValue = Math.max(...this.chartData.map(d => d[this.dataKey] || 0));
    return maxValue > 0 ? (value / maxValue) * 100 : 0;
  }

  getBarTitle(item: any): string {
    return `${item[this.dataKey]} ${this.dataKey} on ${item.date}`;
  }
}
