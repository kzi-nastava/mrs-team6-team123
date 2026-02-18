package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class ChatListDTO {
    @SerializedName("chatId")
    private Long chatId;
    @SerializedName("userId")
    private Long userId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("lastMessage")
    private String lastMessage;
    @SerializedName("lastMessageTimestamp")
    private String lastMessageTimestamp;

    public ChatListDTO() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(String lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
}
