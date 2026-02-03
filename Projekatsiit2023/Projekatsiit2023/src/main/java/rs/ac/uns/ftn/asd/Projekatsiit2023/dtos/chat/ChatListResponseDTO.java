package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatListResponseDTO {
    private Long chatId;
    private Long userId;
    private String userName;
    private String lastMessage;
    private String lastMessageTimestamp;
}
