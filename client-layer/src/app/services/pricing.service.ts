import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Pricing } from "../models/pricing.model";
import { environment } from "../../enviroment";

@Injectable({
  providedIn: 'root'
})
export class PricingService {
    private baseUrl = `${environment.apiUrl}/pricing`;

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