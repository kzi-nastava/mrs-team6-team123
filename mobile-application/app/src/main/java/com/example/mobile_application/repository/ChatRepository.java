package com.example.mobile_application.repository;

import com.example.mobile_application.dto.ChatDTO;
import com.example.mobile_application.dto.ChatListDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.ChatService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ChatRepository {
    private final ChatService service;

    public ChatRepository() {
        this.service = ApiClient.getInstance().create(ChatService.class);
    }

    public void getMyChat(Long userId, Callback<ChatDTO> callback) {
        Call<ChatDTO> call = service.getMyChat(userId);
        call.enqueue(callback);
    }

    public void getAdminChats(Long adminId, Callback<List<ChatListDTO>> callback) {
        Call<List<ChatListDTO>> call = service.getAdminChats(adminId);
        call.enqueue(callback);
    }
}
