package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class MessageResponseDTO {
    @SerializedName("senderId")
    private Long senderId;
    @SerializedName("content")
    private String content;
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("mine")
    private boolean mine;

    public MessageResponseDTO() {
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }
}
