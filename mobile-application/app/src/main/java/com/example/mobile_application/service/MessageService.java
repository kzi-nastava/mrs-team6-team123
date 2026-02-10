package com.example.mobile_application.service;

import com.example.mobile_application.dto.MessageRequestDTO;
import com.example.mobile_application.dto.MessageResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MessageService {
    @GET("api/messages/chat/{chatId}/{userId}")
    Call<List<MessageResponseDTO>> getChatMessages(
            @Path("chatId") Long chatId, @Path("userId") Long userId);

    @POST("api/messages/send")
    Call<Void> sendMessage(@Body MessageRequestDTO request);
}
