package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class NotificationDTO {
    @SerializedName("notificationId")
    private Long notificationId;
    @SerializedName("recipientId")
    private Long recipientId;
    @SerializedName("title")
    private String title;
    @SerializedName("message")
    private String message;
    @SerializedName("read")
    private boolean read;
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("link")
    private String link;

    public NotificationDTO() {
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
