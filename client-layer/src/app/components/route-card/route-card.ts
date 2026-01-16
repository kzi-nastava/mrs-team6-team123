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
    title: string;
    duration: string;
    bookText: string;
    deleteText: string;
  };

  //when book button pressed we need pop up a booking confirmation

  bookFavorite() {
    console.log('Book ride clicked');
    alert('Ride booked successfully!');
    this.book.emit();
  }

  deleteFavorite() {
    console.log('Delete favorite clicked');
    alert('Favorite route deleted.');
    this.delete.emit();
  }

  @Output() book = new EventEmitter<void>();
  @Output() delete = new EventEmitter<void>();
}