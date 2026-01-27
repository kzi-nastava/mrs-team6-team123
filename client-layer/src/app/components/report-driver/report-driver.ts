import { Component, Inject, Input } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TrackRideResponse } from '../../models/track-ride.model';
import { FormsModule } from '@angular/forms';
import { ReportRequest } from '../../models/report.model';
import { ReportService } from '../../services/report.service';

@Component({
  selector: 'app-report-driver',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './report-driver.html',
  styleUrls: ['./report-driver.css'],
})
export class ReportDriverComponent {
  comment = '';

  constructor (
    public dialogRef: MatDialogRef<ReportDriverComponent>,
    @Inject(MAT_DIALOG_DATA) public ride: TrackRideResponse,
    public service: ReportService
  ) {}

  close() {
    this.dialogRef.close();
  }

  submit() {
    const reportRequest: ReportRequest = {
      rideId: this.ride.rideId,
      authorId: 2,
      comment: this.comment
    }
    this.sendReport(reportRequest);
    this.dialogRef.close(reportRequest);
  }

  private sendReport(report: ReportRequest) {
    this.service.reportDriver(report).subscribe({
      next: () => {
        console.log('Report successfully sent');
        window.alert('Report sent successfully!');
      },
      error: (err) => {
        console.error('Error sending report:', err);
        const message =
          typeof err.error === 'string'
            ? err.error
            : 'Something went wrong';

        window.alert(message);
      }
    });
  }
  
}
