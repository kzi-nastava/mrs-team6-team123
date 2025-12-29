import { Component, Input } from '@angular/core';
import { ActiveRidesCardComponent } from '../../components/active-rides-card/active-rides-card';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [ActiveRidesCardComponent, CommonModule],
  templateUrl: './admin-home.html',
  styleUrls: ['./admin-home.css'],
})
export class AdminHomeComponent {
  @Input() rides: any[] = [];

  /*
  rides = [
    {
      id: 'ride1',
      from: 'Belgrade',
      to: 'Novi Sad',
      nextStop: 'Zemun',
      timeLeft: '15 min',
      driverName: 'Marko Markovic',
      startedAt: '10:30 AM',
      price: 1200,
      passengers: ['Jovana J.', 'Ana A.', 'Petar P.']
    },
    {
      id: 'ride2',
      from: 'Nis',
      to: 'Kragujevac',
      nextStop: 'Cuprija',
      timeLeft: '25 min',
      driverName: 'Ivan Ivanovic',
      startedAt: '11:00 AM',
      price: 900,
      passengers: ['Milan M.', 'Sara S.']
    },
    {
      id: 'ride3',
      from: 'Subotica',
      to: 'Novi Sad',
      nextStop: 'Backa Topola',
      timeLeft: '40 min',
      driverName: 'Petar Petrovic',
      startedAt: '09:45 AM',
      price: 1500,
      passengers: ['Ana A.', 'Luka L.', 'Jelena J.']
    }
  ];
*/
}
