import { ChangeDetectorRef, Component } from '@angular/core';
import { NotificationResponse } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';
import { AuthService } from '../../services/auth.service';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-notification-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './notification-panel.html',
  styleUrls: ['./notification-panel.css'],
})
export class NotificationPanelComponent {
  activeTab: 'UNREAD' | 'READ' = 'UNREAD';

  unreadNotifications: NotificationResponse[] = [];
  readNotifications: NotificationResponse[] = [];

  private sub?: Subscription;
  private userId!: number;

  loading = false;

  constructor(
    private notificationService: NotificationService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.userId = this.authService.getCurrentUserId()!;
    this.loadUnread();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  switchTab(tab: 'UNREAD' | 'READ') {
    this.activeTab = tab;

    if (tab === 'UNREAD') {
      this.loadUnread();
    } else {
      this.loadRead();
    }
  }

  private loadUnread() {
    this.loading = true;

    this.notificationService.loadUnread(this.userId);

    this.sub = this.notificationService
      .getUnread()
      .subscribe((list: NotificationResponse[]) => {
        this.unreadNotifications = list;
        this.loading = false;
        this.cdr.markForCheck();
      });
  }

  private loadRead() {
    this.loading = true;

    this.notificationService
      .getRead(this.userId)
      .subscribe((list: NotificationResponse[]) => {
        this.readNotifications = list;
        this.loading = false;
        this.cdr.markForCheck();
      });
  }

  markAsRead(notification: NotificationResponse) {
    this.notificationService.markAsRead(notification.notificationId).subscribe(() => {
      this.unreadNotifications =
        this.unreadNotifications.filter(n => n.notificationId !== notification.notificationId);

      this.readNotifications.unshift(notification);
    });
    this.cdr.markForCheck();
  }

  onNotificationClick(notification: NotificationResponse) {
    if (!notification.read) {
      this.markAsRead(notification);
    }
    if (notification.link) {
      console.log(notification.link);
      this.router.navigateByUrl(notification.link);
    }
  }
}
