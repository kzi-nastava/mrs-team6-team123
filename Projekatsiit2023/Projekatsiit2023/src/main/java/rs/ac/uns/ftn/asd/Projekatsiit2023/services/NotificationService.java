package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    // TODO: Implementirati pravo slanje notifikacija (WebSocket, Push, Email, itd.)
    public void sendNotification(Long userId, String title, String message) {
        System.out.println("📬 Notification sent to user " + userId);
        System.out.println("   Title: " + title);
        System.out.println("   Message: " + message);
        
        // Ovde možeš dodati:
        // - WebSocket notifikacije za real-time
        // - Push notifikacije za mobilne
        // - Email notifikacije
        // - Čuvanje u bazi za istoriju
    }
}