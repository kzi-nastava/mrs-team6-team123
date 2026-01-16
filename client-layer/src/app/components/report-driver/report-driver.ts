import { Component, Input } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-report-driver',
  standalone: true,
  imports: [],
  templateUrl: './report-driver.html',
  styleUrls: ['./report-driver.css'],
})
export class ReportDriverComponent {
  //@Input data = {};

  data = {
    "driver": "John Doe",
    "vehicle": "Peugeout AA123TX"
  }
  comment = '';

  constructor(public dialogRef: MatDialogRef<ReportDriverComponent>) {}

  close() {
    this.dialogRef.close();
  }

  submit() {
    this.dialogRef.close({ comment: this.comment });
  }
  
}
