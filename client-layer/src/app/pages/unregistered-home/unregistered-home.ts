import { Component } from '@angular/core';
import { MapComponent } from '../../components/map/map';

@Component({
  selector: 'app-unregistered-home',
  standalone: true,
  imports: [MapComponent],
  templateUrl: './unregistered-home.html',
  styleUrls: ['./unregistered-home.css'],
})
export class UnregisteredHomeComponent {

}
