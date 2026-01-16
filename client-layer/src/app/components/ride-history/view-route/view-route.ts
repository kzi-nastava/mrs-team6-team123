import { Component } from '@angular/core';
import { MapComponent } from '../../map/map';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-view-route',
  standalone: true,
  imports: [MapComponent],
  templateUrl: './view-route.html',
  styleUrls: ['./view-route.css'],
})
export class ViewRouteComponent {
  constructor(public dialogRef: MatDialogRef<ViewRouteComponent>) {}
  
  onClose() {
    this.dialogRef.close();
  }
}
