import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Pricing } from "../models/pricing";

@Injectable({
  providedIn: 'root'
})
export class PricingService {
    private baseUrl = 'http://localhost:8080/api/pricing';

    constructor(private http: HttpClient) {}

    getPricing(): Observable<Pricing[]> {
        return this.http.get<Pricing[]>(
            `${this.baseUrl}/get-pricing`
        );
    }

    changePricing(pricing: Pricing) {
        return this.http.post(`${this.baseUrl}/change-price`, pricing);
    }
}