import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Pricing } from '../../models/pricing';
import { PricingService } from '../../services/pricing.service';

@Component({
  selector: 'app-admin-pricing',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-pricing.html',
  styleUrls: ['./admin-pricing.css'],
})
export class AdminPricingComponent {
  prices: Pricing[] = [];

  constructor(private pricingService: PricingService) {}

  ngOnInit(): void {
    this.getPricing();
  }

  getPricing() {
    this.pricingService
    .getPricing()
    .subscribe({
      next: (data) => {
        console.log('Pricing from backend:', data);
        this.prices = data;
      },
      error: (err) => {
        console.error('Error loading pricing', err);
      }
    })
  }

  confirmPrice(priceItem: Pricing) {
    this.pricingService
    .changePricing(priceItem)
    .subscribe({
      next: (response) => {
        console.log("Saved pricing", priceItem);
      },
      error: (err) => {
        console.error("Error saving pricing", err);
      }
      
    })
    priceItem.price = priceItem.newPrice;
  }
}
