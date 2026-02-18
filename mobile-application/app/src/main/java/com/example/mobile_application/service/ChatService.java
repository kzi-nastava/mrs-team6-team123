package com.example.mobile_application.service;

import com.example.mobile_application.dto.ChatDTO;
import com.example.mobile_application.dto.ChatListDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChatService {
    @GET("api/chats/my/{userId}")
    Call<ChatDTO> getMyChat(@Path("userId") Long userId);

    @GET("api/chats/admin/{adminId}")
    Call<List<ChatListDTO>> getAdminChats(@Path("adminId") Long adminId);
}
