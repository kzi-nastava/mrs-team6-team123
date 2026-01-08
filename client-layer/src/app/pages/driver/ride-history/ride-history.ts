import { Component } from '@angular/core';
import { RideHistoryFilterComponent } from '../../../components/ride-history/ride-history-filter/ride-history-filter';
import { RideHistoryTableComponent } from '../../../components/ride-history/ride-history-table/ride-history-table';

@Component({
  selector: 'app-ride-history',
  standalone: true,
  imports: [RideHistoryFilterComponent, RideHistoryTableComponent],
  templateUrl: './ride-history.html',
  styleUrls: ['./ride-history.css'],
})
export class RideHistoryComponent {
  columns = ['Date', 'From', 'To', 'Started at', 'Ended at', 'Canceled', 'PANIC', 'Price'];

  rides = [
    {
      Date: "01.01.01.",
      From: "ns",
      To: "bg",
      "Started at": "09",
      "Ended at": "11",
      Canceled: "-",
      PANIC: "-",
      Price: "100"
    },
    {
      Date: "02.01.01.",
      From: "ns",
      To: "su",
      "Started at": "79",
      "Ended at": "71",
      Canceled: "M",
      PANIC: "Y",
      Price: "0"
    }
  ]
}
