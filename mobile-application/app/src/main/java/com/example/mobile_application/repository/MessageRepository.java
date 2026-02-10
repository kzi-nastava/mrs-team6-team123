package com.example.mobile_application.repository;

import com.example.mobile_application.dto.MessageRequestDTO;
import com.example.mobile_application.dto.MessageResponseDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.MessageService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MessageRepository {
    private final MessageService service;

    public MessageRepository() {
        this.service = ApiClient.getInstance().create(MessageService.class);
    }

    public void getChatMessages(Long chatId, Long userId,
                                Callback<List<MessageResponseDTO>> callback) {
        Call<List<MessageResponseDTO>> call = service.getChatMessages(chatId, userId);
        call.enqueue(callback);
    }

    public void sendMessage(MessageRequestDTO message, Callback<Void> callback) {
        Call<Void> call = service.sendMessage(message);
        call.enqueue(callback);
    }
}
