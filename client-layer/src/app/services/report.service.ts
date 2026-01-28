import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ReportRequest } from "../models/report.model";
import { environment } from "../../enviroment";

@Injectable({
  providedIn: 'root'
})
export class ReportService {
    private baseUrl = `${environment.apiUrl}/drivers`;

    constructor(private http: HttpClient) {}

    reportDriver(report: ReportRequest) {
        console.log(report);
        return this.http.post(`${this.baseUrl}/report`, report);
    }
}