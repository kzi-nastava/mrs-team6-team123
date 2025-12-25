import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-active-rides-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './active-rides-card.html',
  styleUrls: ['./active-rides-card.css'],
})
export class ActiveRidesCardComponent {
  @Input() data!: {
    driver: string;
    from: string;
    to: string;
  };
}
