package rs.ac.uns.ftn.asd.Projekatsiit2023.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.chat.ChatListResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.chat.ChatResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.services.ChatService;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/my/{userId}")
    public ResponseEntity<?> getMyChat(@PathVariable Long userId) {
        try {
            ChatResponseDTO response = chatService.getOrCreateUserChat(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e);
        }
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<?> getAdminChats(@PathVariable Long adminId) {
        try {
            List<ChatListResponseDTO> response = chatService.getAdminChats(adminId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e);
        }
    }
}
