import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ReportRequest } from "../models/report.model";

@Injectable({
  providedIn: 'root'
})
export class ReportService {
    private baseUrl = 'http://localhost:8080/api/drivers';

    constructor(private http: HttpClient) {}

    reportDriver(report: ReportRequest) {
        console.log(report);
        return this.http.post(`${this.baseUrl}/report`, report);
    }
}