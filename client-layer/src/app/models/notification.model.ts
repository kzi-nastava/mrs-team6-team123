export interface NotificationResponse {
    notificationId: number;
    recipientId: number;
    title: string;
    message: string;
    isRead: boolean;
    timestamp: string;
}