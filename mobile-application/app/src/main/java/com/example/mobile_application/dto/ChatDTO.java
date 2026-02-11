package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatDTO implements Serializable {
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
