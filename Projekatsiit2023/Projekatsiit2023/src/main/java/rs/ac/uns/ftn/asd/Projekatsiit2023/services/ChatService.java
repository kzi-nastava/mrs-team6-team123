package rs.ac.uns.ftn.asd.Projekatsiit2023.services;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.chat.ChatListResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.chat.ChatResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.chat.MessageRequestDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.dtos.chat.MessageResponseDTO;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.UserRole;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Chat;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.Message;
import rs.ac.uns.ftn.asd.Projekatsiit2023.models.User;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.ChatRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.MessageRepository;
import rs.ac.uns.ftn.asd.Projekatsiit2023.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChatService {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatService(
            MessageRepository messageRepository,
            ChatRepository chatRepository,
            UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public ChatResponseDTO getOrCreateUserChat(Long userId) {
        Optional<Chat> chat = chatRepository.findByUserId(userId);
        if (chat.isEmpty()) {
            Chat newChat = new Chat();
            User user = userRepository.findById(userId).orElseThrow();
            newChat.setUser(user);
            newChat.setAdmin(assignAdmin());
            chatRepository.save(newChat);
            return mapChatToResponseDTO(newChat);
        } else {
            return mapChatToResponseDTO(chat.get());
        }
    }

    public List<ChatListResponseDTO> getAdminChats(Long adminId) {
        List<Chat> chats = chatRepository.findByAdminId(adminId);
        List<ChatListResponseDTO> response = new ArrayList<>();
        for (Chat chat : chats) {
            response.add(mapChatToListResponseDTO(chat));
        }
        response.sort(Comparator.comparing(ChatListResponseDTO::getLastMessageTimestamp));
        return response;
    }

    private User assignAdmin() {
        List<Object[]> adminChatsCount = chatRepository.countChatsPerAdmin();
        if (adminChatsCount.isEmpty()) {
            return userRepository.findByUserRole(UserRole.ADMIN)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No admin found"));
        }
        Long adminId = (Long) adminChatsCount.get(0)[0];
        return userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
    }

    public List<MessageResponseDTO> getChatMessages(Long chatId, Long userId) {
        List<Message> messages = messageRepository.findByChatId(chatId);
        messages.sort(Comparator.comparing(Message::getTimestamp));
        List<MessageResponseDTO> response = new ArrayList<>();
        for (Message message : messages) {
            response.add(mapMessageToResponseDTO(message, userId));
        }
        return response;
    }

    public void sendMessage(MessageRequestDTO request) {
        Chat chat = chatRepository.findById(request.getChatId())
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));
        Message message = new Message();
        message.setChat(chat);
        message.setContent(request.getContent());
        message.setTimestamp(LocalDateTime.now());
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        message.setSentByUser(!sender.getUserRole().equals(UserRole.ADMIN));
        messageRepository.save(message);
    }

    private MessageResponseDTO mapMessageToResponseDTO(Message message, Long userId) {
        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setSenderId(
                message.isSentByUser()
                        ? message.getChat().getUser().getId()
                        : message.getChat().getAdmin().getId()
        );
        dto.setMine(dto.getSenderId().equals(userId));
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp().toString());
        return dto;
    }

    private ChatResponseDTO mapChatToResponseDTO(Chat chat) {
        ChatResponseDTO dto = new ChatResponseDTO();
        dto.setChatId(chat.getId());
        return dto;
    }

    private ChatListResponseDTO mapChatToListResponseDTO(Chat chat) {
        ChatListResponseDTO dto = new ChatListResponseDTO();
        dto.setChatId(chat.getId());
        dto.setUserId(chat.getUser().getId());
        dto.setUserName(chat.getUser().getFirstName() + " " + chat.getUser().getLastName());
        Message lastMessage = getLastMessage(chat);
        if (lastMessage != null) {
            dto.setLastMessage(lastMessage.getContent());
            dto.setLastMessageTimestamp(lastMessage.getTimestamp().toString());
        } else {
            dto.setLastMessage("");
            dto.setLastMessageTimestamp("");
        }
        return dto;
    }

    private Message getLastMessage(Chat chat) {
        List<Message> messages = messageRepository.findByChatId(chat.getId());
        messages.sort(Comparator.comparing(Message::getTimestamp).reversed());
        if (!messages.isEmpty()) {
            return messages.get(0);
        }
        return null;
    }
}
