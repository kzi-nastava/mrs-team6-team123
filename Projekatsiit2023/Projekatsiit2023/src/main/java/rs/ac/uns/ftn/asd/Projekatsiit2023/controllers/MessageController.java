package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.chat.MessageRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.chat.MessageResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.ChatService;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final ChatService chatService;

    public MessageController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<?> getChatMessages(@PathVariable Long chatId) {
        try {
            List<MessageResponseDTO> response = chatService.getChatMessages(chatId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e);
        }
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequestDTO request) {
        try {
            chatService.sendMessage(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e);
        }

    }
}
