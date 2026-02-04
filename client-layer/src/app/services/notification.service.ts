import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import { NotificationResponse } from "../models/notification.model";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../enviroment";

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private unread$ = new BehaviorSubject<NotificationResponse[]>([]);

  constructor(private http: HttpClient) {}

  loadUnread(userId: number) {
    this.http
      .get<NotificationResponse[]>(`${environment.apiUrl}/notifications/unread/${userId}`)
      .subscribe(n => this.unread$.next(n));
  }

  getUnread() {
    return this.unread$.asObservable();
  }

  getRead(userId: number) {
    return this.http
      .get<NotificationResponse[]>(`${environment.apiUrl}/notifications/read/${userId}`);
  }

  markAsRead(id: number) {
    return this.http.post(`${environment.apiUrl}/notifications/mark-read/${id}`, {});
  }
}