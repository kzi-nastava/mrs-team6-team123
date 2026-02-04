import { ChangeDetectorRef, Component } from '@angular/core';
import { NotificationResponse } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';
import { AuthService } from '../../services/auth.service';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProfileChangeModalComponent } from '../profile-change-modal/profile-change-modal';

@Component({
  selector: 'app-notification-panel',
  standalone: true,
  imports: [CommonModule, FormsModule, ProfileChangeModalComponent],
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
  showProfileChangeModal = false;
  profileChangeId: number | null = null;
  currentProfileChangeNotificationId: number | null = null;

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
    // Check if this is a profile change notification
    if (notification.title === 'Driver Profile Change Request' && notification.link) {
      this.profileChangeId = parseInt(notification.link);
      this.currentProfileChangeNotificationId = notification.notificationId;
      this.showProfileChangeModal = true;
    } else {
      // For other notifications, mark as read and navigate
      if (!notification.read) {
        this.markAsRead(notification);
      }
      if (notification.link) {
        this.router.navigateByUrl(notification.link);
      }
    }
  }

  closeProfileChangeModal(actionTaken: boolean = false): void {
    // Only mark as read if admin approved or declined
    if (actionTaken && this.currentProfileChangeNotificationId) {
      const notification = this.unreadNotifications.find(
        n => n.notificationId === this.currentProfileChangeNotificationId
      );
      if (notification) {
        this.notificationService.markAsRead(notification.notificationId).subscribe(() => {
          // Update local arrays immediately
          this.unreadNotifications = this.unreadNotifications.filter(
            n => n.notificationId !== notification.notificationId
          );
          this.readNotifications.unshift(notification);
          
          // Refresh unread notifications to update badge count
          this.notificationService.loadUnread(this.userId);
          
          this.cdr.detectChanges();
        });
      }
    }
    
    this.showProfileChangeModal = false;
    this.profileChangeId = null;
    this.currentProfileChangeNotificationId = null;
  }
}
