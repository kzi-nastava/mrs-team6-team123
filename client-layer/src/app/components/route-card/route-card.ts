import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-route-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './route-card.html',
  styleUrls: ['./route-card.css']
})
export class RouteCardComponent {
  @Input() data!: {
    id: number;
    routeId: number;
    title: string;
    duration: string;
    bookText: string;
    deleteText: string;
  };

  @Output() book = new EventEmitter<void>();
  @Output() delete = new EventEmitter<void>();

  bookFavorite() {
    this.book.emit();
  }

  deleteFavorite() {
    this.delete.emit();
  }
}