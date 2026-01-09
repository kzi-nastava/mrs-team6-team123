import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-pricing',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-pricing.html',
  styleUrls: ['./admin-pricing.css'],
})
export class AdminPricingComponent {
  prices = [
    {
      vehicleType: 'Standard',
      price: 500,
      newPrice: null
    },
    {
      vehicleType: 'Lux',
      price: 800,
      newPrice: null
    },
    {
      vehicleType: 'Van',
      price: 1000,
      newPrice: null
    }
  ];

  confirmPrice(priceItem: any) {
    if (priceItem.newPrice == null || priceItem.newPrice <= 0) {
      alert('Please enter a valid price');
      return;
    }
    
    priceItem.price = priceItem.newPrice;
    priceItem.newPrice = null;
  }
}
