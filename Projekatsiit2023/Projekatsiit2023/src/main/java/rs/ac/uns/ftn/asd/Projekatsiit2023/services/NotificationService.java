package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNotification(Long userId, String title, String message) {
        System.out.println("📬 Notification sent to user " + userId);
        System.out.println("   Title: " + title);
        System.out.println("   Message: " + message);
    }
}