export interface NotificationResponse {
    notificationId: number;
    recipientId: number;
    title: string;
    message: string;
    read: boolean;
    timestamp: string;
    link?: string;
}