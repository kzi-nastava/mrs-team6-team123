package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.notification.GetNotificationResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Notification;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Ride;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.User;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.NotificationRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void sendNotification(Long userId, String title, String message, String link) {
        Notification notification = new Notification();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        notification.setRecipient(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setTimestamp(LocalDateTime.now());
        notification.setLink(link);
        notificationRepository.save(notification);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public List<GetNotificationResponseDTO> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByRecipientIdAndIsReadFalse(userId);
        return getResponse(notifications);
    }

    public List<GetNotificationResponseDTO> getReadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByRecipientIdAndIsReadTrue(userId);
        return getResponse(notifications);
    }

    private List<GetNotificationResponseDTO> getResponse(List<Notification> notifications) {
        List<GetNotificationResponseDTO> response = new ArrayList<>();
        for (Notification notification : notifications) {
            response.add(mapNotificationToResponseDTO(notification));
        }
        return response;
    }

    private GetNotificationResponseDTO mapNotificationToResponseDTO(Notification notification) {
        GetNotificationResponseDTO dto = new GetNotificationResponseDTO();
        dto.setNotificationId(notification.getId());
        dto.setRecipientId(notification.getRecipient().getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setTimestamp(notification.getTimestamp().toString());
        dto.setLink(notification.getLink());
        return dto;
    }
}