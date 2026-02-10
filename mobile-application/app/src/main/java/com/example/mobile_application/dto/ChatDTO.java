package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class ChatDTO {
    @SerializedName("chatId")
    private Long chatId;

    public ChatDTO() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
