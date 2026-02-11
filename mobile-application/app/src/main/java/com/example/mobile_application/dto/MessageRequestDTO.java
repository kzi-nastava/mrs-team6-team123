package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class MessageRequestDTO {
    @SerializedName("content")
    private String content;
    @SerializedName("senderId")
    private Long senderId;
    @SerializedName("chatId")
    private Long chatId;

    public MessageRequestDTO() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
