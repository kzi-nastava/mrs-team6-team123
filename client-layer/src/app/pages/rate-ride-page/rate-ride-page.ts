import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { RateRideComponent } from '../../components/rate-ride/rate-ride';

@Component({
  selector: 'app-rate-ride-page',
  standalone: true,
  imports: [],
  templateUrl: './rate-ride-page.html',
  styleUrls: ['./rate-ride-page.css'],
})
export class RateRidePageComponent {
  constructor(
    private route: ActivatedRoute,
    private dialog: MatDialog,
    private router: Router
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const rideId = params['rideId'];

      if (!rideId) {
        this.router.navigate(['/']);
        return;
      }

      const dialogRef = this.dialog.open(RateRideComponent, {
        data: { rideId },
        disableClose: true
      });

      dialogRef.afterClosed().subscribe(() => {
        this.router.navigate(['/']);
      });
    });
  }
}
