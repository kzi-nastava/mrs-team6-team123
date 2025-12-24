import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouteCardComponent } from '../../components/route-card/route-card';

@Component({
  selector: 'app-user-favorites',
  standalone: true,
  imports: [CommonModule, RouteCardComponent],
  templateUrl: './user-favorites.html',
  styleUrls: ['./user-favorites.css'],
})
export class UserFavoritesComponent {
  favorites = [
    {
      title: 'Belgrade, Novi Sad',
      duration: '87min',
      bookText: 'Book now',
      deleteText: 'Remove'
    },
    {
      title: 'Novi Sad, Budapest',
      duration: '29min',
      bookText: 'Book now',
      deleteText: 'Remove'
    },
    {
      title: 'Zemun, New Belgrade',
      duration: '15min',
      bookText: 'Book now',
      deleteText: 'Remove'
    },
    {
      title: 'Belgrade, Nis, Sofia',
      duration: '45min',
      bookText: 'Book now',
      deleteText: 'Remove'
    },
    {
      title: 'Novi Sad, Subotica, Szeged, Budapest',
      duration: '38min',
      bookText: 'Book now',
      deleteText: 'Remove'
    }
  ];
}
