package rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageRequestDTO {
    private String content;
    private Long senderId;
    private Long chatId;
}
