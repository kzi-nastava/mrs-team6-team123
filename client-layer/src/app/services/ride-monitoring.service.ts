import { Injectable } from "@angular/core";
import { environment } from "../../enviroment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { RideMonitoringResponse } from "../models/ride-monitoring.model";

@Injectable({ providedIn: 'root' })
export class RideMonitoringService {
    private baseUrl = `${environment.apiUrl}/rides/monitoring`;

    constructor(private http: HttpClient) {}

    getActiveRides(): Observable<RideMonitoringResponse[]> {
        return this.http.get<RideMonitoringResponse[]>(`${this.baseUrl}/active`);
    }
}