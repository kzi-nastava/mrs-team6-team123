package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.notification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetNotificationResponseDTO {
    private Long notificationId;
    private Long recipientId;
    private String title;
    private String message;
    private boolean isRead;
    private String timestamp;
    private String link;
}
